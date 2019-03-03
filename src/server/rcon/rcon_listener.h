#ifndef RCON_LISTENER_H
#define RCON_LISTENER_H

#include "uv.h"

typedef struct server_settings_s server_settings;

void rcon_on_new_connection(uv_stream_t *server, int status);
void rcon_on_read(uv_stream_t *handle, ssize_t nread, const uv_buf_t *buf);
void rcon_alloc_buffer(uv_handle_t* handle, size_t  size, uv_buf_t* buf);
void rcon_on_connection_close(uv_handle_t *handle);
void rcon_send(uv_stream_t *handle, char *data);
void rcon_on_write(uv_write_t* req, int status);
void start_rcon(server_settings *settings, uv_thread_t *rcon_thread);

#endif