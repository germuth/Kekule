/* fdGraph.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <assert.h>
#include "auxil.h"
#include "scanner.h"
#include "graph.h"
#include "cells.h"
#include "mkCell.h"
#include "histogram.h"
#include "permutations.h"
#include "classGraph.h"
#include "classify.h"
#include "readKekule.h"

/**

[  1: 12  3  3  3  3]  0 ab ac ad ae.
[  2: 16 13 13  8  8]  0 bc ad abcd ae abce.
[  3:  9  9  6  6  6]  0 ac bc ad bd ae be.
[  4: 12 12  6  6  6]  0 ab ac bc ad bd ae be.
[  5: 19 16 13 11 11]  0 ac ad bd abcd ae be abce.
[  6: 18 18 16 16 16]  0 ab cd abcd ce abce de abde.
[  7: 12  9  9  9  9]  0 ab ac ad bd cd ae be ce.
[  8: 19 19 16 11 11]  0 ac bc ad bd abcd ae be abce.
[  9: 12 12 12  9  9]  0 ab ac bc ad bd cd ae be ce.
[ 10: 19 19 16 14 14]  0 ab ad bd cd abcd ae be ce abce.
[ 11: 24 24 16 16 16]  0 ac bc ad bd abcd ae be abce abde.
[ 12: 12 12 12 12 12]  0 ab ac bc ad bd cd ae be ce de.
[ 13: 22 19 19 14 14]  0 ab ac ad bd cd abcd ae be ce abce.
[ 14: 22 21 21 19 19]  0 ab ac ad cd abcd ae be abce de bcde.
[ 15: 22 22 22 14 14]  0 ab ac bc ad bd cd abcd ae be ce abce.
[ 16: 24 22 22 21 19]  0 ab ac bc bd cd abcd ae be ce abde acde.
[ 17: 24 24 22 22 16]  0 ab ac bc ad bd cd abcd ce abce de abde.
[ 18: 26 24 24 24 24]  0 bc ad bd cd abcd ae be ce abce abde acde.
[ 19: 24 24 22 22 22]  0 ac bc ad bd cd abcd ae be ce abce de abde.
[ 20: 27 24 22 22 19]  0 ab ac bc ad bd cd abcd ae ce abce de abde.
[ 21: 27 27 22 22 22]  0 ab ac bc ad bd cd abcd ae be ce abce de abde.
[ 22: 29 27 27 27 24]  0 ab ac bc ad bd cd abcd be ce abce de abde acde.
[ 23: 32 27 27 27 27]  0 ab ac bc ad bd cd abcd ae be ce abce de abde acde.
[ 24: 32 32 32 32 32]  0 ab ac bc ad bd cd abcd ae be ce abce de abde acde bcde.

*/

void borderEdges(int rank, Intstack cell, Intstack result) {
	int lim = 1 << rank;
	int p, q, i, ch, nf;
	result->size = 0;
	for (p = 1; p < lim; p = p << 1) {
		for (q = p << 1; q < lim; q = q << 1) {
			ch = p + q;
			nf = 1;
			for (i = 0; nf && i < cell->size; i++) {
				nf = ((ch & cell->it[i]) > 0 || member(ch | cell->it[i], cell));
			}
			if (nf)
				putint(ch, result);
		}
	}
}

Graph borderGraph(int rank, Intstack cell) {
	Intstack borderedges = newIntstack(0, NULL);
	borderEdges(rank, cell, borderedges);
	return newGraph(rank, rank, borderedges);
}

int bestBorderGraph(int rank, Intstack cell) {
	Intstack cpCell = newIntstack(cell->size, NULL), borderedges = newIntstack(0, NULL);
	int i, x = 0, xsize = -1, y;
	for (i = 0; i < cell->size; i++) {
		y = cell->it[i];
		cpIntstack(cell, cpCell);
		translate(y, cpCell);
		borderEdges(rank, cpCell, borderedges);
		if (xsize < borderedges->size) {
			x = y;
			xsize = borderedges->size;
		}
	}
	freestack(cpCell);
	freestack(borderedges);
#if 0
	printf("Translating over ");
	printPA(x);
	printf(" gives %d border edges.\n", xsize);
#endif
	return x;
}

/* all potential edges between ports and internal vertices */
Intstack potEdges(int rank, int cn) {
	int size = rank * (cn - rank), p, q, pend = 1 << rank, qend = 1 << cn;
	Intstack result = newIntstack(size, NULL);
	for (q = pend; q < qend; q <<= 1)
		for (p = 1; p < pend; p <<= 1)
			putint(p+q, result);
	assert(result->size == size);
	return result;
}

/* findGraph of Fig. 1 of whh424.
 * g0 = (V, E_0); assume c0 sub cell, and c0 = Kp(g0).
 * If possible return graph g with Kp(g) = cell
 * from g0 and edges from ledges[lptr..); else NULL.
 * Preserves the arguments, but modifies g0 */
Graph findGraphR(Graph g0, Intstack c0, Intstack cell, Intstack ledges, int lptr) {
	int ed, uu, edsize;
	Intstack kk, c1;
	Graph result;
	if (c0->size == cell->size)
		return g0;
	if (lptr == ledges->size)
		return NULL;
	edsize = g0->edges->size;
	result = findGraphR(g0, c0, cell, ledges, lptr+1);
	if (result)
		return result;

	g0->edges->size = edsize; /* undo pushing of edges */
	ed = ledges->it[lptr];
	uu = ((1 << g0->cN) - 1) ^ ed;
	kk = mkcell(uu, g0);
	translate(((1 << g0->cP) - 1) & ed, kk);
	if (subset(kk, cell)) {
		putint(ed, g0->edges);
		c1 = newIntstack(0, c0);
		intunion(c1, kk);
		result = findGraphR(g0, c1, cell, ledges, lptr+1);
		freestack(c1);
		return result;
	} else {
		freestack(kk);
		return NULL;
	}
}

/* Pre: the cell is regular.
 * Therefore, the subgraph of the internal nodes has a matching. 
 * Use the classification of the matched graphs of rank cN-cP */
Graph findGraphEG(int rank, int internal, Intstack cell) {
	Graph g0 = borderGraph(rank, cell), g1, result= NULL;


	Intstack c0, gri, edl;

	c0 = kpa(g0);
	assert(subset(c0, cell));
	if (c0->size == cell->size) {
		freestack(c0);
		return g0;
	}
	if (internal < 2) {
		freestack(c0);
		freeGraph(g0);
		return NULL;
	}
	g1 = newGraph(rank, rank, NULL);
	while (result == NULL && g1->cN + 2 <= rank+internal) {
		Arraylist grs;
		int i, j;
		g1->cN = g1->cN + 2;
#if 1
		printf("Trying with %d nodes.\n", g1->cN);
#endif
		
		
		grs = getAMG(g1->cN - rank);
		edl = potEdges(rank, g1->cN);
		for (i = 0; i < grs->size; i++) {
			gri = (Intstack) grs->it[i];
			printf(".");
			fflush(stdout);
			g1->edges = newIntstack(0, g0->edges);
			/* transfer edges from gri to g1->edges
			 * shifted over rank */
			for (j = 0; j < gri->size; j++) {
				putint(gri->it[j] << rank, g1->edges);
			}
			
			result = findGraphR(g1, c0, cell, edl, 0);

			if (result) {
				break;
			} else {
				freestack(g1->edges);
				g1->edges = NULL;
			}
		}
		freestack(edl);
	}
	freestack(c0);
	freeGraph(g0);
	if (!result)
		freeGraph(g1);
	printf("\n");
	
	
	return result;
}

Graph findGraphBEG(int rank, int internal, Intstack cell) {
	int x = bestBorderGraph(rank, cell);
	Intstack nc = newIntstack(0, cell);
	Graph g;
	translate(x, nc);
	g = findGraphEG(rank, internal, nc);
	
	if (g)
		translateGraph(x, g);

	freestack(nc);
	return g;
}
// [  1: 12  3  3  3  3]  0 ab ac ad ae.
Graph findGraph(int rank, int internal, Intstack cell) {
	/* Tries only the best border graph, and then decompositions */
	Graph g1 = findGraphBEG(rank, internal, cell);
	if (!g1) {
		int i = 0, nf = 1, pa;
		Intstack p1 = newIntstack(cell->size, NULL), p2 = newIntstack(cell->size, NULL), other = newIntstack(0, cell);
		Graph g2;
		printf("Trying decompositions.\n");
		while (nf && i < cell->size) {
			pa = cell->it[i];
			cpIntstack(cell, other);
			translate(pa, other);
			if (!indecomposable(other, p1, p2)) {
				g1 = findGraphBEG(rank, internal, p1);
				g2 = findGraphBEG(rank, internal, p2);
				if (g1 && g2) {
					nf = 0;
					odotGraph(g1, g2);
					translateGraph(pa, g1);
				} else {
					freeGraph(g1);
					g1 = NULL;
				}
				freeGraph(g2);
			}
			i++;
		}
		freestack(other);
		freestack(p1);
		freestack(p2);
	}
	return g1;
}
int main4(int argc, char *argv[]) {
// [ 18: 26 24 24 24 24]  0 bc ad bd cd abcd ae be ce abce abde acde.
	Intstack cell, ce;
	Graph g;
	int rank = 0, internal = 4, nr = 0;
	assert(argc> 1);
	rank = atoi(argv[1]);
	setAMG(internal); /* Construct matched graphs */
	setRank(rank);
	freePerm();
	cell = readCell();
	while (cell) {
		nr = getCnr();
		printCell(nr, cell);
#if 1
		g = findGraph(rank, internal, cell);
#else   /* ORI never yields better results */
		g = findGraphORI(rank, internal, cell);
#endif
		if (g) {
			minimizeGraph(g); /* see mkCell */
			printf("Gra%dnr%d  ", rank, nr);
			writeGraph(g);
#if 1
			ce = kpa(g);
			assert(compareL(cell, ce) == 0);
			freestack(ce);
#endif
			freeGraph(g);
		} else
			printf("No graph found.\n");
		freestack(cell);
		printf("\n");
		cell = readCell();
	}
	freeAMG();
	finalizeScanner();
	reportTime();
#if 1 /* to chase memory leaks */
	reportCnt();
	reportACnt();
	reportGCnt();
#endif
	return 0;
}
