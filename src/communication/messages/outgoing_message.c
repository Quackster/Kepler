#include "outgoing_message.h"

#include "util/stringbuilder.h"

#include "util/encoding/base64encoding.h"
#include "util/encoding/vl64encoding.h"

#include <shared.h>

/**
 * Creates an outgoing message struct with the specified header
 * @param header the b64 header
 * @return the outgoing message
 */
outgoing_message *om_create(int header) {
    outgoing_message *om = malloc(sizeof(outgoing_message));
    om->header_id = header;
    om->header = base64_encode(om->header_id, 2);
    om->finalised = 0;
    om->sb = sb_create();

    sb_add_string(om->sb, om->header);
    return om;
}

/**
 * Writes a string to the outgoing message with the default special character delimeter.
 *
 * @param om the outgoing message
 * @param str the string to write
 */
void om_write_str(outgoing_message *om, char *str) {
    if (str == NULL) {
        om_write_str_delimeter(om, "null", 2);
        return;
    }

    om_write_str_delimeter(om, str, 2);
}

void om_write_str_kv(outgoing_message *om, char *key, char *value) {
    sb_add_string(om->sb, key);
    sb_add_string(om->sb, ":");
    sb_add_string(om->sb, value);
    sb_add_char(om->sb, 13);
}

/**
 * Writes a string to the outgoing message with a custom character delimeter.
 *
 * @param om the outgoing message
 * @param str the string to write
 */
void om_write_str_delimeter(outgoing_message *om, char *str, int delim) {
    sb_add_string(om->sb, str);
    sb_add_char(om->sb, delim);
}

/**
 * Writes a int to the outgoing message with a custom character delimeter.
 *
 * @param om the outgoing message
 * @param str the string to write
 */
void om_write_int_delimeter(outgoing_message *om, int num, int delim) {
    char var[11];
    sprintf(var, "%i", num);

    sb_add_string(om->sb, var);
    sb_add_char(om->sb, delim);
}

void om_write_char(outgoing_message *om, int character) {
    sb_add_char(om->sb, character);
}

/**
 * Writes a integer as a string to the outgoing message
 * @param om the outgoing message
 * @param str the string to write
 */
void om_write_str_int(outgoing_message *om, int num) {
    char var[11];
    sprintf(var, "%i", num);
    om_write_str(om, var);
}

/**
 * Writes a int to the outgoing message
 * @param om the outgoing message
 * @param str the int to write
 */
void om_write_int(outgoing_message *om, int num) {
    char *encoded = vl64_encode(num);
    sb_add_string(om->sb, encoded);
    free(encoded);
}

/**
 * Finalise the packet before sending, by adding a character suffix at the end.
 * @param om the outgoing message
 */
void om_finalise(outgoing_message *om) {
    if (om == NULL) {
        return;
    }

    if (om->finalised) {
        return;
    }

    sb_add_string(om->sb, "\1");
    om->finalised = true;
}

/**
 * Cleanup any variables loaded on the heap that had to do with this struct
 * @param om the outgoing message
 */
void om_cleanup(outgoing_message *om) {
    sb_cleanup(om->sb);
    free(om->header);
    free(om);
    
    om = NULL;
}