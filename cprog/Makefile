# dit is commentaar
CC = c99
# CFLAGS = -Wall -pedantic
SRC =     scanner.c graph.c auxil.c cells.c mkCell.c \
	  coherence.c histogram.c classify.c arraylist.c \
          permutations.c readKekule.c fdGraph.c classGraph.c \
	  mkCellMain.c decompose.c ugraph.c equicells.c \
          clfmain.c complementcell.c
HEADERS = arraylist.h cells.h classify.h graph.h \
          permutations.h auxil.h coherence.h histogram.h \
          mkCell.h  scanner.h readKekule.h classGraph.h
OBJcell = scanner.o graph.o auxil.o cells.o mkCell.o  arraylist.o \
	  coherence.o histogram.o readKekule.o mkCellMain.o permutations.o
OBJgraph = scanner.o graph.o auxil.o cells.o mkCell.o \
          coherence.o histogram.o arraylist.o classify.o \
          permutations.o readKekule.o fdGraph.o classGraph.o
OBJclf =  scanner.o graph.o auxil.o cells.o mkCell.o \
          coherence.o histogram.o classify.o arraylist.o \
          permutations.o readKekule.o classGraph.o clfmain.o
OBJdec =  scanner.o auxil.o cells.o graph.o \
          coherence.o histogram.o arraylist.o \
          permutations.o readKekule.o decompose.o
OBJugr =  scanner.o graph.o auxil.o cells.o mkCell.o \
          coherence.o histogram.o classify.o arraylist.o \
          permutations.o readKekule.o classGraph.o ugraph.o
OBJcom =  scanner.o auxil.o cells.o arraylist.o coherence.o graph.o \
          readKekule.o complementcell.o histogram.o
OBJequi =  scanner.o auxil.o cells.o arraylist.o readKekule.o \
	  equicells.o coherence.o  histogram.o graph.o

cell:   $(OBJcell) 
	$(CC) $(OBJcell) -o g2cell

graph:  $(OBJgraph) 
	$(CC) $(OBJgraph) -o c2graph

clf:    $(OBJclf) 
	$(CC) $(OBJclf) -o classify

dec:    $(OBJdec) 
	$(CC) $(OBJdec) -o decompose

ugr:   $(OBJugr) 
	$(CC) $(OBJugr) -o ugraph

com:   $(OBJcom) 
	$(CC) $(OBJcom) -o complement

$(OBJcell) : $(HEADERS)
$(OBJgraph) : $(HEADERS)
$(OBJclf) : $(HEADERS)
$(OBJdec) : $(HEADERS)
$(OBJugr) : $(HEADERS)
$(OBJcom) : $(HEADERS)

clean:
	rm -f *~
	rm -f #*#
	rm -f *.o
