#!/usr/bin/env bash
echo 'taier Building...'

mvn clean package -DskipTests -T 1C -pl \
taier-common,\
taier-worker/taier-plugins,\
taier-datasource/taier-datasource-plugin \
-am -amd