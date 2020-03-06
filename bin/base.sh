#!/bin/bash


ulimit -c unlimited

HO_HEAP_SIZE="${HO_HEAP_SIZE:=512m}"
JAVA_HOME=/opt/dtstack/java
PATH=$JAVA_HOME/bin:$PATH
CMD_PATH=`dirname $0`

function print_usage(){
  echo "Usage: engine [COMMAND]"
  echo "  where COMMAND is one of:"
  echo "  master                                run the MasterMain"
  echo "  worker                                run the WorkerMain"
}

COMMAND=$1
case $COMMAND in
  # usage flags
  --help|-help|-h)
    print_usage
    exit
    ;;
esac

if [ "$COMMAND" = "master" ] ; then
  CLASS='com.dtstack.engine.master.MasterMain'
  FILE=$CMD_PATH'/../conf/common.conf'
  LIB='engine-master-feat_scheduleMasterWorker-with-dependencies.jar'
elif [ "$COMMAND" = "worker" ] ; then
  CLASS='com.dtstack.engine.worker.WorkerMain'
  FILE=$CMD_PATH'/../conf/worker.conf'
  LIB='engine-worker-feat_scheduleMasterWorker-with-dependencies.jar'
else
  CLASS='com.dtstack.engine.entrance.EngineMain'
  FILE=$CMD_PATH'/../conf/engine.conf'
  LIB='engine-entrance-feat_scheduleMasterWorker-with-dependencies.jar'
fi
echo 'exec java MainClass is' $CLASS.


unset CDPATH
export basedir=$(cd `dirname $0`/..; pwd)

JAVA_OPTS="$JAVA_OPTS -Xmx${HO_HEAP_SIZE}"

JAVA_OPTS="$JAVA_OPTS -Xms${HO_HEAP_SIZE}"

JAVA_OPTS="$JAVA_OPTS -server"

JAVA_OPTS="$JAVA_OPTS -Xloggc:../logs/node.gc"

JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=../logs/heapdump.hprof"

JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=4547 -XX:-OmitStackTraceInFastThrow"

#-XX:MaxDirectMemorySize=16M According to owner memory
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -Dfile.encoding=UTF-8 -Djna.nosys=true -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps"

JAVA_OPTS="$JAVA_OPTS -Dconfig.file=${FILE}"

#Comment to speed up starting time
#JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

exec java $JAVA_OPTS -cp $basedir/lib/$LIB $CLASS "$@"