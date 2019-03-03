#ifndef RCON_HANDLER_H
#define RCON_HANDLER_H

#include "uv.h"

void rcon_handle_command(uv_stream_t *handle, char header, char *message);
void rcon_send(uv_stream_t *handle, char *data);


#endif