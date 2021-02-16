.PHONY: clean

main: main.o classreader.o
	gcc -o main main.o classreader.o

main.o: main.c
	gcc -c -o main.o main.c

classreader.o: classreader.c
	gcc -c -o classreader.o classreader.c

clean:
	rm -rf main *.o
