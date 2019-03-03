#ifndef GAME_THREAD_H
#define GAME_THREAD_H

#include "uv.h"

void game_thread_init(uv_thread_t *thread);
void game_thread_loop(void *arguments);
void game_thread_task(unsigned long ticks);

#endif

