
/* graph.h, Wim H. Hesselink, June 2009 
 * In Kekule theory, a graph is characterized by the number 
 * of ports cP, the number of nodes cN, and the sequence of 
 * undirected edges between the nodes, subject to cP <= cN <= 32. 
 * The nodes are natural numbers < 32. 
 * The edge p-q is represented by the number 2^p * 2^q, i.e., 
 * by a 2bit bitvector obtained as (1<<p)+(1<<q).
 * The edges need not be ordered. */

#if ! defined(GRAPH)
#define GRAPH

struct graph {
  int cP, cN;
  Intstack edges;
};

typedef struct graph* Graph;

Graph newGraph(int cp, int cn, Intstack a);
void reportGCnt();
Graph cpGraph(Graph g0);
void translateGraph(int x, Graph g);
void odotGraph(Graph g1, Graph g2);
void writeGraph(Graph g);
void freeGraph(Graph g);
#endif
