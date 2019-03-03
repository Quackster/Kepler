#include "incoming_message.h"

#include "util/encoding/base64encoding.h"
#include "util/encoding/vl64encoding.h"

/**
 * Creates an incoming message give by a char array
 * @param message the char array
 * @return incoming_message struct
 */
incoming_message *im_create(char *message) {
    incoming_message *im = malloc(sizeof(incoming_message));
    im->data = message;
    im->counter = 0;
    im->total_length = (int)strlen(message);
    im->header = im_read_b64(im);
    im->header_id = base64_decode(im->header);
    return im;
}

/**
 * Read base64 character, use free() on this returned value after using this.
 *
 * @param im the incoming message
 * @return the base64 value
 */
char *im_read_b64(incoming_message *im) {
    if ((im->counter - 2) > im->total_length) {
        return NULL;
    }

    char data[] = {
        im->data[im->counter++],
        im->data[im->counter++],
        '\0'
    };

    return strdup(data);
}

/**
 * Read a B64 as an integer.
 *
 * @param im the incoming message
 * @return the integer
 */
int im_read_b64_int(incoming_message *im) {
    if ((im->counter - 2) > im->total_length) {
        return -1;
    }

    char data[] = {
        im->data[im->counter++],
        im->data[im->counter++],
        '\0'
    };

    return base64_decode(data);
}

/**
 * Read vl64 character as an integer
 *
 * @param im the incoming message
 * @return the integer value
 */
int im_read_vl64(incoming_message *im) {
    if ((im->counter - 2) > im->total_length) {
        return -1;
    }

    char *str = im_get_content(im);

    int length;
    int val = vl64_decode(str, &length);
    im->counter += length;

    free(str);
    return val;
}

/**
 * Get rest of the packet, without header
 *
 * @param im the incoming message
 * @return the integer value
 */
char *im_get_content(incoming_message *im) {
    if ((im->counter - 2) > im->total_length) {
        return NULL;
    }

    int substring = im->counter;
    int new_len = (int)strlen(im->data) - substring;

    char *new_str = malloc((new_len + 1) * sizeof(char));
    memcpy(new_str, &im->data[substring], strlen(im->data) - substring);
    new_str[new_len] = '\0';

    return new_str;
}

/**
 * Read base64 character, use free() on this returned value after using this.
 *
 * @param im the incoming message
 * @return the base64 value
 */
char *im_read_str(incoming_message *im) {
    if ((im->counter - 2) > im->total_length) {
        return NULL;
    }

    char *recv_length = im_read_b64(im);
    int length = base64_decode(recv_length);

    char *str = malloc((length + 1) * sizeof(char));

    if (str) {
        for (int i = 0; i < length; i++) {
            str[i] = im->data[im->counter++];
        }

        str[length] = '\0';
        free(recv_length);

        return str;
    } else {
        return NULL;
    }
}

/**
 * Used for skipping over garbage data.
 *
 * @param im the incoming message instance
 * @param amount_read the amount of bytes to read
 * @return none
 */
void *im_read(incoming_message *im, int amount_read) {
    im->counter += amount_read;
}

/**
 * Cleanup any variables loaded on the heap that had to do with this struct
 * @param im the incoming message
 */
void im_cleanup(incoming_message *im) {
    free(im->header);
    free(im);
}
