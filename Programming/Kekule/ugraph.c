
/* ugraph.c , Wim H. Hesselink, June 2011
 * Classify all undirected graphs on rank vertices, up to isomorphism */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "histogram.h"
#include "arraylist.h"
#include "graph.h"
#include "classify.h"
#include "permutations.h"
#include "classGraph.h"

void printAsGraph(int i, int j, Intstack gr) {
  Graph g;
  g = newGraph(i, j, gr);
  writeGraph(g);
  freeGraph(g);
}

void printAsGraphList(int rank, Arraylist ar) {
  int i;
  for (i = 0; i < ar->size; i++)
    printAsGraph(i+1, rank, ar->it[i]);
}

int main2(int argc, char *argv[]) {
  int i, rank;
  Arraylist r;
  assert(argc > 1);
  rank = atoi(argv[1]);
  r = allGraphs(rank);
  if (rank % 2 == 1){
    printf("Graphs: %d.\n", r->size);
    printAsGraphList(rank, r);
  } else {
    Arraylist r0, r1;
    r0 = newArraylist(5);
    r1 = newArraylist(5);
    for (i = 0 ; i < r->size ; i++) {
      if (matched(rank, r->it[i])) putItem(r->it[i], r0);
      else putItem(r->it[i], r1);
    }
    printf("Matched graphs: %d.\n", r0->size);
    printAsGraphList(rank, r0);
    printf("Unmatched graphs: %d.\n", r1->size);
    printAsGraphList(rank, r1);
    freeArraylist(r0);
    freeArraylist(r1);
  }
  freeArraylist(r);
  freePerm();
#if 0
  reportCnt();
  reportACnt();
  reportGCnt();
#endif
  return 0;
}

/* 
 * The number of isomorphism classes of matched/unmatched graphs is:
 * rank = 1 ->       0 /    1
 * rank = 2  ->      1 /    1
 * rank = 3 ->       0 /    4
 * rank = 4  ->      6 /    5
 * rank = 5 ->       0 /   34
 * rank = 6  ->    101 /   55
 * rank = 7 ->       0 / 1044
 * rank = 8  ->  10413 / 1933
 */
