#ifndef CATALOGUE_MANAGER_H
#define CATALOGUE_MANAGER_H

typedef struct list_s List;
typedef struct hashtable_s HashTable;

typedef struct catalogue_page_s catalogue_page;
typedef struct catalogue_item_s catalogue_item;
typedef struct catalogue_package_s catalogue_package;

struct catalogue_manager {
    List *pages;
    List *items;
    List *packages;
};

void catalogue_manager_init();
void catalogue_manager_add_page(catalogue_page *page);
void catalogue_manager_add_package(catalogue_package *package);
void catalogue_manager_add_item(catalogue_item *item);
catalogue_page *catalogue_manager_get_page_by_id(int id);
catalogue_page *catalogue_manager_get_page_by_index(char *index);
catalogue_item *catalogue_manager_get_item(char *sale_code);
List *catalogue_manager_get_pages();
List *catalogue_manager_get_packages();
void catalogue_manager_dispose();

#endif