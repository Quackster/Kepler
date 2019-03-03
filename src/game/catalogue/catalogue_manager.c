#include "shared.h"

#include "list.h"
#include "hashtable.h"

#include "database/queries/catalogue_query.h"

#include "catalogue_manager.h"
#include "catalogue_page.h"
#include "catalogue_item.h"
#include "catalogue_package.h"

/**
 * Create the catalogue manager instance and load the pages.
 */
void catalogue_manager_init() {
    list_new(&global.catalogue_manager.pages);
    list_new(&global.catalogue_manager.packages);
    list_new(&global.catalogue_manager.items);

    catalogue_query_pages();
    catalogue_query_packages();
    catalogue_query_items();

}

/**
 * Add a page by it's given catalogue page struct.
 *
 * @param page the catalogue page struct
 */
void catalogue_manager_add_page(catalogue_page *page) {
    list_add(global.catalogue_manager.pages, page);
}

/**
 * Add a package by it's given catalogue package struct.
 *
 * @param package the catalogue package struct
 */
void catalogue_manager_add_package(catalogue_package *package) {
    list_add(global.catalogue_manager.packages, package);
}

/**
 * Add catalogue item to the catalogue page hashtable and the defaut hashtable.
 *
 * @param item the item to add
 */
void catalogue_manager_add_item(catalogue_item *item) {
    list_add(global.catalogue_manager.items, item);

    catalogue_page *page = catalogue_manager_get_page_by_id(item->page_id);

    if (page != NULL) {
        list_add(page->items, item);
    }
}

/**
 * Get catalogue page by it's ID.
 *
 * @param id the catalogue page id
 * @return the catalogue page
 */
catalogue_page *catalogue_manager_get_page_by_id(int id) {
    for (size_t i = 0; i < list_size(global.catalogue_manager.pages); i++) {
        catalogue_page *page = NULL;
        list_get_at(global.catalogue_manager.pages, i, (void *) &page);

        if (page->id == id) {
            return page;
        }
    }

    return NULL;
}

/**
 * Get the catalogue page by it's requested catalogue index.
 *
 * @param page_index the page index
 * @return the catalogue page struct
 */
catalogue_page *catalogue_manager_get_page_by_index(char *page_index) {
    for (size_t i = 0; i < list_size(global.catalogue_manager.pages); i++) {
        catalogue_page *page = NULL;
        list_get_at(global.catalogue_manager.pages, i, (void *) &page);

        if (strcmp(page->name_index, page_index) == 0) {
            return page;
        }
    }

    return NULL;
}

/**
 * Get the catalogue item by sale code.
 *
 * @param sale_code the sale code
 * @return the catalogue item
 */
catalogue_item *catalogue_manager_get_item(char *sale_code) {
    for (size_t i = 0; i < list_size(global.catalogue_manager.items); i++) {
        catalogue_item *item = NULL;
        list_get_at(global.catalogue_manager.items, i, (void *) &item);

        if (strcmp(item->sale_code, sale_code) == 0) {
            return item;
        }
    }
    return NULL;
}

/**
 * Get the list of catalogue packages.
 *
 * @return the list of catalogue packages
 */
List *catalogue_manager_get_packages() {
    return global.catalogue_manager.packages;
}


/**
 * Get the entire list of catalogue pages
 *
 * @return list of pages
 */
List *catalogue_manager_get_pages() {
    return global.catalogue_manager.pages;
}


/**
 * Dispose model manager
 */
void catalogue_manager_dispose() {
    for (size_t i = 0; i < list_size(global.catalogue_manager.pages); i++) {
        catalogue_page *page = NULL;
        list_get_at(global.catalogue_manager.pages, i, (void *) &page);
        catalogue_page_dispose(page);
    }

    for (size_t i = 0; i < list_size(global.catalogue_manager.items); i++) {
        catalogue_item *item = NULL;
        list_get_at(global.catalogue_manager.items, i, (void *) &item);
        catalogue_item_dispose(item);
    }

    for (size_t i = 0; i < list_size(global.catalogue_manager.packages); i++) {
        catalogue_package *package = NULL;
        list_get_at(global.catalogue_manager.packages, i, (void *) &package);
        catalogue_package_dispose(package);
    }

    list_destroy(global.catalogue_manager.pages);
    list_destroy(global.catalogue_manager.packages);
    list_destroy(global.catalogue_manager.items);
}