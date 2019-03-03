#include <stdio.h>
#include <ctype.h>
#include "list.h"

#include "hashtable.h"
#include "shared.h"
#include "log.h"

#include "util/encoding/base64encoding.h"

#include "server/rcon/rcon_listener.h"
#include "server/rcon/rcon_handler.h"

#include "server/server_listener.h"


/**
 * Allocate buffer for reading data.
 *
 * @param handle the socket that the data is going to
 * @param size the size of the data
 * @param buf the buffer containing the data
 */
void rcon_alloc_buffer(uv_handle_t* handle, size_t size, uv_buf_t* buf) {
    buf->base = malloc(size);
    buf->len = size;
}

/**
 * Handle connection close.
 *
 * @param handle the session that closed
 */
void rcon_on_connection_close(uv_handle_t *handle) {
    // TODO: log IP
    log_debug("RCON connection disconnected");
}

/**
 * Cleanup buffer after writing data.
 *
 * @param req the write request buffer
 * @param status the status of the write
 */
void rcon_on_write(uv_write_t* req, int status) {
    free(req->data);
    free(req);
}

/**
 * Read incoming data from socket.
 *
 * @param handle the socket to read from
 * @param nread the amount of bytes read
 * @param buf the buffer containing the data
 */
void rcon_on_read(uv_stream_t *handle, ssize_t nread, const uv_buf_t *buf) {
    if (nread == UV_EOF) {
        uv_close((uv_handle_t *) handle, rcon_on_connection_close);
        return;
    }
    if (nread > 0) {
        char header = buf->base[0];

        char *message = NULL;

        if (nread > 1) {
            message = strdup(buf->base + 1);
        } else {
            message = strdup("");
        }
        // char data[255] = buf->base + 1;
        /*char *message = malloc((nread + 1) * sizeof(char));

        log_debug("%u", nread);

        for (int i = 0; i < nread; i++) {
            message[i] = buf->base[read++];
        }

        message[nread] = '\0';*/

        log_debug("RCON Command: %c, data: %s", header, message);
        rcon_handle_command(handle, header, message);

        free(message);
    } else {
        uv_close((uv_handle_t *) handle, rcon_on_connection_close);
    }

    free(buf->base);
}

/**
 * Handle new connection handler.
 *
 * @param server the server to read the client
 * @param status the status of the client
 */
void rcon_on_new_connection(uv_stream_t *server, int status) {
    if (status == -1) {
        return;
    }

    uv_tcp_t *client = malloc(sizeof(uv_tcp_t));
    uv_tcp_init(global.rcon_loop, client);

    uv_stream_t *handle = (uv_stream_t*)client;
    int result = uv_accept(server, handle);

    if(result == 0) {
        struct sockaddr_in client_addr;
        int client_addr_length;

        // uv_stream_t *handle = (uv_stream_t*)client;
        // char ip[256];
        // uv_inet_ntop(AF_INET, &client_addr.sin_addr, ip, sizeof(ip));
        //
        // log_debug("RCON [%s] has connected", ip);

        uv_read_start(handle, rcon_alloc_buffer, rcon_on_read);
    } else {
        uv_close((uv_handle_t *) handle, rcon_on_connection_close);
    }
}

/**
 * Thread callback to start server on a different loop.
 *
 * @param arguments the server settings argument
 */
void listen_rcon(void *arguments)  {
    server_settings *args = (server_settings *)arguments;
    global.rcon_loop = uv_loop_new();
    uv_loop_t *loop = global.rcon_loop;

    uv_tcp_t server;
    struct sockaddr_in bind_addr;

    uv_tcp_init(loop, &server);
    uv_ip4_addr(args->ip, args->port, &bind_addr);
    uv_tcp_bind(&server, (const struct sockaddr*) &bind_addr, 0);
    uv_listen((uv_stream_t *) &server, 128, rcon_on_new_connection);

    uv_run(loop, UV_RUN_DEFAULT);
    uv_loop_close(loop);
}

/**
 * Create thread for server listener.
 *
 * @param settings the server settings
 * @param rcon_thread the thread to initialise
 */
void start_rcon(server_settings *settings, uv_thread_t *rcon_thread) {
    log_info("Starting RCON on port %i...", settings->port);

    if (uv_thread_create(rcon_thread, listen_rcon, (void*) settings) != 0) {
        log_fatal("Uh-oh! Unable to spawn RCON thread");
    } else {
        log_info("RCON successfully started!", settings->port);
    }
}