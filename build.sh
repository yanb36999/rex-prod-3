#!/usr/bin/env bash

jpath=$(which javac | grep /javac)

echo java path : ${jpath}

if [ "$jpath" = "" ]; then
    echo build on docker
    docker run -i --rm -w /job -v /home/maven/:/root/.m2/ -v ${PWD}:/job java:8 ./mvnw clean package -DskipTests
 else
    echo build on local
    ./mvnw clean clean package -DskipTests
fi
mkdir target
cp rex-starter-all/target/rex-starter-all.jar target/rex-starter-all.jar
cp -R rex-ui target/rex-ui