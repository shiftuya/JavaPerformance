#!/bin/sh
cd agent
gradle jar
cd ../app
gradle jar
cd ..

JARPATH=app/build/libs
java -javaagent:agent/$JARPATH/agent.jar -jar app/$JARPATH/app.jar

