/* classify.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "cells.h"
#include "arraylist.h"
#include "histogram.h"
#include "permutations.h"
#include "coherence.h"

/* The number of PAs of rank at most r is 1<<r.
 * The number of even PAs of rank at most r is 1<<(r-1).
 * The number of nonempty even PAs of rank at most r 
 * is 1<<(r-1)-1.
 * The number of even cells of rank at most r that contain 
 * the empty PA is therefore 1<<(1<<(r-1)-1). */

Arraylist classify(int r, int raw) {
	int i, ports = (1 << r) - 1;
	Intstack ev = even(r);
	Arraylist result= NULL;
	int *a = ev->it;
	unsigned long int ulim = (1UL << (ev->size - 1)), x, y;
	Intstack cand = newIntstack(5, NULL);
	int nr = 0;
	if (!raw)
		result = newArraylist(32);
	setRank(r);
	putint(0, cand);
	for (x = 0L; x < ulim; x ++) {
		
		cand->size = 1;
		y = x;
		i = 1;
		while (y > 0) {
			if (y % 2 == 1)
				putint(a[i], cand);
			y /= 2;
			i++;
		}
		if (isFlexible(ports, cand) && portHistDescending(cand)
				&& isCentered(cand, x) && isCoherent(cand) ) {
			if (raw)
				printCell(++nr, cand);
			else
				putItem((void*) newIntstack(0, cand), result);
		}
	}
	freestack(cand);
	freestack(ev);
	return result;
}

void sortAndWeed(int rank, Arraylist ar) {
	int i = 0, j, k;
	Arraylist variants;
	asort(ar, (int(*)(const void*, const void*)) compareCell);
	/* Invariant: it[0..i) has no alpha variants in it, 
	 *            i = ar->size || it[i] != NULL */
	while (i < ar->size) {
		j = i+1;
		while (j < ar->size && ar->it[j] == NULL)
			j++;
		if (j == ar->size || compareW(ar->it[i], ar->it[j]) != 0) {
			i = j;
		} else {
			variants = allVariants(rank, ar->it[i]);
			
			assert(compareL(ar->it[i], variants->it[0]) == 0);
			freestack(variants->it[0]);
			k = 1;
			while (k < variants->size && j < ar->size) {
				if (ar->it[j] != NULL && compareL(ar->it[j], variants->it[k]) == 0) {
					freestack(variants->it[k]);
					freestack(ar->it[j]);
					ar->it[j] = NULL;
					k++;
				}
				j++;
			}
#if 1
			assert(k == variants->size);
#else
			/* Did you call with the RawCells? */
			if (k != variants->size) {
				printf("Not all variants found: %d %d %d\n", i, k, variants->size);
			}
#endif
			freeArraylist(variants);
			i++;
			while (i < ar->size && ar->it[i] == NULL)
				i++;
		}
	}
	removeNulls(ar);
}

void sortAndWeedForGraphs(int rank, Arraylist ar) {
	/* A variation of the above for graph classification */
	int i = 0, j, k;
	Arraylist variants;
	asort(ar, (int(*)(const void*, const void*)) compareCell);
	while (i < ar->size) {
		j = i+1;
		while (j < ar->size && ar->it[j] == NULL)
			j++;
		if (j == ar->size || compareW(ar->it[i], ar->it[j]) != 0) {
			i = j;
		} else {
			variants = permVariants(rank, ar->it[i]); /* var */
			k = 1;
			while (k < variants->size && j < ar->size) {
				if (ar->it[j] != NULL && compareL(ar->it[j], variants->it[k])
						== 0) {
					k++;
					freestack(ar->it[j]);
					ar->it[j] = NULL;
				}
				j++;
			}
			assert(k == variants->size);
			freeStackList(variants);
			i++;
			while (i < ar->size && ar->it[i] == NULL)
				i++;
		}
	}
	removeNulls(ar);
}

void printDoubletons(Arraylist ar) {
	int i, cnt = 0;
	Intstack cen;
	printf("\n");
	for (i = 1; i < ar->size; i++) {
		if (compareW(ar->it[i-1], ar->it[i]) == 0) {
			printf("Doubleton %d %d with the centers:\n", i, i+1);
			cen = centers(ar->it[i-1]);
			printCell(-1, cen);
			freestack(cen);
			cen = centers(ar->it[i]);
			printCell(-1, cen);
			freestack(cen);
			cnt++;
		}
	}
	printf("Number of doubletons: %d.\n", cnt);
}
