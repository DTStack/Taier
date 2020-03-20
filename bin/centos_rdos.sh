#!/bin/bash
PATH=/sbin:/usr/sbin:/bin:/usr/bin
JAVA_HOME=/opt/dtstack/java
PATH=$JAVA_HOME/bin:$PATH
export JAVA_HOME
export PATH

CMD_PATH=`dirname $0`
CMD_HOME=`cd "$CMD_PATH"/../; pwd`

checkuser() {
  if [ "`whoami`" != "admin" ]; then
   echo "You need admin to run this script"
   exit 1
  fi
}
checkuser

LS_HOME=$CMD_HOME
LS_LOG_DIR=$CMD_HOME/logs
LS_CONF_DIR=$CMD_HOME/conf
LS_OPEN_FILES=16384
LS_NICE=19
KILL_ON_STOP_TIMEOUT=1
LS_OPTS=""
REMOTE_PORT="9996"
name=rdos
LS_HEAP_SIZE="128m"
ls_conf=${LS_CONF_DIR}/node.yml
ls_log="${LS_LOG_DIR}/$name.log"
pidfile="${CMD_HOME}/run/$name.pid"
gc_log=${CMD_HOME}/logs/rdos.gc
heapdump=${CMD_HOME}/rdos.hprof

program=${LS_HOME}/bin/base.sh
args="agent -f ${ls_conf} -l ${ls_log} ${LS_OPTS}"
#args="agent -f ${ls_conf} ${LS_OPTS}"

. /etc/init.d/functions

quiet() {
  "$@" > /dev/null 2>&1
  return $?
}

start() {
  COMPONENT=$1
  if [ -z $COMPONENT ] ; then
    COMPONENT="entrance"
  fi  

  echo -n "Starting $name component is $COMPONENT, "

  JAVA_OPTS="${JAVA_OPTS} -Djava.io.tmpdir=${LS_HOME} -Xloggc:${gc_log} -XX:HeapDumpPath=${heapdump}"
  HOME=${LS_HOME}
  export PATH HOME LS_HEAP_SIZE JAVA_OPTS LS_USE_GC_LOGGING LS_GC_LOG_FILE
  #ulimit -n ${LS_OPEN_FILES}

  nice -n ${LS_NICE} sh -c "
    cd $LS_HOME
    exec \"$program\" $COMPONENT $args
   " 1> "${LS_LOG_DIR}/$name.stdout" 2> "${LS_LOG_DIR}/$name.err" &

  echo $! > $pidfile
  ret=$?
  [ $ret -eq 0 ] && success || failure; echo
  return 0
}

stop() {
  echo -n "Stoping $name "
  # Try a few times to kill TERM the program
  if status ; then
    pid=`cat "$pidfile"`
    echo "Killing $name (pid $pid) with SIGTERM"
    kill -TERM $pid
    # Wait for it to exit.
    for i in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30; do
      echo "Waiting $name (pid $pid) to die..."
      status || break
      sleep 1
    done
    if status ; then
      if [ "$KILL_ON_STOP_TIMEOUT" == "1" ] ; then
        echo "Timeout reached. Killing $name (pid $pid) with SIGKILL. This may result in data loss."
        kill -KILL $pid
        echo "$name killed with SIGKILL."
      else
        echo "$name stop failed; still running."
        return 1 # stop timed out and not forced
      fi
    else
      echo -n "$name stopped "
    fi
  fi
  ret=$?
  [ $ret -eq 0 ] && success || failure; echo
}

status() {
  if [ -f "$pidfile" ] ; then
    pid=`cat "$pidfile"`
    if kill -0 $pid > /dev/null 2> /dev/null ; then
      # process by this pid is running.
      # It may not be our pid, but that's what you get with just pidfiles.
      # TODO(sissel): Check if this process seems to be the same as the one we
      # expect. It'd be nice to use flock here, but flock uses fork, not exec,
      # so it makes it quite awkward to use in this case.
      return 0
    else
      return 2 # program is dead but pid file exists
    fi
  else
    return 3 # program is not running
  fi
}

reload() {
  echo -n "Reload $name "
  if status ; then
    kill -HUP `cat "$pidfile"`
  fi
  ret=$?
  [ $ret -eq 0 ] && success || failure; echo
}

force_stop() {
  echo -n "Force stop $name "
  if status ; then
    stop
    status && kill -KILL `cat "$pidfile"`
  fi
  ret=$?
  [ $ret -eq 0 ] && success || failure; echo
}

configtest() {
  echo -n "Configtest $name "
  # Check if a config file exists
  if [ ! "$(ls -A ${LS_CONF_DIR}/* 2> /dev/null)" ]; then
    echo "There aren't any configuration files in ${LS_CONF_DIR}"
    return 1
  fi

  HOME=${LS_HOME}
  export PATH HOME

  test_args="--configtest -f ${LS_CONF_DIR} ${LS_OPTS}"
  $program ${test_args}
  [ $? -eq 0 ] && return 0
  # Program not configured
  return 6
  ret=$?
  [ $ret -eq 0 ] && success || failure; echo
}

case "$1" in
  start)
    status
    code=$?
    if [ $code -eq 0 ]; then
      echo "$name is already running "
    else
      start $2
      code=$?
    fi
    exit $code
    ;;
  stop) stop ;;
  force-stop) force_stop ;;
  status)
    status
    code=$?
    if [ $code -eq 0 ] ; then
      echo "$name is running "
    else
      echo "$name is not running "
    fi
    exit $code
    ;;
  reload) reload ;;
  restart)

    quiet configtest
    RET=$?
    if [ ${RET} -ne 0 ]; then
      echo "Configuration error. Not restarting. Re-run with configtest parameter for details"
      exit ${RET}
    fi
    stop && start
    ;;
  configtest)
    configtest
    exit $?
    ;;
  *)
    echo "Usage: $SCRIPTNAME {start|stop|force-stop|status|reload|restart|configtest}" >&2
    exit 3
  ;;
esac

exit $?
