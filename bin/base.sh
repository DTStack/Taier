#!/bin/bash

ulimit -c unlimited

HO_HEAP_SIZE="${HO_HEAP_SIZE:=2048m}"

CMD_PATH=`dirname $0`
CMD_HOME=`cd "$CMD_PATH"/../; pwd`
LS_CONF_DIR=$CMD_HOME/conf
LS_LIB_DIR=$CMD_HOME/lib

ENTRY_POINT_CLASS='com.dtstack.taier.develop.TaierApplication'

unset CDPATH
export basedir=$(cd `dirname $0`/..; pwd)

JAVA_OPTS="$JAVA_OPTS -Xmx${HO_HEAP_SIZE}"

JAVA_OPTS="$JAVA_OPTS -Xms${HO_HEAP_SIZE}"

JAVA_OPTS="$JAVA_OPTS -server"

JAVA_OPTS="$JAVA_OPTS -Xloggc:../logs/node.gc"

JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=../logs/heapdump.hprof"

JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=4547 -XX:-OmitStackTraceInFastThrow"

JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -Dfile.encoding=UTF-8 -Djna.nosys=true -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps"

JAVA_OPTS="$JAVA_OPTS -Djava.security.policy=$LS_CONF_DIR/java.policy"

JAVA_OPTS="$JAVA_OPTS -Djava.io.tmpdir=./tmpSave"

exec java $JAVA_OPTS -cp $basedir/lib/* $ENTRY_POINT_CLASS "$@"