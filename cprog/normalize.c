
/* normalize.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "arraylist.h"
#include "cells.h"
#include "histogram.h"
#include "permutations.h"
#include "readKekule.h"

int main(int argc, char *argv[]) {  
  Intstack cell, nc ;
  int nr = 0, rank, pa ;
  printf("From normalize.c.\n") ;
  assert(argc > 1, "Give rank (integer)");
  rank = atoi(argv[1]); 
  setRank(rank) ;
  cell = readCell() ;
  while (cell) {
    nc = newIntstack(0, cell) ;
    toCenter(nc, &pa) ; /* see histogram.c */
    printCell(nr, cell) ;
    if (pa) {
      printf("translated over '");
      printPA(pa);
      printf("' and centralized, gives:\n");
    }
    nc = firstVariant(rank, nc) ; /* see permutations.c */
    nr = getCnr() ; /* see readKekule.c */
    printCell(nr, nc) ;
    freestack(nc) ;
    freestack(cell) ;
    cell = readCell() ;
  }
  reportTime() ;
  return 0 ;
}
