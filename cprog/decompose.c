
/* decompose.c, Wim H. Hesselink, June 2011 */

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include <assert.h>
#include "auxil.h"
#include "histogram.h"
#include "arraylist.h"
#include "readKekule.h"
#include "scanner.h"
#include "cells.h"

int main(int argc, char *argv[]) { 
  /* Input CellsYonZ.  
   * Determines whether cells are prime or original. */
  Intstack cell;
  int rank, nr = 0;
  assert(argc > 1);
  rank = atoi(argv[1]); 
  printf("Decomposing translated cells of rank %d:\n", rank);
  setRank(rank);
  cell = readCell();
  while (cell) {
    nr = getCnr();
    printCell(nr, cell);
    printf(isOriginal(cell) ? "is original.\n" : "\n");
    freestack(cell);
    cell = readCell();
  }
  reportTime();
#if 1
  reportCnt();
  reportACnt();
#endif
  finalizeScanner();
  return 0;
}
