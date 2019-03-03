#include <stdio.h>

#include "hashtable.h"
#include "shared.h"
#include "log.h"

#include "game/player/player.h"
#include "game/player/player_manager.h"

#include "communication/message_handler.h"

#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

#include "util/encoding/base64encoding.h"
#include "server/server_listener.h"


/**
 * Allocate buffer for reading data.
 *
 * @param handle the socket that the data is going to
 * @param size the size of the data
 * @param buf the buffer containing the data
 */
void server_alloc_buffer(uv_handle_t* handle, size_t size, uv_buf_t* buf) {
    buf->base = malloc(size);
    buf->len = size;
}

/**
 * Handle connection close.
 *
 * @param handle the session that closed
 */
void server_on_connection_close(uv_handle_t *handle) {
    entity *player = handle->data;
    player->disconnected = true;

    log_info("Client [%s] has disconnected", player->ip_address);
    player_cleanup(player);
}

/**
 * Cleanup buffer after writing data.
 *
 * @param req the write request buffer
 * @param status the status of the write
 */
void server_on_write(uv_write_t* req, int status) {
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
void server_on_read(uv_stream_t *handle, ssize_t nread, const uv_buf_t *buf) {
    if (nread == UV_EOF) {
        uv_close((uv_handle_t*) handle, server_on_connection_close);
        return;
    }

    if (nread == 0) {
        uv_close((uv_handle_t*) handle, server_on_connection_close);
        return;
    }

    if (nread > 0) {
        entity *player = handle->data;

        if (buf->base == NULL) {
            player->disconnected = true;
            return;
        }

        int amount_read = 0;

        while (amount_read < nread) {
            char recv_length[] = {
                    buf->base[amount_read++],
                    buf->base[amount_read++],
                    buf->base[amount_read++],
                    '\0'
            };

            int message_length = base64_decode(recv_length) + 1;


            if (message_length < 0 || message_length > 5120) {
                continue;
            }

            char *message = malloc(message_length * sizeof(char));

            for (int i = 0; i < message_length - 1; i++) {
                message[i] = buf->base[amount_read++];
            }

            message[message_length - 1] = '\0';

            if (player != NULL) {
                incoming_message *im = im_create(message);
                message_handler_invoke(im, player);
                im_cleanup(im);
            }

            free(message);
        }
    } else {
        uv_close((uv_handle_t *) handle, server_on_connection_close);
    }

    free(buf->base);
}

/**
 * Handle new connection handler.
 *
 * @param server the server to read the client
 * @param status the status of the client
 */
void server_on_new_connection(uv_stream_t *server, int status) {
    if (status == -1) {
        return;
    }

    uv_tcp_t *client = malloc(sizeof(uv_tcp_t));
    uv_tcp_init(uv_default_loop(), client);

    struct sockaddr_in client_addr;
    int client_addr_length;

    uv_stream_t *handle = (uv_stream_t*)client;
    uv_tcp_getpeername((const uv_tcp_t*) handle, (struct sockaddr*)&client_addr, &client_addr_length);

    char ip[256];
    uv_inet_ntop(AF_INET, &client_addr.sin_addr, ip, sizeof(ip));

    entity *p = player_manager_add(handle, ip);
    client->data = p;

    log_info("Client [%s] has connected", p->ip_address);
    int result = uv_accept(server, handle);

    if(result == 0) {
        outgoing_message *msg = om_create(0); // "@@"
        player_send(p, msg);
        om_cleanup(msg);

        uv_read_start(handle, server_alloc_buffer, server_on_read);
    } else {
        uv_close((uv_handle_t *) handle, server_on_connection_close);
    }
}

/**
 * Thread callback to start server on a different loop.
 *
 * @param arguments the server settings argument
 * @param arguments the server settings argument
 */
void listen_server(void *arguments)  {
    server_settings *args = (server_settings *)arguments;
    uv_loop_t *loop = uv_default_loop();

    uv_tcp_t server;
    struct sockaddr_in bind_addr;

    uv_tcp_init(loop, &server);
    uv_ip4_addr(args->ip, args->port, &bind_addr);
    uv_tcp_bind(&server, (const struct sockaddr*) &bind_addr, 0);
    uv_listen((uv_stream_t *) &server, 128, server_on_new_connection);

    uv_run(loop, UV_RUN_DEFAULT);
    uv_loop_close(loop);
}

/**
 * Create thread for server listener.
 *
 * @param settings the server settings
 * @param server_thread the thread to initialise
 */
void start_server(server_settings *settings, uv_thread_t *server_thread) {
    log_info("Starting server on port %i...", settings->port);

    if (uv_thread_create(server_thread, &listen_server, (void*) settings) != 0) {

        log_fatal("Uh-oh! Unable to spawn server thread");
    } else {
        log_info("Server successfully started!", settings->port);
    }
}