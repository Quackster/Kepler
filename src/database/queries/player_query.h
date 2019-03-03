#ifndef PLAYER_QUERY_H
#define PLAYER_QUERY_H

#include "array.h"

typedef struct entity_s entity;
typedef struct entity_details_s entity_data;

char *player_query_username(int user_id);
int player_query_id(char *username);
int player_query_login(char *username, char *password);
int player_query_sso(char *ticket);
int player_query_exists_username(char *username);
int player_query_create(char *username, char *password, char *figure, char *motto);
entity_data *player_query_data(int id);
void player_query_save_last_online(entity *);
void player_query_save_details(entity *player);
void player_query_save_motto(entity *player);
void player_query_save_currency(entity *player);
void player_query_save_tickets(int id, int tickets);
Array *player_query_badges(int id);
void player_query_save_badge(entity *player);
void player_query_save_club_information(entity *player);

#endif