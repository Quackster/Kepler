#include <shared.h>

#include "util/stringbuilder.h"
#include "util/encoding/vl64encoding.h"

/**
 * Creates a stringbuilder instance
 * @return the stringbuilder
 */
stringbuilder *sb_create() {
    stringbuilder *sb = malloc(sizeof(stringbuilder));
    sb->capacity = 1024;
    
    sb->data = malloc(sb->capacity * sizeof(char));
    memset(sb->data, 0, sizeof(sb->data));

    sb->index = 0;
    return sb;
}

/**
 * Checks the capacity of the buffer, if there is not a sufficient amount, the buffer will be expanded.
 * @param sb the stringbuilder
 * @param length the new length required
 */
void sb_ensure_capacity(stringbuilder *sb, int length) {
    if ((sb->capacity - sb->index) >= length) {
        return;
    }

    sb->capacity = sb->capacity + length;
    sb->data = realloc(sb->data, sb->capacity * sizeof(char));
}

/**
 * Adds a string to the stringbuilder
 * @param sb the stringbuilder
 * @param data the string
 */
void sb_add_string(stringbuilder *sb, const char *data) {
    if (data == NULL) {
        return;
    }

    sb_ensure_capacity(sb, strlen(data) + 1); //+1 for terminated string

    for (int i = 0; i < strlen(data); i++) {
        sb->data[sb->index++] = data[i];
    }

    // zero terminated string
    sb->data[sb->index] = '\0';
}

/**
 * Adds an integer to the stringbuilder
 * @param sb the stringbuilder
 * @param integer the int
 */
void sb_add_int(stringbuilder *sb, int integer) {
    char data[11];
    sprintf(data, "%i", integer);
    data[10] = '\0';
    sb_add_string(sb, data);
}

/**
 * Adds a wired to the stringbuilder
 * @param sb the stringbuilder
 * @param integer the int
 */
void sb_add_wired(stringbuilder *sb, int integer) {
    char *encoded = vl64_encode(integer);
    sb_add_string(sb, encoded);
    free(encoded);
}

/**
 * Adds an double to the stringbuilder
 *
 * @param sb the stringbuilder
 * @param d the double
 */
void sb_add_float(stringbuilder *sb, double d) {
    char data[11];
    sprintf(data, "%.2f", d);
    data[10] = '\0';
    sb_add_string(sb, data);
}

/**
 * Adds an integer to the stringbuilder
 * @param sb the stringbuilder
 * @param integer the int
 */
void sb_add_char(stringbuilder *sb, int num) {
    char data[2];
    data[0] = (char)num;
    data[1] = '\0';
    sb_add_string(sb, data);
}

/**
 * Adds a float to the stringbuilder with a delimeter
 *
 * @param sb the stringbuilder
 * @param d the float
 * @paam delim the delimeter
 */
void sb_add_float_delimeter(stringbuilder *sb, double d, char delim) {
    sb_add_float(sb, d);
    sb_add_char(sb, delim);
}
/**
 * Adds a string to the stringbuilder with a delimeter
 *
 * @param sb the stringbuilder
 * @param data the string
 * @paam delim the delimeter
 */
void sb_add_string_delimeter(stringbuilder *sb, const char *data, char delim) {
    sb_add_string(sb, data);
    sb_add_char(sb, delim);
}

/**
 * Adds an integer to the stringbuilder with a delimeter
 *
 * @param sb the stringbuilder
 * @param integer the int
 * @paam delim the delimeter
 */
void sb_add_int_delimeter(stringbuilder *sb, int integer, char delim) {
    sb_add_int(sb, integer);
    sb_add_char(sb, delim);
}

/**
 * Adds a wired to the stringbuilder with a delimeter
 *
 * @param sb the stringbuilder
 * @param integer the int
 * @paam delim the delimeter
 */
void sb_add_wired_delimeter(stringbuilder *sb, int integer, char delim) {
    sb_add_wired(sb, integer);
    sb_add_char(sb, delim);
}

/**
 * Cleanup any variables loaded on the heap that had to do with this struct
 * @param sb the stringbuilder
 */
void sb_cleanup(stringbuilder *sb) {
    if (sb == NULL) {
        return;
    }

    free(sb->data);
    free(sb);
}