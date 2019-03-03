#ifndef CATALOGUE_PACKAGE_H
#define CATALOGUE_PACKAGE_H

typedef struct catalogue_package_s {
    char *sale_code;
    int definition_id;
    int special_sprite_id;
    int amount;
    item_definition *definition;
} catalogue_package;

catalogue_package *catalogue_package_create(char *salecode, int definition_id, int special_sprite_id, int amount);
void catalogue_package_dispose(catalogue_package *package);

#endif