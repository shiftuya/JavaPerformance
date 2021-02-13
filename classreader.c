#include <malloc.h>
#include <stdio.h>
#include <mem.h>

#include "classreader.h"


char *getConstantName(int index) {
    switch (index) {
        case CONSTANT_Utf8:
            return "Utf8";
        case CONSTANT_Integer:
            return "Integer";
        case CONSTANT_Float:
            return "Float";
        case CONSTANT_Long:
            return "Long";
        case CONSTANT_Double:
            return "Double";
        case CONSTANT_Class:
            return "Class";
        case CONSTANT_String:
            return "String";
        case CONSTANT_Fieldref:
            return "Fieldref";
        case CONSTANT_Methodref:
            return "Methodref";
        case CONSTANT_InterfaceMethodref:
            return "InterfaceMethodref";
        case CONSTANT_NameAndType:
            return "NameAndType";
        case CONSTANT_MethodHandle:
            return "MethodHandle";
        case CONSTANT_MethodType:
            return "MethodType";
        case CONSTANT_Dynamic:
            return "Dynamic";
        case CONSTANT_InvokeDynamic:
            return "InvokeDynamic";
        case CONSTANT_Module:
            return "Module";
        case CONSTANT_Package:
            return "Package";
        default:
            return "";
    }
}



void entryToString(struct classfile c, int i, char *buf) {
    char newbuf[1000];
    float floatbuf;
    double doublebuf;
    int k = 0;
    switch(c.constant_pool[i].tag) {
        case CONSTANT_Utf8:
            for (int j = 0; j < ((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->length; ++j) {
                if (((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes[j] >> 5 == 0b110) {
                    buf[k] = ((((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes[j] & 0x1f) << 6) + (((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes[j+1] & 0x3f);
                    ++j;
                } else if (((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes[j] >> 4 == 0b1110) {
                    buf[k] = ((((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes[j] & 0xf) << 12) + ((((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes[j+1] & 0x3f) << 6) + (((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes[j+2] & 0x3f);
                    j += 2;
                } else {
                    buf[k] = ((struct CONSTANT_Utf8_info *) c.constant_pool[i].info)->bytes[j];
                }
                k++;
               // sprintf(buf++, "%c", ((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes[j]);
            }
            buf[k] = 0;
            break;
        case CONSTANT_Integer:
            sprintf(buf, "%d", ((struct CONSTANT_Integer_info*)c.constant_pool[i].info)->bytes);
            break;
        case CONSTANT_Float:
            *(uint32_t*)(&floatbuf) = ((uint32_t)((struct CONSTANT_Float_info*)c.constant_pool[i].info)->bytes);
            if (*(uint32_t*)(&floatbuf) == 0x7f800000) {
                sprintf(buf, "Infinity");
            } else if (*(uint32_t*)(&floatbuf) == 0xff800000) {
                sprintf(buf, "-Infinity");
            } else if ((*(uint32_t*)(&floatbuf) >= 0x7f800001 && *(uint32_t*)(&floatbuf) <=  0x7fffffff) || (*(uint32_t*)(&floatbuf) >= 0xff800001 && *(uint32_t*)(&floatbuf) <= 0xffffffff)) {
                sprintf(buf, "NaN");
            } else {
                sprintf(buf, "%e", floatbuf);
            }
            break;
        case CONSTANT_Long:
            sprintf(buf, "%lld", ((uint64_t)((struct CONSTANT_Long_info*)c.constant_pool[i].info)->high_bytes << 32) + ((struct CONSTANT_Long_info*)c.constant_pool[i].info)->low_bytes);
            break;
        case CONSTANT_Double:
            *(uint64_t*)(&doublebuf) = ((uint64_t)((struct CONSTANT_Double_info*)c.constant_pool[i].info)->high_bytes << 32) + ((struct CONSTANT_Double_info*)c.constant_pool[i].info)->low_bytes;
            if (*(uint64_t*)(&doublebuf) == 0x7ff0000000000000L) {
                sprintf(buf, "Infinity");
            } else if (*(uint64_t*)(&doublebuf) == 0xfff0000000000000L) {
                sprintf(buf, "-Infinity");
            } else if ((*(uint64_t*)(&doublebuf) >= 0x7ff0000000000001L && *(uint64_t*)(&doublebuf) <= 0x7fffffffffffffffL) || (*(uint64_t*)(&doublebuf) >= 0xfff0000000000001L && *(uint64_t*)(&doublebuf) <= 0xffffffffffffffffL)) {
                sprintf(buf, "NaN");
            } else {
                sprintf(buf, "%e", doublebuf);
            }
            break;
        case CONSTANT_Class:
            sprintf(newbuf, "#%d", ((struct CONSTANT_Class_info*)c.constant_pool[i].info)->name_index);
            sprintf(buf, "%-20s", newbuf);
            entryToString(c, ((struct CONSTANT_Class_info *) c.constant_pool[i].info)->name_index - 1, newbuf);
            strcat(buf, "// ");
            strcat(buf, newbuf);
            break;
        case CONSTANT_String:
            sprintf(buf, "#%d", ((struct CONSTANT_String_info*)c.constant_pool[i].info)->string_index);
            break;
        case CONSTANT_Fieldref:
            sprintf(newbuf, "#%d.#%d", ((struct CONSTANT_Fieldref_info*)c.constant_pool[i].info)->class_index, ((struct CONSTANT_Fieldref_info*)c.constant_pool[i].info)->name_and_type_index);
            sprintf(buf, "%-20s", newbuf);
            entryToString(c, ((struct CONSTANT_Class_info *) c.constant_pool[
                                  ((struct CONSTANT_Fieldref_info *) c.constant_pool[i].info)->class_index - 1].info)->name_index - 1,
                          newbuf);
            strcat(buf, "// ");
            strcat(buf, newbuf);
            strcat(buf, ".");
            entryToString(c, ((struct CONSTANT_NameAndType_info *) c.constant_pool[
                    ((struct CONSTANT_Fieldref_info *) c.constant_pool[i].info)->name_and_type_index -
                    1].info)->name_index - 1, newbuf);
            strcat(buf, newbuf);
            strcat(buf, ":");
            entryToString(c, ((struct CONSTANT_NameAndType_info *) c.constant_pool[
                    ((struct CONSTANT_Fieldref_info *) c.constant_pool[i].info)->name_and_type_index -
                    1].info)->descriptor_index - 1, newbuf);
            strcat(buf, newbuf);
            break;
        case CONSTANT_Methodref:
            sprintf(newbuf, "#%d.#%d", ((struct CONSTANT_Methodref_info*)c.constant_pool[i].info)->class_index, ((struct CONSTANT_Methodref_info*)c.constant_pool[i].info)->name_and_type_index);
            sprintf(buf, "%-20s", newbuf);
            entryToString(c, ((struct CONSTANT_Class_info *) c.constant_pool[
                    ((struct CONSTANT_Methodref_info *) c.constant_pool[i].info)->class_index - 1].info)->name_index -
                             1, newbuf);
            strcat(buf, "// ");
            strcat(buf, newbuf);
            strcat(buf, ".");
            entryToString(c, ((struct CONSTANT_NameAndType_info *) c.constant_pool[
                    ((struct CONSTANT_Methodref_info *) c.constant_pool[i].info)->name_and_type_index -
                    1].info)->name_index - 1, newbuf);
            strcat(buf, newbuf);
            strcat(buf, ":");
            entryToString(c, ((struct CONSTANT_NameAndType_info *) c.constant_pool[
                    ((struct CONSTANT_Methodref_info *) c.constant_pool[i].info)->name_and_type_index -
                    1].info)->descriptor_index - 1, newbuf);
            strcat(buf, newbuf);
            break;
        case CONSTANT_InterfaceMethodref:
            sprintf(newbuf, "#%d.#%d", ((struct CONSTANT_InterfaceMethodref_info*)c.constant_pool[i].info)->class_index, ((struct CONSTANT_InterfaceMethodref_info*)c.constant_pool[i].info)->name_and_type_index);
            sprintf(buf, "%-20s", newbuf);
            entryToString(c, ((struct CONSTANT_Class_info *) c.constant_pool[
                    ((struct CONSTANT_InterfaceMethodref_info *) c.constant_pool[i].info)->class_index -
                    1].info)->name_index - 1, newbuf);
            strcat(buf, "// ");
            strcat(buf, newbuf);
            strcat(buf, ".");
            entryToString(c, ((struct CONSTANT_NameAndType_info *) c.constant_pool[
                    ((struct CONSTANT_InterfaceMethodref_info *) c.constant_pool[i].info)->name_and_type_index -
                    1].info)->name_index - 1, newbuf);
            strcat(buf, newbuf);
            strcat(buf, ":");
            entryToString(c, ((struct CONSTANT_NameAndType_info *) c.constant_pool[
                    ((struct CONSTANT_InterfaceMethodref_info *) c.constant_pool[i].info)->name_and_type_index -
                    1].info)->descriptor_index - 1, newbuf);
            strcat(buf, newbuf);
            break;
        case CONSTANT_NameAndType:
            sprintf(newbuf, "#%d:#%d", ((struct CONSTANT_NameAndType_info*)c.constant_pool[i].info)->name_index, ((struct CONSTANT_NameAndType_info*)c.constant_pool[i].info)->descriptor_index);
            sprintf(buf, "%-20s// ", newbuf);
            entryToString(c, ((struct CONSTANT_NameAndType_info *) c.constant_pool[i].info)->name_index - 1, newbuf);
            strcat(buf, newbuf);
            strcat(buf, ":");
            entryToString(c, ((struct CONSTANT_NameAndType_info *) c.constant_pool[i].info)->descriptor_index - 1,
                          newbuf);
            strcat(buf, newbuf);
            break;
        case CONSTANT_MethodHandle:
            break;
        case CONSTANT_MethodType:
            sprintf(newbuf, "#%d", ((struct CONSTANT_MethodType_info*)c.constant_pool[i].info)->descriptor_index);
            sprintf(buf, "%-20s// ", newbuf);
            entryToString(c, ((struct CONSTANT_MethodType_info *) c.constant_pool[i].info)->descriptor_index - 1, newbuf);
            strcat(buf, newbuf);
            break;
        case CONSTANT_Dynamic: //???
            break;
        case CONSTANT_InvokeDynamic: //???
            break;
        case CONSTANT_Module:
            sprintf(newbuf, "#%d", ((struct CONSTANT_Module_info*)c.constant_pool[i].info)->name_index);
            sprintf(buf, "%-20s// ", newbuf);
            entryToString(c, ((struct CONSTANT_Module_info *) c.constant_pool[i].info)->name_index - 1, newbuf);
            strcat(buf, newbuf);
            break;
        case CONSTANT_Package:
            sprintf(newbuf, "#%d", ((struct CONSTANT_Package_info*)c.constant_pool[i].info)->name_index);
            sprintf(buf, "%-20s// ", newbuf);
            entryToString(c, ((struct CONSTANT_Package_info *) c.constant_pool[i].info)->name_index - 1, newbuf);
            strcat(buf, newbuf);
            break;
        default:
            break;
    }
}


struct classfile readClass(FILE *file) {
    struct classfile c;

    fread(&c.magic, sizeof(c.magic), 1, file);
    fread(&c.minor_version, sizeof(c.minor_version), 1, file);
    fread(&c.major_version, sizeof(c.major_version), 1, file);
    fread(&c.constant_pool_count, sizeof(c.constant_pool_count), 1, file);

    c.minor_version = swap2bytes(c.minor_version);
    c.major_version = swap2bytes(c.major_version);
    c.constant_pool_count = swap2bytes(c.constant_pool_count);

    c.constant_pool = calloc((size_t)c.constant_pool_count - 1, sizeof(struct cp_info));

    for (int i = 0; i < c.constant_pool_count - 1; ++i) {
        fread(&c.constant_pool[i].tag, sizeof(uint8_t), 1, file);

        switch(c.constant_pool[i].tag) {
            case CONSTANT_Utf8:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Utf8_info));
                fread(&((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->length, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->length = swap2bytes(((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->length);
                ((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes = malloc(((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->length);
                fread(((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes, 1, ((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->length, file);
                break;
            case CONSTANT_Integer:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Integer_info));
                fread(&((struct CONSTANT_Integer_info*)c.constant_pool[i].info)->bytes, sizeof(uint32_t), 1, file);
                ((struct CONSTANT_Integer_info*)c.constant_pool[i].info)->bytes = swap4bytes(((struct CONSTANT_Integer_info*)c.constant_pool[i].info)->bytes);
                break;
            case CONSTANT_Float:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Float_info));
                fread(&((struct CONSTANT_Float_info*)c.constant_pool[i].info)->bytes, sizeof(uint32_t), 1, file);
                ((struct CONSTANT_Float_info*)c.constant_pool[i].info)->bytes = swap4bytes(((struct CONSTANT_Float_info*)c.constant_pool[i].info)->bytes);
                break;
            case CONSTANT_Long:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Long_info));
                fread(&((struct CONSTANT_Long_info*)c.constant_pool[i].info)->high_bytes, sizeof(uint32_t), 1, file);
                fread(&((struct CONSTANT_Long_info*)c.constant_pool[i].info)->low_bytes, sizeof(uint32_t), 1, file);
                ((struct CONSTANT_Long_info*)c.constant_pool[i].info)->low_bytes = swap4bytes(((struct CONSTANT_Long_info*)c.constant_pool[i].info)->low_bytes);
                ((struct CONSTANT_Long_info*)c.constant_pool[i].info)->high_bytes = swap4bytes(((struct CONSTANT_Long_info*)c.constant_pool[i].info)->high_bytes);
                i++; // 8-byte constants take two constant pool entries
                break;
            case CONSTANT_Double:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Double_info));
                fread(&((struct CONSTANT_Double_info*)c.constant_pool[i].info)->high_bytes, sizeof(uint32_t), 1, file);
                fread(&((struct CONSTANT_Double_info*)c.constant_pool[i].info)->low_bytes, sizeof(uint32_t), 1, file);
                ((struct CONSTANT_Double_info*)c.constant_pool[i].info)->low_bytes = swap4bytes(((struct CONSTANT_Double_info*)c.constant_pool[i].info)->low_bytes);
                ((struct CONSTANT_Double_info*)c.constant_pool[i].info)->high_bytes = swap4bytes(((struct CONSTANT_Double_info*)c.constant_pool[i].info)->high_bytes);
                i++; // 8-byte constants take two constant pool entries
                break;
            case CONSTANT_Class:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Class_info));
                fread(&((struct CONSTANT_Class_info*)c.constant_pool[i].info)->name_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_Class_info*)c.constant_pool[i].info)->name_index = swap2bytes(((struct CONSTANT_Class_info*)c.constant_pool[i].info)->name_index);
                break;
            case CONSTANT_String:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_String_info));
                fread(&((struct CONSTANT_String_info*)c.constant_pool[i].info)->string_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_String_info*)c.constant_pool[i].info)->string_index = swap2bytes(((struct CONSTANT_String_info*)c.constant_pool[i].info)->string_index);
                break;
            case CONSTANT_Fieldref:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Fieldref_info));
                fread(&((struct CONSTANT_Fieldref_info*)c.constant_pool[i].info)->class_index, sizeof(uint16_t), 1, file);
                fread(&((struct CONSTANT_Fieldref_info*)c.constant_pool[i].info)->name_and_type_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_Fieldref_info*)c.constant_pool[i].info)->class_index = swap2bytes(((struct CONSTANT_Fieldref_info*)c.constant_pool[i].info)->class_index);
                ((struct CONSTANT_Fieldref_info*)c.constant_pool[i].info)->name_and_type_index = swap2bytes(((struct CONSTANT_Fieldref_info*)c.constant_pool[i].info)->name_and_type_index);
                break;
            case CONSTANT_Methodref:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Methodref_info));
                fread(&((struct CONSTANT_Methodref_info*)c.constant_pool[i].info)->class_index, sizeof(uint16_t), 1, file);
                fread(&((struct CONSTANT_Methodref_info*)c.constant_pool[i].info)->name_and_type_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_Methodref_info*)c.constant_pool[i].info)->class_index = swap2bytes(((struct CONSTANT_Methodref_info*)c.constant_pool[i].info)->class_index);
                ((struct CONSTANT_Methodref_info*)c.constant_pool[i].info)->name_and_type_index = swap2bytes(((struct CONSTANT_Methodref_info*)c.constant_pool[i].info)->name_and_type_index);
                break;
            case CONSTANT_InterfaceMethodref:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_InterfaceMethodref_info));
                fread(&((struct CONSTANT_InterfaceMethodref_info*)c.constant_pool[i].info)->class_index, sizeof(uint16_t), 1, file);
                fread(&((struct CONSTANT_InterfaceMethodref_info*)c.constant_pool[i].info)->name_and_type_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_InterfaceMethodref_info*)c.constant_pool[i].info)->class_index = swap2bytes(((struct CONSTANT_InterfaceMethodref_info*)c.constant_pool[i].info)->class_index);
                ((struct CONSTANT_InterfaceMethodref_info*)c.constant_pool[i].info)->name_and_type_index = swap2bytes(((struct CONSTANT_InterfaceMethodref_info*)c.constant_pool[i].info)->name_and_type_index);
                break;
            case CONSTANT_NameAndType:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_NameAndType_info));
                fread(&((struct CONSTANT_NameAndType_info*)c.constant_pool[i].info)->name_index, sizeof(uint16_t), 1, file);
                fread(&((struct CONSTANT_NameAndType_info*)c.constant_pool[i].info)->descriptor_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_NameAndType_info*)c.constant_pool[i].info)->name_index = swap2bytes(((struct CONSTANT_NameAndType_info*)c.constant_pool[i].info)->name_index);
                ((struct CONSTANT_NameAndType_info*)c.constant_pool[i].info)->descriptor_index = swap2bytes(((struct CONSTANT_NameAndType_info*)c.constant_pool[i].info)->descriptor_index);
                break;
            case CONSTANT_MethodHandle:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_MethodHandle_info));
                fread(&((struct CONSTANT_MethodHandle_info*)c.constant_pool[i].info)->reference_kind, sizeof(uint8_t), 1, file);
                fread(&((struct CONSTANT_MethodHandle_info*)c.constant_pool[i].info)->reference_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_MethodHandle_info*)c.constant_pool[i].info)->reference_index = swap2bytes(((struct CONSTANT_MethodHandle_info*)c.constant_pool[i].info)->reference_index);
                break;
            case CONSTANT_MethodType:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_MethodType_info));
                fread(&((struct CONSTANT_MethodType_info*)c.constant_pool[i].info)->descriptor_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_MethodType_info*)c.constant_pool[i].info)->descriptor_index = swap2bytes(((struct CONSTANT_MethodType_info*)c.constant_pool[i].info)->descriptor_index);
                break;
            case CONSTANT_Dynamic:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Dynamic_info));
                fread(&((struct CONSTANT_Dynamic_info*)c.constant_pool[i].info)->bootstrap_method_attr_index, sizeof(uint16_t), 1, file);
                fread(&((struct CONSTANT_Dynamic_info*)c.constant_pool[i].info)->name_and_type_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_Dynamic_info*)c.constant_pool[i].info)->bootstrap_method_attr_index = swap2bytes(((struct CONSTANT_Dynamic_info*)c.constant_pool[i].info)->bootstrap_method_attr_index);
                ((struct CONSTANT_Dynamic_info*)c.constant_pool[i].info)->name_and_type_index = swap2bytes(((struct CONSTANT_Dynamic_info*)c.constant_pool[i].info)->name_and_type_index);
                break;
            case CONSTANT_InvokeDynamic:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_InvokeDynamic_info));
                fread(&((struct CONSTANT_InvokeDynamic_info*)c.constant_pool[i].info)->bootstrap_method_attr_index, sizeof(uint16_t), 1, file);
                fread(&((struct CONSTANT_InvokeDynamic_info*)c.constant_pool[i].info)->name_and_type_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_InvokeDynamic_info*)c.constant_pool[i].info)->bootstrap_method_attr_index = swap2bytes(((struct CONSTANT_InvokeDynamic_info*)c.constant_pool[i].info)->bootstrap_method_attr_index);
                ((struct CONSTANT_InvokeDynamic_info*)c.constant_pool[i].info)->name_and_type_index = swap2bytes(((struct CONSTANT_InvokeDynamic_info*)c.constant_pool[i].info)->name_and_type_index);
                break;
            case CONSTANT_Module:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Module_info));
                fread(&((struct CONSTANT_Module_info*)c.constant_pool[i].info)->name_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_Module_info*)c.constant_pool[i].info)->name_index = swap2bytes(((struct CONSTANT_Module_info*)c.constant_pool[i].info)->name_index);
                break;
            case CONSTANT_Package:
                c.constant_pool[i].info = malloc(sizeof(struct CONSTANT_Package_info));
                fread(&((struct CONSTANT_Package_info*)c.constant_pool[i].info)->name_index, sizeof(uint16_t), 1, file);
                ((struct CONSTANT_Package_info*)c.constant_pool[i].info)->name_index = swap2bytes(((struct CONSTANT_Package_info*)c.constant_pool[i].info)->name_index);
                break;
            default:
                break;
        }
    }

    return c;
}

void dispose(struct classfile c) {
    for (int i = 0; i < c.constant_pool_count - 1; ++i) {
        if (c.constant_pool[i].tag == CONSTANT_Utf8) {
            free(((struct CONSTANT_Utf8_info*)c.constant_pool[i].info)->bytes);
        }
        free(c.constant_pool[i].info);
    }
}

