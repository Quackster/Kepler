#include "room_task.h"
#include "log.h"
#include "list.h"

#include "shared.h"
#include "game/room/room.h"
#include "game/room/tasks/roller_task.h"
#include "game/room/tasks/status_task.h"
#include "game/room/tasks/walk_task.h"

/**
 * Start all room tasks if they haven't started already.
 *
 * @param room the room for the task to run inside
 */
void room_start_tasks(room *room) {
    if (room->walk_process_timer == NULL) {
        room->walk_process_timer = hh_dispatch_timer_create(RoomDispatch, (hh_dispatch_cb_t) &walk_task, (void *) room);
        hh_dispatch_timer_start(room->walk_process_timer, 0, 500);
    }

    if (room->roller_process_timer == NULL) {
        room->roller_process_timer = hh_dispatch_timer_create(RoomDispatch, (hh_dispatch_cb_t) &roller_task, (void *) room);
        hh_dispatch_timer_start(room->roller_process_timer, 0, 3000);
    }

    if (room->status_process_timer == NULL) {
        room->status_process_timer = hh_dispatch_timer_create(RoomDispatch, (hh_dispatch_cb_t) &status_task, (void *) room);
        hh_dispatch_timer_start(room->status_process_timer, 0, 1000);
    }
}

/**
 * Stop all room tasks if they haven't stopped already.
 *
 * @param room the room for the task to run inside
 */
void room_stop_tasks(room *room) {
    if (room->walk_process_timer != NULL) {
        hh_dispatch_timer_dispose(room->walk_process_timer);
        room->walk_process_timer = NULL;
    }

    if (room->status_process_timer != NULL) {
        hh_dispatch_timer_dispose(room->status_process_timer);
        room->status_process_timer = NULL;
    }

    if (room->roller_process_timer != NULL) {
        hh_dispatch_timer_dispose(room->roller_process_timer);
        room->roller_process_timer = NULL;
    }

}