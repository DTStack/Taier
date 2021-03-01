#!/usr/bin/env bash
echo 'DAGSchedulex Building...'

mvn -T 1C clean package -DskipTests  -pl \
engine-entrance \
-am