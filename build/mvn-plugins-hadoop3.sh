#!/usr/bin/env bash
echo 'Dependency Hadoop-3.0.0 Building...'

mvn clean package -DskipTests -Dhadoop.version=3.0.0 -pl \
plugins/kylin,\
plugins/rdbs,\
plugins/odps,\
entrance \
-am -amd