#ifndef CONFIGURATION_H
#define CONFIGURATION_H

typedef struct hashtable_s HashTable;

struct configuration {
    HashTable *entries;
};

void configuration_init();
void configuration_new();
void configuration_read(FILE *file);
char *configuration_get_string(char *key);

bool configuration_get_bool(char *key);
int configuration_get_int(char *key);
void configuration_dispose();

#endif