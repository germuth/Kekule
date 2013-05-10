
/* scanner.h, Wim H. Hesselink, juni 2009 */

#if !defined(SCAN)
#define SCAN

void readLine(FILE *input, int kek);
void scan(); 
char getChar();
int getNumber();
char *getIdent();
int accept(char cc);
void finalizeScanner();

#endif
