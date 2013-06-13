/* clfmain.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "cells.h"
#include "arraylist.h"
#include "scanner.h"
#include "histogram.h"
#include "permutations.h"
#include "readKekule.h"
#include "classify.h"

int main(int argc, char *argv[]) { /* Give the classification */
	int rank, option = 0;
	Arraylist ar;
	assert(argc> 1);
	rank = atoi(argv[1]);
	if (argc > 2)
		option = atoi(argv[2]);
	if (option == 2) {
		ar = readCellList();
		setRank(rank);
	} 
	else {
		ar = classify(rank, option);
	}
	if (option != 1) {
		sortAndWeed(rank, ar);
		freePerm();
		printCellList(ar);
#if 0
		printDoubletons(ar);
#endif
		freeStackList(ar);
	}
	if (option == 2)
		finalizeScanner();
	printf("\n");
	reportTime();
#if 0
	reportACnt();
	reportCnt();
#endif
	return 0;
}
