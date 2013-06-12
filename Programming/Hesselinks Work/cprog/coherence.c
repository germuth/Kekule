
/* coherence.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <stdlib.h>
#include "auxil.h" 
#include "cells.h" 

int isChan(int x) {
  int cnt = 0;
  while (x > 0 && cnt < 3) {
    cnt += x % 2;
    x /= 2;
  }
  return (cnt == 2);
}

Intstack chFrom[32];
/* is filled as needed, because isCoherent may be false */

Intstack chansFrom(int p, Intstack cell) {
  /* yields the sorted list of channels c with 
   * cell->it[p] ^ c in cell. */
  int i, c ;
  Intstack a;
  if (chFrom[p]) return chFrom[p];
  a = newIntstack(cell->size, NULL) ;
  for (i = 0 ; i < cell->size ; i++) {
    c = cell->it[p] ^ cell->it[i] ;
    if (isChan(c)) putint(c, a) ;
  }
  qusort(a->it, 0, a->size) ;
  chFrom[p] = a;
  return a;
}

Arraylist chanDiv(int k, Intstack set) {
  /* All elements of set are subsets of bitstring k.
   * Give all ways to write bitstring k as a disjoint union
   * of different elements of set; destroy set */
  int ch ;
  Arraylist result, ar ;
  Intstack s2 ;
  if (set->size == 0) {
    freestack(set) ;
    result = newArraylist(k) ;
    if (k == 0) putItem(newIntstack(5, NULL), result) ;
    return result ;
  }
  ch = popint(set) ;
  s2 = newIntstack(0, set) ;
  result = chanDiv(k, s2) ; /* ch is not used here */
  k ^= ch ;  
  filtersub(k, set) ;
  ar = chanDiv(k, set) ;
  mapiar(ar, ch, (void(*)(int, void*)) putint) ;
  concatAr(result, ar) ;
  return result ;
}

int isfan(int p, Intstack fan, Intstack cell) {
  /* Pre: p in cell. Preserves both fan and cell. 
   * For every subset D of fan, verify that p^(^D) in cell. */
  int x, b;
  if (fan->size == 0) return 1;
  x = popint(fan);
  b = isfan(p, fan, cell) 
    && member(p ^ x, cell) && isfan(p ^ x, fan, cell);
  fan->size++;
  return b;
}

int channelConnect(int p, int q, Intstack cell) {
  /* pre: p < q < cell->size with distance > 2.
   * Verify that p and q are channel connected. */
  Intstack chs1 ;
  Arraylist li ;
  int b, i, pp = cell->it[p], dif = pp ^ cell->it[q],
    cn = cntBits(dif);
  if (cn <= 2) return 1;
  chs1 = intersection(chansFrom(p, cell), chansFrom(q, cell));
  filtersub(dif, chs1);
#if 1
  if (2 * chs1->size < cn) {
    freestack(chs1);
    return 0;
  }
#endif
  li = chanDiv(dif, chs1);
  i = 0;
  /* If there are never more than 3 disjoint channels (i.e. rank < 8),
   * isfan holds because of the construction of chs1. 
   * Therefore next loop is superfluous. We only need li nonempty. */
#if 1
  while (i < li->size && !isfan(pp, li->it[i], cell)) i++;
#endif
  b = (i < li->size);
  for (i = 0 ; i < li->size ; i++) {
    freestack(li->it[i]) ;
  }
  freeArraylist(li) ;
  return b ;
}

int isCoherent(Intstack cell) {
  int p, q, b = 1;
  if (cell->size < 2) return 1;
  for (p = 0 ; b && p < cell->size ; p++) {
    for (q = p+1 ; b && q < cell->size ; q++) {
      b = channelConnect(p, q, cell);
    }
  }
  for (p = 0 ; p < cell->size ; p++) {
    freestack(chFrom[p]);
    chFrom[p] = NULL;
  }
  return b;
}
