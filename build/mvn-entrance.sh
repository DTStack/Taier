#!/usr/bin/env bash
echo 'DAGSchedulex Building...'

mvn -s /Users/shengpei/.m2/settings2.xml clean package -DskipTests  -pl \
engine-entrance \
-am