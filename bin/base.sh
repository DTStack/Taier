#!/bin/bash


ulimit -c unlimited

HO_HEAP_SIZE="${HO_HEAP_SIZE:=512m}"
JAVA_HOME=/opt/dtstack/java
PATH=$JAVA_HOME/bin:$PATH

CMD_PATH=`dirname $0`
CMD_HOME=`cd "$CMD_PATH"/../; pwd`
LS_CONF_DIR=$CMD_HOME/conf
LS_LIB_DIR=$CMD_HOME/lib

COMPONENT=$1
echo 'start component is' $COMPONENT

if [ "$COMPONENT" = "master" ] ; then
  ENTRY_POINT_CLASS='com.dtstack.engine.master.MasterMain'
  FILE=$LS_CONF_DIR'/master.conf'
  LIB=`ls $LS_LIB_DIR/ |grep engine-master`
elif [ "$COMPONENT" = "worker" ] ; then
  ENTRY_POINT_CLASS='com.dtstack.engine.worker.WorkerMain'
  FILE=$LS_CONF_DIR'/worker.conf'
  LIB=`ls $LS_LIB_DIR/ |grep engine-worker`
else
  ENTRY_POINT_CLASS='com.dtstack.engine.entrance.EngineMain'
  FILE=$LS_CONF_DIR'/engine.conf'
  LIB=`ls $LS_LIB_DIR/ |grep engine-entrance`
fi
echo 'exec java MainClass is' $ENTRY_POINT_CLASS  ' FILE is' $FILE ' LIB is' $LIB.


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

exec java $JAVA_OPTS -cp $basedir/lib/$LIB $ENTRY_POINT_CLASS "$@"