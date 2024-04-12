#Author: Koumis

#Compiler flags for cpp
CC = g++
CFLAGS = -Wall -ltbb -o

#binary file names
CPP_NAME = BarnesHut

CPP_SRC = Cpp/src
JAVA_SRC = Java/src

all: cpp java

cpp: 
	$(CC) $(CPP_SRC)/*.cpp $(CFLAGS) $(CPP_NAME)

java:
	javac -d . $(JAVA_SRC)/*.java

clean: cleanCPP cleanJava
	
cleanCPP:
	rm -f $(CPP_NAME)

cleanJava:
	rm -rf $(JAVA_SRC)/*.class  *.class

cleanOutput:
	rm -f *.txt