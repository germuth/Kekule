
/* arraylist.h, Wim H. Hesselink, June 2009 */
#if ! defined(ALIST)
#define ALIST

struct alist {
  int size ;
  int upb ; /* upb is the number of array elements available */
  void** it ;
} ;

typedef struct alist* Arraylist ;

Arraylist newArraylist(int upb) ;
void freeArraylist(Arraylist a) ;
void printArrayList(Arraylist a);
void reportACnt() ;

Arraylist putItem(void* x, Arraylist a) ;
void concatAr(Arraylist result, Arraylist ar) ;

void swapar(void** a, int p, int q) ;
void mapar(Arraylist a, void(*f)(void*)) ;
void mapiar(Arraylist a, int x, void(*f)(int, void*)) ;
void asort(Arraylist a, int(*comp)(const void*, const void*)) ;
void sortDD(Arraylist a, int(*comp)(const void*, const void*),
	    void(*f)(void*) ) ;
void removeNulls(Arraylist ar) ;
int memberAr(void* item, Arraylist ar, 
	     int(*comp)(const void*, const void*)) ;
void mergeAr(Arraylist result, Arraylist ar, 
	     int(*comp)(const void*, const void*)) ;

#endif
