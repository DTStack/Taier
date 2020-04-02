#!/usr/bin/env bash
echo 'Dependency Hadoop-2.7.3 Building...'

mvn clean package -DskipTests -Dhadoop.version=2.7.3 -pl \
engine-entrance \
-am -amd