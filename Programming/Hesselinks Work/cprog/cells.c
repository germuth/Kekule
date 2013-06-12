
/* cells.c, Wim H. Hesselink, June 2011 */
/* A cell is a set of port assignments; 
 * the port assignments are represented as bit vectors (integers); 
 * a set of them is represented as an ordered intstack.
 * See /usr/include/limits.h
 */

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <assert.h>
#include "auxil.h"
#include "histogram.h"
#include "arraylist.h"
#include "coherence.h"
#include "mkCell.h"
#include "cells.h"

void printPA(int x) {
  int k;
  if (x) {
    while (x) {
      k = firstbit(x);
      x ^= (1 << k);
      printf("%c", 'a'+k);
    }
  } else printf("0");
}

Intstack even(int r) {
  Intstack result = newIntstack(1 << (r-1), NULL);
  int i;
  for (i = 0; i < (1 << r); i++) {
    if (cntBits(i) % 2 == 0) {
      putint(i, result);
    }
  }
  assert(result->size == (r == 0 ? 1 : 1 << (r-1)));
  return result;
}

void translate(int x, Intstack a) {
  /* Translates cell a over port assignement x
   * Destroys a */
  int i;
  if (x == 0) return;
  for (i = 0; i < a->size; i++) {
    a->it[i] = x ^ a->it[i];
  }
  qusort(a->it, 0, a->size);
}

int isFlexible(int ports, Intstack cell) {
  /* Pre: 0 in cell */
  int i, upb = 0;
  for (i = 0; i < cell->size; i++) {
    upb |= cell->it[i];
  }
  return (upb == ports);
}

/* result := a odot b */
void odot(Intstack a, Intstack b, Intstack result) {
  int i, j, x, y;
  result->size = 0;
  for (i = 0; i < a->size; i++) {
    x = a->it[i];
    for (j = 0; j < b->size; j++) {
      y = b->it[j];
      if ((x & y) == 0) putint(x | y, result);
    }
  }
  isortDD(result);
}

/* K//L = {x in K | all y in L: x&y = 0 => x|y in K };
 * result is the greatest M sub K with (M odot L) sub K
 * Pre: 0 in L=b sub K=a
 * Post result = a//b. */
void ofactor(Intstack a, Intstack b, Intstack result) {
  int i, j, k, x, acc;
  result->size = 0;
  putint(0, result);
  for (i = 1; i < a->size; i++) {
    x = a->it[i];
    acc = 1;
    j = 1; 
    k = i+1;
    while (acc && j < b->size) {
      if ((x & b->it[j]) != 0) {
	j++;
      } else if (k == a->size || x + b->it[j] < a->it[k]) {
	acc = 0; /* x is not acceptable */
      } else if (a->it[k] < x + b->it[j]) {
	k++;
      } else { 
	j++;
	k++;
      } 
    }
    if (acc) putint(x, result);
  }
}

unsigned long long int toBits(Intstack cell){
  unsigned long long int xx = 0ULL;
  int i;
  for (i = 0 ; i < cell->size ; i++){
    xx = xx | 1ULL << cell->it[i];
  }
  return xx;
}

unsigned long long int toplus(Intstack a, Intstack b){
  unsigned long long int xx = 0ULL;
  int i, j;
  for (i = 0 ; i < a->size ; i++)
    for (j = 0 ; j < b->size ; j++){
      if ((a->it[i] & b->it[j]) == 0)
	xx = xx | (1ULL << (a->it[i] ^ b->it[j]));
    }
  return xx;
}

int hasFactor(Intstack cell, Intstack a, Intstack klad) {
  ofactor(cell, a, klad);
  return (toBits(cell) & ~toplus(a, klad)) == 0;
}

int indecomposable(Intstack cell, Intstack part1, Intstack part2) {
  unsigned long int ulim = 1UL << (cell->size - 1), x, y;
  unsigned long long int xx = 0ULL;
  Intstack cand = newIntstack(5, NULL), 
    ncell = newIntstack(cell->size, NULL);
  Arraylist ar = newArraylist(cell->size);
  int i, r = 1, rank = 0, fract;
  xx = toBits(cell);
  putint(0, cand);
  for (x = 1L; r && x < ulim - 1; x++) {
    cand->size = 1;
    y = x; 
    i = 0;
    while (y > 0) {
      if (y % 2 == 1) putint(cell->it[i+1], cand);
      y /= 2;
      i++;
    }
    if (isCoherent(cand) 
	&& hasFactor(cell, cand, ncell) ) {
      putItem((void*) newIntstack(0, cand), ar);
      fract = (cell->size - 1) / cand->size;
      i = ar->size;
      while (r && (i > 0)) {
	i--;
	if (((Intstack)(ar->it[i]))->size > fract) {
	  if (subset(ar->it[i], ncell)) {
	    if (toplus(cand, ar->it[i]) == xx) r = 0;
	  }
	} 
      }
    }
  }
  if (r == 0) {
    cpIntstack(cand, part1);
    cpIntstack(ar->it[i], part2);
    odot(cand, ar->it[i], ncell);
    assert(equalIntstack(cell, ncell));
  }
  freestack(ncell);
  freestack(cand);
  freeStackList(ar);
  return r;
}

int isOriginal(Intstack cell) {
  int i = 0, nf = 1;
  Intstack p1 = newIntstack(cell->size, NULL), 
    p2 = newIntstack(cell->size, NULL), 
    other = newIntstack(0, cell);
  while (nf && i < cell->size) {
    cpIntstack(cell, other);
    translate(cell->it[i], other);
    if (! indecomposable(other, p1, p2)) {
      nf = 0;
      if (cell->it[i]) {
	printf("Cell is prime. Translating over '");
	printPA(cell->it[i]);
	printf("' gives decomposition:\n");
      } else {
	printf("Cell has decomposition:\n");
      }
      printCell(0, p1); 
      printCell(0, p2); 
      odot(p1, p2, other);
      translate(cell->it[i], other);
      assert(equalIntstack(other, cell));
    }
    i++;
  }
  freestack(other);
  freestack(p1);
  freestack(p2);
  return nf;
}

#define LATEX 0

void printCell(int nr, Intstack a) {
  int i;
  if (a == NULL) {
    printf("NULL\n");
    return;
  }
#if LATEX
  printf(" Size %3d ", a->size);
#endif
  if (nr >= 0) {
    if (nr == 0) printf("[");
    else printf("[%3d:", nr);
    printHisto(a);
#if 0
    printf("\t: ");
    printWeightlist(a);
#endif
    printf("] ");
  }
  for (i = 0; i < a->size; i++) {
#if LATEX
    printf(", \\S ");
#else
    printf(" ");
#endif
    printPA(a->it[i]);
#if LATEX
    printf("/");
#endif
  }
  printf(".\n");
}

void printCellList(Arraylist ar) {
  int  i;
  for (i = 0; i < ar->size; i++) printCell(i+1, ar->it[i]);
}

void printDistances(Intstack a) {
  int i, j;
  for (i = 0; i < a->size; i++) {
    for (j = 0; j < a->size; j++) {
      printf(" %2d", cntBits(a->it[i] ^ a->it[j]));
    }
    printf("\n");
  }
}
