#ifndef STRINGBUILDER_H
#define STRINGBUILDER_H

typedef struct stringbuilder_s {
    char *data;
    int index;
    int capacity;
} stringbuilder;

stringbuilder *sb_create();
void sb_ensure_capacity(stringbuilder*, int);
void sb_add_string(stringbuilder*, const char*);
void sb_add_int(stringbuilder*, int);
void sb_add_wired(stringbuilder*, int);
void sb_add_float(stringbuilder*, double);
void sb_add_char(stringbuilder*, int);
void sb_add_string_delimeter(stringbuilder *sb, const char *data, char delim);
void sb_add_int_delimeter(stringbuilder *sb, int integer, char delim);
void sb_add_wired_delimeter(stringbuilder *sb, int integer, char delim);
void sb_add_float_delimeter(stringbuilder *sb, double d, char delim);
void sb_cleanup(stringbuilder*);

#endif