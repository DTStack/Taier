#!/usr/bin/env bash
echo 'taier Building...'

mvn clean package -DskipTests  -pl \
taier-data-develop \
-am