#!/usr/bin/env bash
echo 'taier Building...'

mvn clean package -DskipTests -T 1C  -pl \
taier-data-develop \
-am