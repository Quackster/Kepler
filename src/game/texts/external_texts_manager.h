#ifndef EXTERNAL_TEXTS_MANAGER_H
#define EXTERNAL_TEXTS_MANAGER_H

typedef struct hashtable_s HashTable;

struct texts_manager {
    HashTable *texts;
};

void texts_manager_init();
char *texts_manager_get_value_by_id(char *key);
void texts_manager_dispose();

#endif