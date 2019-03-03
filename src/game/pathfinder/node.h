typedef struct node_s node;

typedef struct node_s {
    node *node;
    int cost;
    int open;
    int closed;
    int x;
    int y;
} node;


node *create_node();