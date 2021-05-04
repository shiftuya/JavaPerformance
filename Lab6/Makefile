all: libcpuinfo.so Main.class

libcpuinfo.so: Main.c Main.h
	gcc -fPIC -I"${JAVA_HOME}/include" -I"${JAVA_HOME}/include/linux" -shared -o libcpuinfo.so Main.c

Main.class: Main.java
	javac Main.java
