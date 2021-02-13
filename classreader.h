#pragma once


#include <stdint.h>


#define CONSTANT_Utf8 1
#define CONSTANT_Integer 3
#define CONSTANT_Float 4
#define CONSTANT_Long 5
#define CONSTANT_Double 6
#define CONSTANT_Class 7
#define CONSTANT_String 8
#define CONSTANT_Fieldref 9
#define CONSTANT_Methodref 10
#define CONSTANT_InterfaceMethodref 11
#define CONSTANT_NameAndType 12
#define CONSTANT_MethodHandle 15
#define CONSTANT_MethodType 16
#define CONSTANT_Dynamic 17
#define CONSTANT_InvokeDynamic 	18
#define CONSTANT_Module 19
#define CONSTANT_Package 20

// Change endianness of a number
#define swap2bytes(x) (((x)<<8) | ((x)>>8))
#define swap4bytes(x) ((((x)<<24) & 0xff000000) | (((x)<<8) & 0xff0000) | (((x)>>8) & 0xff00) | (((x)>>24) & 0xff))

// The following structures are taken from the Oracle documentation
// The fields are left (almost) unchanged but not everything is used for now because some are not required for the task
struct cp_info {
    uint8_t tag;
    uint8_t *info;
};

struct field_info {
    uint16_t access_flags;
    uint16_t name_index;
    uint16_t descriptor_index;
    uint16_t attributes_count;
    struct attribute_info *attributes;
};

struct method_info {
    uint16_t access_flags;
    uint16_t name_index;
    uint16_t descriptor_index;
    uint16_t attributes_count;
    struct attribute_info *attributes;
};

struct CONSTANT_Class_info {
    uint16_t name_index;
};

struct CONSTANT_Fieldref_info {
    uint16_t class_index;
    uint16_t name_and_type_index;
};

struct CONSTANT_Methodref_info {
    uint16_t class_index;
    uint16_t name_and_type_index;
};

struct CONSTANT_InterfaceMethodref_info {
    uint16_t class_index;
    uint16_t name_and_type_index;
};

struct CONSTANT_String_info {
    uint16_t string_index;
};

struct attribute_info {
    uint16_t attribute_name_index;
    uint32_t attribute_length;
    uint8_t *info;
};

struct CONSTANT_Integer_info {
    uint32_t bytes;
};

struct CONSTANT_Float_info {
    uint32_t bytes;
};

struct CONSTANT_Long_info {
    uint32_t high_bytes;
    uint32_t low_bytes;
};

struct CONSTANT_Double_info {
    uint32_t high_bytes;
    uint32_t low_bytes;
};


struct CONSTANT_NameAndType_info {
    uint16_t name_index;
    uint16_t descriptor_index;
};

struct CONSTANT_Utf8_info {
    uint16_t length;
    uint8_t *bytes;
};

struct CONSTANT_MethodHandle_info {
    uint8_t reference_kind;
    uint16_t reference_index;
};

struct CONSTANT_MethodType_info {
    uint16_t descriptor_index;
};

struct CONSTANT_Dynamic_info {
    uint16_t bootstrap_method_attr_index;
    uint16_t name_and_type_index;
};

struct CONSTANT_InvokeDynamic_info {
    uint16_t bootstrap_method_attr_index;
    uint16_t name_and_type_index;
};


struct CONSTANT_Module_info {
    uint16_t name_index;
};


struct CONSTANT_Package_info {
    uint16_t name_index;
};

struct classfile {
    uint32_t magic;
    uint16_t minor_version;
    uint16_t major_version;
    uint16_t constant_pool_count;
    struct cp_info *constant_pool;
    uint16_t access_flags;
    uint16_t this_class;
    uint16_t super_class;
    uint16_t interfaces_count;
    uint16_t *interfaces;
    uint16_t fields_count;
    struct field_info *fields;
    uint16_t methods_count;
    struct method_info *methods;
    uint16_t attributes_count;
    struct attribute_info *attributes;
};


char *getConstantName(int index);

void entryToString(struct classfile c, int i, char *buf);

struct classfile readClass(FILE *file);

void dispose(struct classfile c);

