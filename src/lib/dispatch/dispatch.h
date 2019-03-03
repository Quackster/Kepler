#pragma once

/*
 * HH_DISPATCH
 * @Author Leon Hartley
 */

#include <uv.h>

/*
 *  GameDispatch is the main loop which all messages are handled on,
 *  which means pretty much everything other than room stuff happens here.
 */
#define GameDispatch 0

/*
 *  RoomDispatch is the loop which room processing is handled on.
 */
#define RoomDispatch 1

/*
 *  Storage dispatch is used for asynchronous calls to the database
 */
#define StorageDispatch 2

typedef void (*hh_dispatch_cb_t) (void *data);

typedef struct hh_dispatch_work_s {
    void *data;
    hh_dispatch_cb_t cb;
} hh_dispatch_work_t;

typedef struct hh_dispatch_loop_t {
    uv_thread_t *thread;
    uv_loop_t *loop;
    int id;
} hh_dispatch_loop_t;

typedef struct hh_dispatch_loop_group_s {
    int total_loops;
    int current_index;

    uv_mutex_t *mutex;
    hh_dispatch_loop_t **loops;
} hh_dispatch_loop_group_t;

typedef struct hh_dispatch_timer_s {
    hh_dispatch_work_t *work;
    uv_timer_t *handle;
} hh_dispatch_timer_t;

void hh_dispatch_initialise(int game_dispatch_count, int room_dispatch_count,
                            int storage_dispatch_count);

int hh_dispatch(char group, hh_dispatch_cb_t cb, void *data);

hh_dispatch_timer_t *hh_dispatch_timer_create(char group, hh_dispatch_cb_t cb, void *data);

int hh_dispatch_timer_start(hh_dispatch_timer_t *handle, int initial_delay, int delay);

int hh_dispatch_timer_dispose(hh_dispatch_timer_t *handle);

void hh_dispatch_shutdown();
