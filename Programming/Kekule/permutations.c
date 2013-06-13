/* permutations.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "arraylist.h"
#include "cells.h"
#include "histogram.h"
#include "readKekule.h"

#define RANKLIM 10

static Arraylist perm[RANKLIM];
static int permInit = 0;

/* Each permutation of k is represented by an Intstack with 
 * of size k with the numbers 0, .., k-1 in some permuted 
 * order. The very last one is the identity. */

void initPerm() {
	int i;
	Intstack p0, p1;
	if (permInit)
		return;
	permInit = 1;
	for (i = 0; i < RANKLIM; i++)
		perm[i] = NULL;
	perm[0] = newArraylist(1) ;
	perm[1] = newArraylist(1) ;
	p0 = newIntstack(0, NULL) ;
	putItem(p0, perm[0]) ;
	p1 = newIntstack(1, NULL) ;
	putint(0, p1) ;
	putItem(p1, perm[1]) ;
}

void freePerm() {
	int i;
	for (i = 0; i < RANKLIM; i++) {
		if (perm[i]) {
			freeStackList(perm[i]) ;
			perm[i] = NULL;
		}
	}
	permInit = 0;
}

Arraylist getPerm(int rank) {
	int i, j, k;
	Intstack y, z;
	if (!permInit)
		initPerm() ;
	if (perm[rank] != NULL)
		return perm[rank];
	if (perm[rank-1] == NULL)
		getPerm(rank-1) ;
	perm[rank] = newArraylist(rank * perm[rank-1]->size) ;
	for (i = 0; i < perm[rank-1]->size; i++) {
		y = perm[rank-1]->it[i];
		for (j = 0; j < rank; j++) {
			z = newIntstack(rank, NULL);
			for (k = 0; k < j; k++)
				z->it[k] = y->it[k];
			z->it[j] = rank - 1;
			for (k = j; k < y->size; k++)
				z->it[k+1] = y->it[k];
			z->size = rank;
			putItem(z, perm[rank]) ;
		}
	}
	return perm[rank];
}

void printPerm(int rank) {
	getPerm(rank) ;
	mapar(perm[rank], (void (*)(void*)) printCell) ;
}

Intstack applyPerm(int shift, Intstack arg, Intstack pm) {
	int i;
	Intstack result = newIntstack(0, arg) ;
	for (i = 0; i < pm->size; i++) {
		result->it[i+shift] = arg->it[pm->it[i]+shift];
	}
	return result;
}

/* Pre: perm is initialized, hist[0..rank-1] is descending.
 * Create the list of permutations (as powers of 2)
 * that preserve hist[0..rank-1]  */
Arraylist specPerm(int rank, int* hist) {
	int i, j, k, m, x;
	Intstack prim = newIntstack(rank, NULL) ;
	Arraylist result = newArraylist(1) ;
	Arraylist locperm;
	prim->size = rank;
	for (i = 0, x = 1; i < rank; i++, x <<= 1) {
		prim->it[i] = x;
	}
	putItem(prim, result) ;
	i = 0;
	while (i + 1 < rank) {
		j = i+1;
		while (j < rank && hist[i] == hist[j])
			j++;
		if (i+1 < j) {
			x = result->size;
			locperm = getPerm(j-i) ;
			for (k = 0; k < locperm->size - 1; k++)
				for (m = 0; m < x; m++)
					putItem(applyPerm(i, result->it[m], locperm->it[k]), result) ;
		}
		i = j;
	}
	return result;
}

/* Pre: per is a permutation of powers of 2;
 * Return the port permutation of cell a */
Intstack permuteCell(Intstack a, Intstack per) {
	Intstack result = newIntstack(a->size, NULL) ;
	int i, j, x, y;
	for (i = 0; i < a->size; i++) {
		x = a->it[i];
		y = 0;
		j = 0;
		while (x > 0) {
			y += (x % 2) * per->it[j];
			x /= 2;
			j++;
		}
		putint(y, result) ;
	}
	qusort(result->it, 0, result->size) ;
	return result;
}

Arraylist rawPermVariants(int rank, Intstack cell) {
	int i, h[RANKLIM];
	Arraylist perms;
	Arraylist result;
	portHisto(cell, h) ;
	perms = specPerm(rank, h) ;
	result = newArraylist(perms->size) ;
	for (i = 0; i < perms->size; i++) {
		putItem(permuteCell(cell, perms->it[i]), result) ;
		freestack(perms->it[i]) ;
	}
	freeArraylist(perms) ;
	return result;
}

Arraylist permVariants(int rank, Intstack cell) {
	Arraylist result = rawPermVariants(rank, cell) ;
	sortDD(result, (int(*)(const void*, const void*)) compareL,
			(void(*)(void*)) freestack) ;
	return result;
}

Intstack somePHDvariant(int rank, Intstack cell) {
	/* Pre: cell is centered.
	 * Permute cell such that it becomes portHistDescending.
	 * The cell is destroyed. */
	int hh[RANKLIM];
	Intstack pe = below(rank), pg = newIntstack(rank, NULL) ;
	int i, p, j, q;
	setRank(rank) ;
	portHisto(cell, hh) ;
	
	/* insertion sort of hh, invariant: hh[0..i) is sorted */
	i = 1;
	while (i < rank) {
		j = i;
		p = hh[i];
		q = pe->it[i];
		i++;
		while (j > 0 && hh[j-1] < p) {
			hh[j] = hh[j-1];
			pe->it[j] = pe->it[j-1];
			j--;
		}
		hh[j] = p;
		pe->it[j] = q;
	}
	
	pg->size = rank;
	p = 1;
	for (i = 0; i < rank; i++) {
		pg->it[pe->it[i]] = p;
		p <<= 1;
	}
	freestack(pe) ;
	
	pe = permuteCell(cell, pg) ;
	freestack(pg) ;
	freestack(cell) ;
	return pe;
}

Intstack firstVariant(int rank, Intstack cell) {
		
	Intstack pe = somePHDvariant(rank, cell) ;
	Arraylist ar = rawPermVariants(rank, pe) ;

	int i;
	freestack(pe) ;
	pe = ar->it[0];
	for (i = 1; i < ar->size; i++) {
		if (compareL(ar->it[i], pe) < 0) {
			freestack(pe) ;
			pe = ar->it[i];
		} else {
			freestack(ar->it[i]) ;
		}
	}
	freeArraylist(ar) ;
	return pe;
}

Arraylist allVariants(int rank, Intstack cell) {
	Arraylist ar, result = permVariants(rank, cell) ;
	Intstack cen = centers(cell), ce0, ce1;

	int i;
	for (i = 1; i < cen->size; i++) {
		ce0 = newIntstack(0, cell) ;
		translate(cen->it[i], ce0) ;
		ce1 = somePHDvariant(rank, ce0) ;
		if (!memberAr(ce1, result, (int(*)(const void*, const void*)) compareL)) {
#if 0
			printf("Variant occurs.\n");
#endif
			ar = permVariants(rank, ce1) ;
			mergeAr(result, ar, (int(*)(const void*, const void*)) compareL) ;
		}
		freestack(ce1) ;
	}
	freestack(cen) ;
	return result;
}
