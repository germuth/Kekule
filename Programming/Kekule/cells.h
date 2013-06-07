
/* cells.h, Wim H. Hesselink, June 2009 */
#include "arraylist.h"

Intstack even(int r) ;
void translate(int x, Intstack a);
int isFlexible(int ports, Intstack cell);
int indecomposable(Intstack cell, Intstack part1, Intstack part2);

void printPA(int x) ;
void printCell(int nr, Intstack a) ;
void printDistances(Intstack a) ;
void printCellList(Arraylist ar) ;
int isOriginal(Intstack cell);
