#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "shared.h"

#include "list.h"
#include "hashtable.h"

#include "communication/messages/outgoing_message.h"
#include "game/player/player.h"

#include "catalogue_page.h"

/**
 *
 * @param id
 * @param min_role
 * @param name_index
 * @param name
 * @param layout
 * @param image_headline
 * @param image_teasers
 * @param body
 * @param label_pick
 * @param label_extra_s
 * @param label_extra_t
 * @return
 */
catalogue_page *catalogue_page_create(int id, int min_role, char *name_index, char *name, char *layout, char *image_headline, char *image_teasers, char *body, char *label_pick, char *label_extra_s, char *label_extra_t) {
    catalogue_page *page = malloc(sizeof(catalogue_page));
    page->id = id;
    page->min_role = min_role;
    page->name_index = name_index;
    page->name = name;
    page->layout = layout;
    page->image_headline = image_headline;
    page->image_teasers = image_teasers;
    page->body = body;
    page->label_pick = label_pick;
    page->label_extra_s = label_extra_s;

    hashtable_new(&page->label_extra);
    list_new(&page->items);

    if (label_extra_t != NULL) {
        char *new_line = strtok(label_extra_t,"\r\n");

        while(new_line != NULL) {
            // Get ID
            int index = (int)strcspn (new_line, ":");
            char z_id[10];
            memcpy(z_id, &new_line[0], (size_t)index + 1);
            z_id[index] = '\0';

            // Get the value, past the ":", jesus christ this messy as fuck,
            // sorry for anyone trying to understand this terrible code.
            size_t new_length = strlen(new_line) - (index + 1);
            char *z_data = malloc(new_length + 1 * sizeof(char));
            strcpy(z_data, new_line + ((size_t)index + 1));

            char key[25]; // "label_extra_t_" + 10 chars for integer and 1 for /0 ending
            sprintf(key, "label_extra_t_%s", z_id);

            hashtable_add(page->label_extra, strdup(key), strdup(z_data));
            new_line = strtok(NULL,"\r\n");
        }
    }

    free(label_extra_t);
    return page;
}

/**
 * Dispose catalogue page.
 *
 * @param page the catalogue page to dispose
 */
void catalogue_page_dispose(catalogue_page *page) {
    free(page->name_index);
    free(page->name);
    free(page->layout);
    free(page->image_headline);
    free(page->image_teasers);
    free(page->body);
    free(page->label_pick);
    free(page->label_extra_s);


    if (hashtable_size(page->label_extra) > 0) {
        HashTableIter iter;
        TableEntry *entry;

        hashtable_iter_init(&iter, page->label_extra);

        while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
            free(entry->key);
            free(entry->value);
        }
    }

    hashtable_destroy(page->label_extra);
    list_destroy(page->items);
    free(page);
}