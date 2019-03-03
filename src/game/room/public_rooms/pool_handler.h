#ifndef POOL_HANDLER_H
#define POOL_HANDLER_H

typedef struct entity_s entity;
typedef struct item_s item;
typedef struct room_s room;

void pool_booth_exit(entity*);
void pool_item_walk_on(entity *, item *);
void pool_setup_redirections(room*, item*);

#endif