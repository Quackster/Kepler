#ifndef PLAYER_H
#define PLAYER_H

#include <stdbool.h>
#include <time.h>

typedef struct outgoing_message_s outgoing_message;

typedef enum {
    BOT_TYPE,
    PLAYER_TYPE,
    PET_TYPE
} entity_type;

typedef struct entity_details_s {
    int id;
    char *username;
    char *password;
    char *figure;
    char *pool_figure;
    int credits;
    char *motto;
    char *sex;
    int tickets;
    int film;
    int rank;
    char *console_motto;
    unsigned long last_online;
    time_t club_subscribed;
    time_t club_expiration;
    char *badge;
    bool badge_active;
} entity_data;

typedef struct entity_s {
    void *stream;
    char *ip_address;
    entity_type entity_type;
    struct entity_details_s *details;
    struct messenger_s *messenger;
    struct inventory_s *inventory;
    struct room_user_s *room_user;
    bool logged_in;
    bool disconnected;
    bool ping_safe;
    time_t last_stalk;
} entity;

entity *player_create(void*, char*);
entity_data *player_create_data(int, char*, char*, char*, char*, int, char*, char*, int, int, int, char*, unsigned long, unsigned long, unsigned long, char*, bool);
void player_login(entity *player);
bool player_has_fuse(entity *player, char *fuse_right);
void player_disconnect(entity *player);
void player_send(entity *, outgoing_message *);
void player_cleanup(entity*);
void player_data_cleanup(entity_data*);

#endif