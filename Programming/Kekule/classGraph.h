
/* classGraph.c , Wim H. Hesselink, August 2009 */

#include "auxil.h"
#include "arraylist.h"

Arraylist allGraphs(int rank);
Arraylist allMatchedGraphs(int rank);
void setAMG(int k);
Arraylist getAMG(int k);
void freeAMG();
int matched(int rank, Intstack gr);
