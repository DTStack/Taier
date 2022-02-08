#!/usr/bin/env bash
echo 'Taiga Building...'

mvn clean package -DskipTests  -pl \
taiga-data-develop \
-am