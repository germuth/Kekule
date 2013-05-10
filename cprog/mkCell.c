
/* mkCell.c, Wim H. Hesselink, June 2011 */
#include <stdlib.h>
#include <stdio.h>
#include "auxil.h"
#include "graph.h"
#include "cells.h"

static int ports;

/* Function M(U) of whh424, section 4.2.
 * Computed according to Lemma 3.
 * The set of nodes U is represented by the bit vector uu.
 * The set of ports is represented by the bit vector ports. 
 */
Intstack mkcell(int uu, Graph g) {
  int u, i, v;
  Intstack acc, addend, ed = g->edges;
  ports = (1 << g->cP) - 1;
  if (uu == 0) {
    acc = newIntstack(5, NULL);
    putint(0, acc);
  } else {
    u = firstspike(uu);
    uu = u ^ uu;
    acc = (u & ports ? mkcell(uu, g) : newIntstack(5, NULL));
    /* treat nbh(u, g) */ 
    for (i = 0; i < ed->size; i++) 
      if (u & ed->it[i]) { /* u in edge[i] */
	v = firstspike(u ^ ed->it[i]);
	if (v & uu) { /* v in uu */
	  addend = mkcell(uu ^ v, g);
	  translate(ports & (u+v), addend);
	  intunion(acc, addend);
	}
      }
  }
  return acc;
}

/* Kp(G) */
Intstack kpa(Graph g) {
  return mkcell((1 << g->cN) - 1, g);
}

void minimizeGraph(Graph g) {
  Intstack cell, acell, ed;
  int i = 0, edge;
  ed = g->edges;
  if (ed->size == 0) return;
  cell = kpa(g);
  ed->size--;
  while (i <= ed->size) {
    edge = ed->it[i];
    ed->it[i] = ed->it[ed->size];
    acell = kpa(g); 
    if (subset(cell, acell)) {
      printf("  Removing edge.\n");
      freestack(cell);
      cell = acell;
      ed->size--;
    } else {
      ed->it[i] = edge;
      freestack(acell); 
      i++;
    }
  }
  ed->size++;
  freestack(cell);
}
