#!/usr/bin/env bash
echo 'taier Building...'

mvn clean package -U -DskipTests -T 1C -pl \
taier-data-develop,\
taier-common,\
taier-worker/taier-plugins,\
taier-datasource/taier-datasource-plugin \
-am -amd