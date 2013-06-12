
/* graph.c, Wim H. Hesselink, June 2011 */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "auxil.h"
#include "graph.h"

static int cnt = 0; /* Number of graphs created minus deleted */
static int cnt0 = 0;

void reportGCnt() {
  printf("Graph difference %d.\n", cnt - cnt0);
  cnt0 = cnt;
}

Graph newGraph(int cp, int cn, Intstack a) {
  Graph g = (Graph) malloc(sizeof(struct graph)) ;
  assert(g != NULL);
  cnt++;
  g->cP = cp ;
  g->cN = cn ;
  g->edges = a ;
  return g ;
}

void freeGraph(Graph g) {
  if (g){
    freestack(g->edges);
    free(g);
    cnt--;
  }
}

Graph cpGraph(Graph g0) {
  return newGraph(g0->cP, g0->cN, 
		  newIntstack(0, g0->edges)) ;
}

void translateGraph(int x, Graph g) {
  int i, k, p ;
  Intstack ed;
  if (g == NULL) return;
  ed = g->edges ;
  assert(x < 1 << g->cP);
  while (x > 0) {
    k = firstspike(x) ;
    x = x - k ;
    p = 1 << g->cN ;
    for (i = 0 ; i < ed->size ; i++) {
      if ((ed->it[i] & k) > 0) ed->it[i] += p-k ;
    }
    putint(p+k, ed) ;
    g->cN ++ ;
  }
}

void writeGraph(Graph g) {
  int i, p, q, x ;
  Intstack a ; 
  if (g == NULL) {
    printf("Null.\n") ;
    return ;
  }
  a = g->edges ;
  qusort(a->it, 0, a->size);
  printf("%d %d;", g->cP, g->cN) ;
  for (i = 0 ; i < a->size ; i++) {
    x = a->it[i] ;
    p = firstbit(x) ;
    x = x - (1<<p) ;
    q = firstbit(x) ;
    if (i > 0) printf(",") ;
    printf(" %d-%d", p, q) ;
  }
  printf(".\n") ;
}

int renameEdge(int ed, int from, int over){
  int i = 0, k = 1, result = 0;
  assert(ed != 0);
  while ((ed & k) == 0){
    k *= 2;
    i++;
  }
  result = (i < from ? k : k << over);
  assert(ed > k);
  do {
    k *= 2;
    i++;
  } while ((ed & k) == 0);
  return result + (i < from ? k : k << over);
}

void odotGraph(Graph g1, Graph g2){
  /* assume g1->cP >= g2->cP; corrupts and reuses g1 */
  int i;
  for (i = 0 ; i < g2->edges->size ; i++){
    putint(renameEdge(g2->edges->it[i], g2->cP, g1->cN - g2->cP),
	   g1->edges);
  }
  g1->cN = g1->cN + g2->cN - g2->cP;
}
