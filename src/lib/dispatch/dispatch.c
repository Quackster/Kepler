#include "dispatch.h"

#include <stdlib.h>
#include <unistd.h>
#include <main.h>

int runner_count = 0;
hh_dispatch_loop_group_t game_dispatch;
hh_dispatch_loop_group_t room_dispatch;
hh_dispatch_loop_group_t storage_dispatch;

void dispatch_wake(void *data) {

}

void dispatch_exec_callback(uv_async_t *handle) {
    if (handle->data != NULL) {
        hh_dispatch_work_t *work = (hh_dispatch_work_t *) handle->data;

        work->cb(work->data);

        free(work);
        free(handle);
    }
}

void dispatch_exec_scheduled_callback(uv_timer_t *handle) {
    if (handle->data != NULL) {
        hh_dispatch_timer_t *timer = (hh_dispatch_timer_t *) handle->data;

        timer->work->cb(timer->work->data);
    }
}

void dispatch_initialise_loop_thread(void *data) {
    hh_dispatch_loop_t *loop = (hh_dispatch_loop_t *) data;

    uv_async_t async = {
            .data = NULL
    };

    uv_timer_t timer;

    uv_async_init(loop->loop, &async, &dispatch_exec_callback);
    uv_async_send(&async);

    uv_timer_init(loop->loop, &timer);
    uv_timer_start(&timer, (uv_timer_cb) &dispatch_wake, 500, 500);

    int r = uv_run(loop->loop, UV_RUN_DEFAULT);
}

void dispatch_initialise_loops(hh_dispatch_loop_group_t *group, int count) {
    // setup dispatch with counts
    group->total_loops = count;
    group->current_index = 0;
    group->mutex = (uv_mutex_t *) malloc(sizeof(uv_mutex_t));

    uv_mutex_init(group->mutex);

    group->loops = calloc((size_t) count, sizeof(hh_dispatch_loop_t));

    for (int i = 0; i < count; i++) {
        hh_dispatch_loop_t *loop = malloc(sizeof(hh_dispatch_loop_t));

        loop->id = ++runner_count;
        loop->loop = malloc(sizeof(uv_loop_t));
        loop->thread = malloc(sizeof(uv_thread_t));

        uv_loop_init(loop->loop);
        uv_thread_create(loop->thread, &dispatch_initialise_loop_thread, loop);

        loop->loop->data = loop;
        group->loops[i] = loop;
    }
}

void hh_dispatch_initialise(int game_dispatch_count, int room_dispatch_count,
                            int storage_dispatch_count) {
    dispatch_initialise_loops(&game_dispatch, game_dispatch_count);
    dispatch_initialise_loops(&room_dispatch, room_dispatch_count);
    dispatch_initialise_loops(&storage_dispatch, storage_dispatch_count);
}

void hh_dispatch_shutdown() {

}

hh_dispatch_loop_group_t *dispatch_get_group(char group) {
    switch (group) {
        default:
            return &game_dispatch;

        case GameDispatch:
            return &game_dispatch;
        case RoomDispatch:
            return &room_dispatch;
        case StorageDispatch:
            return &storage_dispatch;
    }
}

hh_dispatch_loop_t *dispatch_group_next_loop(hh_dispatch_loop_group_t *group) {
    hh_dispatch_loop_t *loop;

    uv_mutex_lock(group->mutex);

    if ((group->current_index + 1) == group->total_loops) {
        group->current_index = 0;
    } else {
        group->current_index++;
    }

    loop = group->loops[group->current_index];
    uv_mutex_unlock(group->mutex);

    return loop;
}

int hh_dispatch(char group_id, hh_dispatch_cb_t cb, void *data) {
    hh_dispatch_loop_group_t *group = dispatch_get_group(group_id);
    hh_dispatch_loop_t *loop = dispatch_group_next_loop(group);

    if (loop != NULL) {
        hh_dispatch_work_t *work = malloc(sizeof(hh_dispatch_work_t));
        uv_async_t *async = malloc(sizeof(uv_async_t));

        work->cb = cb;
        work->data = data;

        async->data = work;

        uv_async_init(loop->loop, async, &dispatch_exec_callback);
        uv_async_send(async);
        return 0;
    }

    return 1;
}

hh_dispatch_timer_t *hh_dispatch_timer_create(char group_id, hh_dispatch_cb_t cb, void *data) {
    hh_dispatch_loop_group_t *group = dispatch_get_group(group_id);
    hh_dispatch_loop_t *loop = dispatch_group_next_loop(group);

    if (loop != NULL) {
        hh_dispatch_timer_t *handle = malloc(sizeof(hh_dispatch_timer_t));
        hh_dispatch_work_t *work = malloc(sizeof(hh_dispatch_work_t));
        uv_timer_t *timer = malloc(sizeof(uv_timer_t));

        work->cb = cb;
        work->data = data;

        handle->work = work;
        handle->handle = timer;

        timer->data = handle;

        uv_timer_init(loop->loop, timer);
        return handle;
    }

    return NULL;
}

int hh_dispatch_timer_start(hh_dispatch_timer_t *handle, int initial_delay, int delay) {
    return uv_timer_start(handle->handle, &dispatch_exec_scheduled_callback, (uint64_t) initial_delay, (uint64_t) delay);
}

int hh_dispatch_timer_dispose(hh_dispatch_timer_t *handle) {
    if (handle == NULL) {
        return -1;
    }

    uv_timer_stop(handle->handle);

    free(handle->work);
    free(handle->handle);

    free(handle);
}