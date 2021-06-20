#!/usr/bin/env bash
echo 'DAGSchedulex Building...'

mvn clean package -DskipTests  -pl \
engine-datadevelop \
-am -amd