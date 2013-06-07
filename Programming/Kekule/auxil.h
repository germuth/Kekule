/* auxil.h, Wim H. Hesselink, June 2009 */

#if ! defined(AUXIL)
#define AUXIL

#include "arraylist.h"

struct istack {
	int size;
	int upb;
	int *it;
};

typedef struct istack *Intstack;

void reportTime();

void cpIntstack(Intstack a, Intstack b);
Intstack newIntstack(int upb, Intstack a);
void freestack(Intstack a);
void freeStackList(Arraylist ar);
void reportCnt();

Intstack below(int n);
Intstack putint(int x, Intstack a);
int popint(Intstack a);
int equalIntstack(Intstack a, Intstack b);
int member(int x, Intstack a);
int amember(int x, Intstack a);
int subset(Intstack a, Intstack b);
void intunion(Intstack a, Intstack b);
void intersect(Intstack a, Intstack b);
Intstack intersection(Intstack a, Intstack b);
void filtersub(int upb, Intstack a);

void swap(int *a, int p, int q);
void qusort(int *a, int p, int q);
void isortDD(Intstack a);

int cntBits(int x);
int firstbit(int x);
int firstspike(int x);
int bitUnion(Intstack a);
int twolog(int x);

void printar(Intstack a);
void printarn(Intstack a);

#endif
