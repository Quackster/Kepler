#ifndef ROLLER_TASK_H
#define ROLLER_TASK_H

#include "game/room/room.h"
#include "game/room/room_user.h"

#include "game/items/item.h"

#include "game/pathfinder/coord.h"

void roller_task(room *room);
bool do_roller_item(room *room, item *roller, item *item);
void do_roller_player(room *room, item *roller, room_user *room_entity);

#endif