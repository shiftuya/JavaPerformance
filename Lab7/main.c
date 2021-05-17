#include "graalvm/llvm/polyglot.h"
#include <stdio.h>
#include <string.h>
#include <ctype.h>

char *trim(char *str) {
  while (isspace(str[0])) {
    str++;
  }
  if (str[0] == '\0') {
    return str;
  }

  char *end = str + strlen(str) - 1;
  while (isspace(end[0])) {
    end--;
  }
  end[1] = '\0';
  return str;
}

void *getCpuInfo() {
  //jclass mapclass = (*env)->FindClass(env, "java/util/HashMap");
  //jmethodID init = (*env)->GetMethodID(env, mapclass, "<init>", "()V");
  //jobject map = (*env)->NewObject(env, mapclass, init);
  void *mapclass = polyglot_java_type("java.util.HashMap");
  void *map = polyglot_new_instance(mapclass);

  char str[1024];

  FILE *file = fopen("/proc/cpuinfo", "r");
  while (1) {
    if (fgets(str, sizeof(str), file) == NULL) {
      break;
    }
    if (str[0] == '\n') {
      break;
    }
    char *key = strtok(str, ":");
    char *value = strtok(NULL, ":");

    key = trim(key);
    value = trim(value);

    void *jkey = polyglot_from_string(key, "US-ASCII");
    void *jvalue = polyglot_from_string(value, "US-ASCII");

    polyglot_invoke(map, "put", jkey, jvalue);

    //jstring jkey = (*env)->NewStringUTF(env, key);
    //jstring jvalue = (*env)->NewStringUTF(env, value);
    //jmethodID put = (*env)->GetMethodID(env, mapclass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");


    //(*env)->CallObjectMethod(env, map, put, jkey, jvalue);
  }

  fclose(file);
  return map;
}
