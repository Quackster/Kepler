#include "shared.h"
#include "vl64encoding.h"

int vl64_decode(const char *value, int *total_bytes) {
    int result;

    int is_negative = (value[0] & 4) == 4;
    *total_bytes = (value[0] >> 3) & 7;

    result = value[0] & 3;

    int shift_amount = 2;
    for (int i = 1; i < *total_bytes; i++) {
        result |= (value[i] & 0x3f) << shift_amount;
        shift_amount += 6;
    }

    if (is_negative) {
        result = -result;
    }

    return result;
}

char *vl64_encode(int value) {
    char encoded[6];
    char byte_count = 1;
    int absolute_value = abs(value);

    encoded[0] = (char) (0x40 + (absolute_value & 3));
    encoded[0] |= value < 0 ? 4 : 0;

    for (absolute_value >>= 2; absolute_value != 0; absolute_value >>= 6) {
        encoded[byte_count++] = (char) (0x40 + (absolute_value & 0x3f));
    }

    encoded[0] |= byte_count << 3;

    char *str = malloc(byte_count + 1 *sizeof(char));

    for (int i = 0; i < byte_count; i++) {
        str[i] = encoded[i];
    }

    str[byte_count] = '\0';
    return str;
}