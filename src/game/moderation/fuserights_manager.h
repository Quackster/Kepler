#ifndef FUSERIGHTS_MANAGER_H
#define FUSERIGHTS_MANAGER_H

#include <stdbool.h>

typedef struct list_s List;
typedef struct outgoing_message_s outgoing_message;

typedef struct fuseright_s {
    int min_rank;
    char *fuse_right;
} fuseright;

struct fuserights_manager {
    List *fuserights;
};

void fuserights_init();
fuseright *fuserights_create(int rank, char *fuseright);
void fuserights_append(int rank, outgoing_message *om);
bool fuserights_has_permission(int rank, char *fuseright);

#endif

