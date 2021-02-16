#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include "classreader.h"

int main(int argc, char **argv) {
    char *filename;
    if (argc > 1) {
        filename = argv[1];
    } else {
        perror("Filename not provided");
        exit(1);
    }

    FILE* file = fopen(filename, "rb");

    if (file == 0) {
        perror("File not found");
        exit(1);
    }

    struct classfile c = readClass(file);

    fclose(file);

    printf("Minor version: %d\n", c.minor_version);
    printf("Major version: %d\n", c.major_version);

    char buffer[1000];


    for (int i = 0; i < c.constant_pool_count - 1; ++i) {
        printf("#%-6d%-20s", i + 1, getConstantName(c.constant_pool[i].tag));

        entryToString(c, i, buffer);
        printf("%s", buffer);

        // 8-byte constants take two constant pool entries
        if (c.constant_pool[i].tag == CONSTANT_Long || c.constant_pool[i].tag == CONSTANT_Double) {
            i++;
        }

        printf("\n");
    }

    dispose(c);

    return 0;
}
