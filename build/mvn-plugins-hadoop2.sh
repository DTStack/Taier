#!/usr/bin/env bash
echo 'DAGSchedulex Building...'

hadoop2Version=$1
if [ -z $hadoop2Version ] ; then
    hadoop2Version=2.7.3
fi
echo "Hadoop2Version: ${hadoop2Version} Building..."


hadoop3Version=$2
if [ -z $hadoop3Version ] ; then
    hadoop3Version=3.0.0
fi
echo "Hadoop3Version: ${hadoop3Version} Building..."

mvn -T 1C clean package -DskipTests -Dhadoop2.version=${hadoop2Version} -Dhadoop3.version=${hadoop3Version} -pl \
engine-worker/engine-plugins/dummy,\
engine-worker/engine-plugins/flink/common,\
engine-entrance \
-am