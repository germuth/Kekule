
/* complementcell.c, Wim H. Hesselink, June 2011 */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "scanner.h"
#include "readKekule.h"
#include "cells.h"

void complement(int upb, Intstack cell, Intstack result) {
  int i = 0, j = 0;
  result->size = 0;
  /* invariant: j == cell->size || i <= cell->it[j] */
  while (i < upb) {
    if (j == cell->size || i < cell->it[j]) {
      if (cntBits(i) % 2 == 0) putint(i, result); 
      i++;
    } else {
      i++;
      j++;
    }
  }
}

int main(int argc, char *argv[]) {  
  Intstack cell, co;
  int rank, upb, nr = 0;
  assert(argc > 1);
  rank = atoi(argv[1]);
  upb = 1 << rank;
  co = newIntstack(upb, NULL);
  cell = readCell();
  while (cell) {
    nr = getCnr();
    printCell(nr, cell);
    complement(upb, cell, co);
    printf("COM ");
    printCell(0, co);
    freestack(cell);
    printf("\n");
    cell = readCell();
  }
  freestack(cell);
  freestack(co);
#if 0
  reportTime();
  reportCnt();
#endif
  finalizeScanner();
  return 0;
}

