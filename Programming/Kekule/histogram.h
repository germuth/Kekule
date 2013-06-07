
/* histogram.h, Wim H. Hesselink, June 2009 */

#include "auxil.h"

void setRank(int r) ;

int compareL(const Intstack h1, const Intstack h2) ;
int compareW(const Intstack ce1, const Intstack ce2) ;
int compareCell(const Intstack ce1, const Intstack ce2) ; 

Intstack portHisto(Intstack cell, int* hist) ;
int portHistDescending(Intstack cell) ;
int isCentered(Intstack cell) ;
void toCenter(Intstack cell, int *pa) ;
Intstack centers(Intstack cell) ;
Intstack weightlist(Intstack cell) ;

void printHisto(Intstack cell) ;
void printWeightlist(Intstack cell) ;
