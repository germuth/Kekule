
/* arraylist.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "arraylist.h"

static int cnt = 0; /* Number of arraylists created minus deleted */
static int cnt0 = 0;

void reportACnt() {
  printf("Arraylist difference %d.\n", cnt - cnt0);
  cnt0 = cnt;
}

Arraylist newArraylist(int upb) {
  Arraylist result = malloc(sizeof(struct alist));
  assert(result != NULL);
  result->upb = upb;
  result->size = 0;
  result->it = calloc(upb, sizeof(void*));
  assert(result->it != NULL);
  cnt++;
  return result;
}

void freeArraylist(Arraylist a) {
  if (a){
    /* first, free array a->it */
    cnt--;
    free(a->it);
    free(a);
  }
}

Arraylist putItem(void *x, Arraylist a) {
  void* *b;
  if (a->size == a->upb) {
    a->upb = 2 * a->upb + 1;
    b = realloc(a->it, (a->upb) * sizeof(void*));
    assert(b != NULL);  
    a->it = b;
  }
  a->it[a->size] = x;
  a->size++;
  return a;
}

void concatAr(Arraylist result, Arraylist ar) {
  int i;
  for (i = 0; i < ar->size; i++) {
    putItem(ar->it[i], result);
  }
  freeArraylist(ar);
}

void mapar(Arraylist a, void(*f)(void*)) {
  int i;
  for (i = 0; i < a->size; i++) {
    (*f)(a->it[i]);
  }
}

void mapiar(Arraylist a, int x, void(*f)(int, void*)) {
  int i;
  for (i = 0; i < a->size; i++) {
    (*f)(x, a->it[i]);
  }
}

void swapar(void* *a, int p, int q) {
  void *x = a[p];
  a[p] = a[q];
  a[q] = x;
}

/* quicksort, sorts a[p..q-1] */
void qasort(void* *a, int p, int q, int(*comp)(const void*, const void*)) {
  int r = p+1, s = q;
  if (q <= p+1) /* empty or singleton */
    return;
  swapar(a, p, (p+q)/2);
  /* invariant p < r && a[p+1..r) <= a[p] <= a[s..q) */
  while (r < s) {
    if ((*comp)(a[r], a[p]) <= 0) r++;
    else swapar(a, r, --s);
  } /* p < r == s <= q */
  swapar(a, p, r-1);
  qasort(a, p, r-1, comp);
  qasort(a, r, q, comp);
}

void asort(Arraylist a, int(*comp)(const void*, const void*)) {
  qasort(a->it, 0, a->size, comp); 
}

/* Sort and delete duplicates */
void sortDD(Arraylist a, int(*comp)(const void*, const void*),
	    void(*delete)(void*) ) {
  int i = 0, j;
  asort(a, comp);
  while (i+1 < a->size && (*comp)(a->it[i], a->it[i+1]) != 0) i++;
  if (i+1 < a->size) {
    j = i+2; (*delete)(a->it[i+1]);
    /* [0..i] has no duplicates, [i+1..j-1] are superfluous */
    while (j < a->size) {
      if ((*comp)(a->it[i], a->it[j]) == 0) {
	(*delete)(a->it[j]); 
	j++;
      } else {
	i++;
	a->it[i] = a->it[j]; 
	j++;
      }
    }
    a->size = i+1;
  }
}

/* Pre: ar is comp-sorted */
int memberAr(void *item, Arraylist ar, 
	     int(*comp)(const void*, const void*)) {
  int p = 0, q = ar->size, m;
  /* Invariant: item is not outside ar[p..q) */
  while (p+1 < q) {
    m = (p + q) / 2;
    if ((*comp)(item, ar->it[m]) < 0) q = m;
    else p = m;
  }
  return ((*comp)(item, ar->it[p]) == 0);
}

void mergeAr(Arraylist result, Arraylist ar, 
	     int(*comp)(const void*, const void*)) {
  /* result gets the union, ar is destroyed, 
   * no freeing for the intersection! */
  Arraylist tmp = newArraylist(result->size + ar->size);
  int i = 0, j = 0, cp;
  while (i < result->size && j < ar->size) {
    cp = (*comp)(result->it[i], ar->it[j]);
    if (cp <= 0) putItem(result->it[i++], tmp);
    else putItem(ar->it[j++], tmp);
    if (cp == 0) j++;
  }
  free(ar);
  result->upb = tmp->upb;
  result->size = tmp->size;
  result->it = tmp->it;
  free(tmp);
  cnt -= 2;
}

void removeNulls(Arraylist ar) {
  int i = 0, j;
  while (i < ar->size && ar->it[i] != NULL) i++;
  if (i < ar->size) { /* Nulls to be removed */
    j = i+1;
    /* Invariant it[0..i) has no NULLs, i < j, it[i..j) is superfluous */
    while (j < ar->size) {
      while (j < ar->size && ar->it[j] == NULL) j++;
      if (j < ar->size) {
	ar->it[i] = ar->it[j];
	i++; j++;
      }
    }
    ar->size = i;
  }
}
