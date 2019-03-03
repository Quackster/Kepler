#include <stdlib.h>
#include "limits.h"

#include "node.h"

node *create_node() {
    node *current = malloc(sizeof(node));
    current->cost = INT_MAX;
    current->open = 0;
    current->closed = 0;
    current->x = 0;
    current->y = 0;
    current->node = NULL;
    return current;
}