#include "shared.h"
#include "string.h"

#include "database/queries/player_query.h"
#include "util/configuration/configuration.h"

#include <stdarg.h>
#include <ctype.h>
#include <time.h>


/**
 * Get the current time formatted, must be free'd at the end.
 *
 * @return the time as formatted
 */
char *get_time_formatted() {
    char buff[20];
    time_t now = time(NULL);
    strftime(buff, 20, "%Y-%m-%d %H:%M:%S", localtime(&now));
    return strdup(buff);
}

/**
 * Get short time as format, used for trophies.
 *
 * @return the short time format
 */
char *get_short_time_formatted() {
    char buff[20];
    time_t now = time(NULL);
    strftime(buff, 20, "%m-%d-%Y", localtime(&now));
    return strdup(buff);
}

/**
 * Get the time formatted by unix number, must be free'd at the end.
 *
 * @param time_seconds the unix timestamp
 * @return the time formatted.
 */
char *get_time_formatted_custom(unsigned long time_seconds) {
    char buff[20];
    time_t now = (time_t) time_seconds;
    strftime(buff, 20, "%Y-%m-%d %H:%M:%S", localtime(&now));
    return strdup(buff);
}

void filter_vulnerable_characters(char **str, bool remove_newline) {
    char *body = *str;

    if (body == NULL) {
        return;
    }

    for (int i = 0; i < strlen(body); i++) {
        char ch = body[i];

        if (ch == 2 || ch == 9 || ch == 10 || ch == 12 || (remove_newline && ch == 13)) {
            memmove(&body[i], &body[i + 1], strlen(body) - i); //remove char completely
        }
    }
}

char *replace_unreadable_characters(char *body) {
    if (body == NULL) {
        return NULL;
    }

    char *to_return = strdup(body);

    for (int i = 0; i < 14; i++) {
        char char_represent[5];
        sprintf(char_represent, "{%i}", i);

        char *temp = replace_char(to_return, (char) i, char_represent);

        free(to_return);
        to_return = temp;
    }

    return to_return;
}

char *get_argument(char *str, char *delim, int index) {
    if (str == NULL || delim == NULL) {
        return NULL;
    }

    char *copy = strdup(str);
    char *value = NULL;

    int i = 0;

    for (char *token = strtok(copy, delim); token; token = strtok(NULL, delim)) {
        if (i++ == index) {
            value = strdup(token);
            break;
        }
    }

    free(copy);
    return value;
}

/*
 * Return a string that's been converted, does not require to be freed.
 */
char *strlwr(char *str) {
    if (str == NULL) {
        return NULL;
    }

    unsigned char *p = (unsigned char *) str;

    while (*p) {
        *p = (unsigned char) tolower((unsigned char) *p);
        p++;
    }

    return str;
}

/**
  * Replace all occurences of a string with another string, will return a string that requires to be freed.
 * @param str
 * @param sub
 * @param replace
 * @return
 */
char *replace(char *str, char *sub, char *replace) {
    char *pos = str;

    int count = 0;
    const char *tmp = str;
    tmp = strstr(tmp, sub);

    while (tmp) {
        count++;
        tmp++;
        tmp = strstr(tmp, sub);
    }

    if (0 >= count) {
        return strdup(str);
    }

    size_t size = (strlen(str) - (strlen(sub) * count) + strlen(replace) * count) + 1;

    char *result = (char *) malloc(size);

    if (NULL == result) {
        return NULL;
    }

    memset(result, '\0', size);

    char *current;
    while ((current = strstr(pos, sub))) {
        int len = (int) (current - pos);
        strncat(result, pos, (size_t) len);
        strncat(result, replace, strlen(replace));
        pos = current + strlen(sub);
    }

    if (pos != (str + strlen(str))) {
        strncat(result, pos, (str - pos));
    }

    return result;
}

/**
 * Replace a character with a string, will return a string that requires to be freed.
 *
 * @param s
 * @param ch
 * @param repl
 * @return
 */
char *replace_char(const char *s, char ch, char *repl) {
    if (s == NULL || repl == NULL) {
        return false;
    }

    int count = 0;
    const char *t;
    for (t = s; *t; t++)
        count += (*t == ch);

    size_t rlen = strlen(repl);
    char *res = malloc(strlen(s) + (rlen - 1) * count + 1);
    char *ptr = res;
    for (t = s; *t; t++) {
        if (*t == ch) {
            memcpy(ptr, repl, rlen);
            ptr += rlen;
        } else {
            *ptr++ = *t;
        }
    }
    *ptr = 0;
    return res;
}

int valid_password(const char *username, const char *password) {
    if (username == NULL || password == NULL) {
        return 2;
    }

    char *temp_username = strdup(username);
    char *temp_password = strdup(username);

    int error_code = 0;

    if (strcmp(strlwr(temp_username), strlwr(temp_password)) > 0) {
        error_code = 5;
    } else if (strlen(password) < 6) {
        error_code = 1;
    } else if (strlen(password) > 10) {
        error_code = 2;
    } else if (!has_numbers(password)) {
        error_code = 4;
    }

    free(temp_password);
    free(temp_username);

    return error_code;
}

int get_name_check_code(char *username) {
    if (username == NULL) {
        return 2;
    }

    // Some fuckings to that guy that got rich of other people's work
    if (configuration_get_bool("fuck.aaron") && (strcmp("Aaron", username) == 0 || strcmp("Sojobo", username) == 0)) {
        return AARON_IS_A_FAG;
    }

    if (strlen(username) > 15 || !has_allowed_characters(username, "1234567890qwertyuiopasdfghjklzxcvbnm-+=?!@:.,$")) {
        return 2;
    } else {
        if (player_query_exists_username(username)) {
            return 4;
        } else {
            return 0;
        }
    }
}

bool is_numeric(const char *s) {
    if (s == NULL || *s == '\0' || isspace(*s))
        return false;

    char *p;
    strtod(s, &p);
    return *p == '\0';
}

bool has_numbers(const char *str) {
    if (str == NULL) {
        return false;
    }

    for (int i = 0; i < strlen(str); i++) {
        if (isdigit(str[i])) {
            return true;
        }
    }

    return false;
}

bool has_allowed_characters(char *str, char *allowed_chars) {
    if (str == NULL) {
        return false;
    }

    bool valid = false;

    for (int j = 0; j < strlen(str); j++) {
        valid = false;

        for (int i = 0; i < strlen(allowed_chars); i++) {
            if (str[j] == allowed_chars[i]) {
                valid = true;
                continue;
            }
        }
    }

    return valid;
}

bool starts_with(const char *restrict string, const char *restrict prefix) {
    while (*prefix) {
        if (*prefix++ != *string++)
            return 0;
    }

    return 1;
}

/* This code is public domain -- Will Hartung 4/9/09 */
size_t get_file_line(char **lineptr, size_t *n, FILE *stream) {
    char *bufptr = NULL;
    char *p = bufptr;
    size_t size;
    int c;

    if (lineptr == NULL) {
        return (size_t) -1;
    }
    if (stream == NULL) {
        return (size_t) -1;
    }
    if (n == NULL) {
        return (size_t) -1;
    }
    bufptr = *lineptr;
    size = *n;

    c = fgetc(stream);
    if (c == EOF) {
        return (size_t) -1;
    }
    if (bufptr == NULL) {
        bufptr = malloc(128);
        if (bufptr == NULL) {
            return (size_t) -1;
        }
        size = 128;
    }
    p = bufptr;
    while (c != EOF) {
        if ((p - bufptr) > (size - 1)) {
            size = size + 128;
            bufptr = realloc(bufptr, size);
            if (bufptr == NULL) {
                return (size_t) -1;
            }
        }
        *p++ = (char) c;
        if (c == '\n') {
            break;
        }
        c = fgetc(stream);
    }

    *p++ = '\0';
    *lineptr = bufptr;
    *n = size;

    return (p - bufptr - 1);
}
