#!/usr/bin/env bash

JAR_NAME=`ls -r build/libs/jstributed-*.jar | head -1`

if [[ -z ${JAR_NAME} ]]; then
    echo 'Jar file not found. Run `./build.sh` first.'
    exit 1
fi

echo ${JAR_NAME}
java -Djava.net.preferIPv4Stack=true -cp ${JAR_NAME} com.luciopaiva.jstributed.Client $1
