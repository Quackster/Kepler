#ifndef SERVER_LISTENER_H
#define SERVER_LISTENER_H

#include "uv.h"

typedef struct server_settings_s {
    char ip[255];
    int port;
} server_settings;

void server_on_new_connection(uv_stream_t *server, int status);
void server_on_read(uv_stream_t *handle, ssize_t nread, const uv_buf_t *buf);
void server_alloc_buffer(uv_handle_t* handle, size_t  size, uv_buf_t* buf);
void server_on_connection_close(uv_handle_t *handle);
void server_on_write(uv_write_t* req, int status);
void start_server(server_settings *settings, uv_thread_t *server_thread);

#endif