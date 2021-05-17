all: main.bc Main.class

main.bc: main.c
	clang -g -O1 -c -emit-llvm -I$$GRAALVM_HOME/languages/llvm/include -I$$GRAALVM_HOME/include/linux -target x86_64-unknown-linux-gnu main.c -o main.bc

Main.class: Main.java
	$$GRAALVM_HOME/bin/javac Main.java
