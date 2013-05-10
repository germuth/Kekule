
/* auxil.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <assert.h>
#include "auxil.h"

void reportTime() {
  printf("Done in %d seconds.\n", 
	 (int)(((unsigned long int) clock()) / CLOCKS_PER_SEC) );
}

static int cnt = 0; /* Number of intstacks created minus deleted */
static int cnt0 = 0;

void reportCnt() {
  printf("Intstack difference %d.\n", cnt - cnt0);
  cnt0 = cnt;
}

/* Make copy of a->it[0..n) in b->it */
void cpIntstack(Intstack a, Intstack b) {
  int i, n = a->size;
  assert(n <= b->upb);
  for (i = 0; i < n; i++) b->it[i] = a->it[i];
  b->size = n;
}

/* Make copy of a in new space of at least upb */
Intstack newIntstack(int upb, Intstack a) {
  int n;
  Intstack result = malloc(sizeof(struct istack));
  assert(result != NULL);
  n = (a == NULL ? 0 : a->size);
  if (upb < n) upb = n;
  result->upb = upb;
  result->it = calloc(upb, sizeof(int));
  assert(result->it != NULL);
  if (a) cpIntstack(a, result);
  else result->size = 0;
  cnt ++;
  return result;
}

void freestack(Intstack a) {
  if (a) {
    assert(cnt > 0);
    cnt--;
    free(a->it);
    free(a);
  }
}

void freeStackList(Arraylist ar) {
  /* Pre: ar == NULL or all ar->it[i] are Intstacks */
  int i;
  if (! ar) return;
  for (i = 0; i < ar->size; i++) 
    freestack(ar->it[i]);
  freeArraylist(ar);
}

/* Make {0..n-1} */
Intstack below(int n) {
  int i;
  Intstack result = newIntstack(n, NULL);
  result->size = n;
  for (i = 0; i < n; i++) result->it[i] = i;
  return result;
}

Intstack putint(int x, Intstack a){
  int* b;
  if (a->size == a->upb) {
    a->upb = 2 * a->upb + 1;
    b = realloc(a->it, (a->upb) * sizeof(int));
    assert(b != NULL);
    a->it = b;
  }
  a->it[a->size] = x;
  a->size++;
  return a;
}

int popint(Intstack a) {
  int result;
  assert(a->size > 0);
  a->size--;
  result = a->it[a->size];
  return result;
}

int equalIntstack(Intstack a, Intstack b) {
  int i;
  if (a->size != b->size) return 0;
  for (i = 0; i < a->size; i++) {
    if (a->it[i] != b->it[i]) return 0;
  }
  return 1;
}

int member(int x, Intstack a) {
  /* Pre: a->it is sorted */
  int p = 0, q = a->size, m;
  if (q == 0 || x < a->it[0]) return 0;
  /* inv. a[p] <= x < a[q], where a[size] == infty */
  while (p+1 < q) {
    m = (p+q) / 2;
    if (x < a->it[m]) q = m;
    else p = m;
  }
  return (x == a->it[p]);
}

int subset(Intstack a, Intstack b) {
  /* Pre: a->it and b->it are ascending */
  int p = 0, q = 0;
  /* inv. a[0..p) sub b[0..q) and ... */
  while (p < a->size && q < b->size) {
    if (a->it[p] < b->it[q]) {
      return 0;
    } else if (a->it[p] == b->it[q]) {
      p++; 
    } else {
      q++;
    }
  }
  return p == a->size;
}

void intunion(Intstack a, Intstack b) {
  /* Pre: a and b are increasing;
   * a becomes the union, b is destroyed */
  int i = 0, j = 0, k = 0;
  int* c;
  a->upb = a->size + b->size;
  c = calloc(a->upb, sizeof(int));
  assert(c != NULL);
  while (i < a->size && j < b->size) {
    if (a->it[i] < b->it[j]) {
      c[k++] = a->it[i++];
    } else if (a->it[i] > b->it[j]) {
      c[k++] = b->it[j++]; 
    } else {
      c[k++] = a->it[i++]; j++;
    }
  }
  while (i < a->size) c[k++] = a->it[i++];
  while (j < b->size) c[k++] = b->it[j++];
  a->size = k;
  free(a->it);
  a->it = c;
  freestack(b);
}

void intersect(Intstack a, Intstack b) {
  /* Pre: a and b are increasing;
   * a becomes the intersection, b is destroyed */
  int i = 0, j = 0, k = 0;
  int* c;
  a->upb = a->size;
  c = calloc(a->upb, sizeof(int));
  assert(c != NULL);
  while (i < a->size && j < b->size) {
    if (a->it[i] < b->it[j]) {
      i++;
    } else if (a->it[i] > b->it[j]) {
      j++;
    } else {
      c[k++] = a->it[i++]; j++;
    }
  }
  a->size = k;
  free(a->it);
  a->it = c;
  freestack(b);
}

Intstack intersection(Intstack a, Intstack b) {
  /* Pre: a and b are increasing */
  int i = 0, j = 0, k = 0;
  Intstack result = newIntstack(a->size, NULL);
  while (i < a->size && j < b->size) {
    if (a->it[i] < b->it[j]) {
      i++;
    } else if (a->it[i] > b->it[j]) {
      j++;
    } else {
      putint(a->it[i++], result); 
      j++;
    }
  }
  return result;
}

/* Retain all bit strings of a contained in upb */
void filtersub(int upb, Intstack a) {
  int i, k = 0;
  for (i = 0; i < a->size; i++) {
    if ((a->it[i] | upb) == upb) a->it[k++] = a->it[i];
  }
  a->size = k;
}

void swap(int* a, int p, int q) {
  int x = a[p];
  a[p] = a[q];
  a[q] = x;
}

/* quicksort, sorts a[p..q-1] */
void qusort(int* a, int p, int q) {
  int r = p+1, s = q;
  if (q <= p+1) /* empty or singleton */
    return;
  swap(a, p, (p+q)/2);
  /* invariant p < r && a[p+1..r) <= a[p] <= a[s..q) */
  while (r < s) {
    if (a[r] <= a[p]) r++;
    else swap(a, r, --s);
  } /* p < r == s <= q */
  swap(a, p, r-1);
  qusort(a, p, r-1);
  qusort(a, r, q);
}

void isortDD(Intstack a) {
  /* sort with duplicate deletion */
  int i = 0 , j;
  qusort(a->it, 0, a->size);
  while (i + 1 < a->size && a->it[i] < a->it[i+1]) i++;
  if (i+1 < a->size) {
    j = i+2;
    while (j < a->size) {
      if (a->it[i] == a->it[j]) {
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

/* Return number of bits in bit string x */
int cntBits(int x) {
  int result = 0;
  while (x > 0) {
    result += x % 2;
    x /= 2;
  }
  return result;
}

/* Pre x > 0, return smallest k with (x>>k) & 1 > 0 */
int firstbit(int x) {
  int k = 0;
  while (((x>>k) & 1 ) == 0) k++;
  return k;
}

/* Pre x > 0, return smallest power k of 2 with x & k > 0 */
int firstspike(int x) {
  int k = 1;
  if (x == 0) return 0;
  while ((x&k)  == 0) k *= 2;
  return k;
}

void printar(Intstack a) {
  int i;
  printf("%3d", a->it[0]);
  for (i = 1; i < a->size; i++) printf(" %3d", a->it[i]);
}

void printarn(Intstack a) {
  printar(a);
  printf("\n");
}

int bitUnion(Intstack a){
  int x = 0, i;
  for (i = 0 ; i < a->size ; i++){
    x |= a->it[i];
  }
  return x;
}

int twolog(int x){ /* x < 2^twolog */
  int result = 0;
  while (x != 0) {
    x >>= 1;
    result++;
  }
  return result;
}
