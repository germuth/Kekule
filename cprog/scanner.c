
/* scanner.c, Wim H. Hesselink, June 2011 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#define MAX_LEN 100

static char *line = NULL;
static int i, kind;
static int num;
static char idstring[MAX_LEN];
static char ch;

static void readNumber() {
  /* Pre: i = i0 && isdigit(line[i]),
   * Post: num holds the number represented by line[i0 .. i)
   */
  num = 0;
  while (isdigit(line[i])) {
    num = 10 * num + line[i] - '0'; 
    i++;
  }
}

static void readKekNumber() {
  /* Pre: i = i0 && isalnum(line[i]),
   * Letters are interpreted as bits,
   * Post: num holds the bitstring represented by line[i0 .. i)
   */
  if (isdigit(line[i])) {
    readNumber();
  } else {
    num = 0;
    while (isalpha(line[i])) {
      if ('a' <= line[i] && line[i] <= 'z') {
	num += 1 << (line[i] - 'a'); 
      }
      i++;
    }
  }
}

static void readIdent() {
  /* Pre: i = i0 && isalpha(line[i]),
   * Post: idstring holds the identifier line[i0 .. i)
   */
  int len, j;
  j = i;
  while (isalnum(line[i])) i++;
  len = (i-j < MAX_LEN ? i-j : MAX_LEN-1);
  memcpy(idstring, &line[j], len);
  idstring[len] = '\0';
}

/* ch/num/idstring get contents of new symbol at index i;
 * i moves to next position. 
 */
void scan() {
  while (isspace(line[i])) i++;
  /* skips white space */
  if (line[i] == '\0') {
    ch = '\n';
  } else {
    ch = line[i];
    if (isalnum(ch)) {
      if (kind) {
	ch = '0';
	readKekNumber();
      } else if (isalpha(ch)) {
	ch = 'a';
	readIdent();
      } else { /* isdigit(ch) */
	ch = '0';
	readNumber();
      }
    } else i++;
  } 
}

void readLine(FILE *input, int kek) {
  static int maxlen;  /* initially 0; note that maxlen is static! */
  int idx = 0;
  kind = kek;
  do {
    if (idx == maxlen) {
      maxlen += 100;
      line = realloc(line, maxlen * sizeof(char));
    }
    line[idx] = fgetc(input);
    while (line[idx] == '\\') {
      while (fgetc(input) != '\n' && !feof(input));
      line[idx] = fgetc(input);
    }
    idx++;
  } while (line[idx-1] != '\n' && !feof(input));
  line[idx-1] = '\0';
  i = 0; 
  scan();
}

int getNumber() {
  return num;
}

char *getIdent() {
  return (ch == 'a' ? idstring : NULL);
}

char getChar() {
  return ch;
}

int accept(char cc) {
  if (cc == ch) {
    scan();
    return 1;
  } else return 0;
}

void finalizeScanner() {
  free(line);
}
