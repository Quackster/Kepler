#include "shared.h"
#include "log.h"
#include "hashtable.h"
#include "configuration.h"

#define CONFIGURATION_FILE "config.ini"

/**
 * Loads the configuration, will create a fresh file if
 * the config file cannot be found.
 */
void configuration_init() {
    hashtable_new(&global.configuration.entries);

    FILE *file = fopen(CONFIGURATION_FILE, "r");

    if (!file) {
        log_info("Configuration file does not exist, creating...");

        configuration_new();
        file = fopen(CONFIGURATION_FILE, "r");
    }

    configuration_read(file);

    if (file != NULL) {
        fclose(file);
    }
}

/**
 * This command is called automatically to create a new config file
 * with default settings.
 */
void configuration_new() {
    FILE *fp = fopen(CONFIGURATION_FILE, "wb");
    fprintf(fp, "[Database]\n");
    fprintf(fp, "database.filename=%s\n", "Kepler.db");
    fprintf(fp, "\n");
    fprintf(fp, "[Server]\n");
    fprintf(fp, "server.ip.address=%s\n", "127.0.0.1");
    fprintf(fp, "server.port=%i\n", 12321);
    fprintf(fp, "\n");
    fprintf(fp, "[Rcon]\n");
    fprintf(fp, "rcon.ip.address=%s\n", "127.0.0.1");
    fprintf(fp, "rcon.port=%i\n", 12309);
    fprintf(fp, "\n");
    fprintf(fp, "[Game]\n");
    fprintf(fp, "sso.tickets.enabled=%s\n", "true");
    fprintf(fp, "fuck.aaron=%s\n", "true"); // Some fuckings to that guy that got rich of other people's work
    fprintf(fp, "\n");
    fprintf(fp, "welcome.message.enabled=%s\n", "true");
    fprintf(fp, "welcome.message.content=%s\n", "Hello, %username%! And welcome to the Kepler server!");
    fprintf(fp, "\n");
    fprintf(fp, "# 1 tick = 500ms, 6 is 3 seconds\n");
    fprintf(fp, "roller.tick.default=%s\n", "6");
    fprintf(fp, "\n");
    fprintf(fp, "[Console]\n");
    fprintf(fp, "debug=%s\n", "false");
    fclose(fp);
}

/**
 * Read the configuration file with a key/value system seperated by '='
 *
 * @param file the file handle to create for
 */
void configuration_read(FILE *file) {
    char *line = NULL;
    size_t len = 0;
    ssize_t read;

    int id = 0;

    while ((read = get_file_line(&line, &len, file)) != -1) {
        if (line == NULL) {
            continue;
        }

        filter_vulnerable_characters(&line, true);
        char *found = strstr(line, "=" );

        if (found != NULL) {
            int index = (int) (found - line);

            char *key = strdup(line);
            key[index] = '\0';

            char *value = strdup(line + index + 1);
            hashtable_add(global.configuration.entries, key, value);
        }
    }

    free(line);
}

/**
 * Gets a string by its key in the configuration. Will return NULL
 * if the key could not be found.
 *
 * @param key the key to find the value for
 * @return the value, if successful
 */
char *configuration_get_string(char *key) {
    if (hashtable_contains_key(global.configuration.entries, key)) {
        char *value;
        hashtable_get(global.configuration.entries, key, (void*)&value);

        return value;
    }

    return NULL;
}

/**
 * Gets a boolean by its key in the configuration. Will return false
 * if the key could not be found.
 *
 * @param key the key to find the value for
 * @return the value, if successful
 */
bool configuration_get_bool(char *key) {
    if (hashtable_contains_key(global.configuration.entries, key)) {
        char *value;
        hashtable_get(global.configuration.entries, key, (void*)&value);

        if (is_numeric(value)) {
            return strtol(value, NULL, 10) == 1 ? true : false;
        } else {
            return strcmp(value, "true") == 0;
        }
    }

    return false;
}

/**
 * Gets a integer by its key in the configuration. Will return -1
 * if the key could not be found.
 *
 * @param key the key to find the value for
 * @return the value, if successful
 */
int configuration_get_int(char *key) {
    if (hashtable_contains_key(global.configuration.entries, key)) {
        char *value;
        hashtable_get(global.configuration.entries, key, (void *) &value);

        if (is_numeric(value)) {
            return (int) strtol(value, NULL, 10);
        }
    }

    return -1;
}

/**
 * Destory configuration
 */
void configuration_dispose() {
    HashTableIter iter;
    TableEntry *entry;
    hashtable_iter_init(&iter, global.configuration.entries);

    while (hashtable_iter_next(&iter, &entry) != CC_ITER_END) {
        char *key = entry->key;
        char *value = entry->value;

        free(key);
        free(value);
    }

    hashtable_destroy(global.configuration.entries);
}
