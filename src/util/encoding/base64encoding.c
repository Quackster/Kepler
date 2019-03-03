#include "shared.h"
#include "base64encoding.h"

int base64_decode(char *value) {
    int result = 0;

    for (int i = 0; i < strlen(value); i++) {
        result += ((value[i] - 0x40) << 6 * (strlen(value) - 1 - i));
    }

    return result;
}


char *base64_encode(int value, int length) {
    char *encoded = malloc(length + 1 *sizeof(char));

    int slot = 0;
    int sub_value;

    for (int i = 0; i < length; i++) {
        sub_value = (value >> 6 * (length - 1 - i)) & 0x3f;
        encoded[slot++] = (char) (sub_value + 0x40);
    }

    encoded[2] = '\0';
    return encoded;
}