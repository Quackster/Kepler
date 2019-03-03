#ifndef PLAYER_REFRESH_H
#define PLAYER_REFRESH_H

typedef struct entity_s entity;

void player_send_localised_error(entity *p, char *error);
void player_send_alert(entity *p, char *text);
void player_refresh_credits(entity *player);
void player_refresh_tickets(entity *player);
void player_refresh_appearance(entity *player);
void player_refresh_badges(entity *player);

#endif