/* classGraph.c , Wim H. Hesselink, August 2010
 * Classify all graphs on rank vertices, up to isomorphism */

/* We treat graphs here as cells with all PAs of size 2 */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "histogram.h"
#include "arraylist.h"
#include "graph.h"
#include "classify.h"

#define UPB 11

Intstack fullgraph(int rank) {
	Intstack result = newIntstack(rank * (rank-1) / 2, NULL);
	int p, q = 2, qlim = 1 << rank;
	for (q = 2; q < qlim; q <<= 1)
		for (p = 1; p < q; p <<= 1)
			putint(p+q, result);
	assert(result->size == rank * (rank-1) / 2);
	return result;
}

Arraylist allGraphs(int rank) {
	Arraylist result = newArraylist(rank);
	Intstack cand = newIntstack(0, NULL), fg = fullgraph(rank);
	unsigned long int x, y, ulim = 1 << fg->size;
	int i;
	setRank(rank);
	for (x = 0L; x < ulim; x++) {
		cand->size = 0;
		y = x;
		i = 0;
		while (y > 0) {
			if (y % 2 == 1)
				putint(fg->it[i], cand);
			y /= 2;
			i++;
		}
		if (portHistDescending(cand)) {
			putItem((void*) newIntstack(0, cand), result);
		}
	}
	freestack(cand);
	freestack(fg);
	sortAndWeedForGraphs(rank, result);
	return result;
}

int matchedR(int cover, Intstack gr) {
	int ed, size;
	if (cover == 0)
		return 1;
	if (gr->size == 0)
		return 0;
	ed = popint(gr);
	size = gr->size;
	if (matchedR(cover, gr))
		return 1;
	gr->size = size;
	if ((ed | cover) != cover)
		return 0;
	cover = cover ^ ed;
	return matchedR(cover, gr);
}

int matched(int rank, Intstack gr) {
	Intstack grc = newIntstack(0, gr);
	int r = matchedR((1 << rank) - 1, grc);
	freestack(grc);
	return r;
}

Arraylist allMatchedGraphs(int rank) {
	Arraylist ar = allGraphs(rank);
	int i;
	for (i = 0; i < ar->size; i++) {
		if (!matched(rank, ar->it[i])) {
			freestack(ar->it[i]);
			ar->it[i] = NULL;
		}
	}
	removeNulls(ar);
	return ar;
}

static Arraylist aMG[UPB];
/* aMG[2*i] shall hold the isomorphism classes of matched graphs of rank 2*i.
 * aMG[2*i-1] can hold the isomorphism classes of nonmatched graphs 
 * of rank 2*i. */

static int aMGisMade = 0;

void freeAMG() {
	int i;
	for (i = 0; i < UPB; i++) {
		freeStackList(aMG[i]);
	}
}

void setAMG(int k) {
	int i, j;
	Arraylist ar;
	for (i = 0; i < UPB; i++)
		aMG[i] = NULL;
	assert(k < UPB && k%2 == 0);
	for (i = 2; i <= k; i += 2) {
		aMG[i] = allMatchedGraphs(i);
#if 0
		printf("SetAMG finds %d matched graphs for %d nodes.\n",
				aMG[i]->size, i);
#endif
	}
	aMGisMade = 1;
}

Arraylist getAMG(int k) {
	assert(aMGisMade);
	assert(k < UPB && aMG[k] != NULL);
	return aMG[k];
}
