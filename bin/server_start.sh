#!/bin/bash
# Script to start the job server
# Extra arguments will be spark-submit options, for example
#  ./server_start.sh --jars cassandra-spark-connector.jar
#
# Environment vars (note settings.sh overrides):
#   JOBSERVER_MEMORY - defaults to 1G, the amount of memory (eg 512m, 2G) to give to job server
#   JOBSERVER_CONFIG - alternate configuration file to use
#   JOBSERVER_FG    - launches job server in foreground; defaults to forking in background
set -e

get_abs_script_path() {
  pushd . >/dev/null
  cd "$(dirname "$0")"
  appdir=$(pwd)
  popd  >/dev/null
}

get_abs_script_path

set -a
. $appdir/setenv.sh
set +a

GC_OPTS_SERVER="$GC_OPTS_BASE -Xloggc:$appdir/$GC_OUT_FILE_NAME"

MAIN="spark.jobserver.JobServer"

PIDFILE=$appdir/spark-jobserver.pid
if [ -f "$PIDFILE" ] && kill -0 $(cat "$PIDFILE"); then
   echo 'Job server is already running'
   exit 1
fi

# The following lines are added for mineset. Uncomment when adding to /3rdparty/spark/template/...
#ASSEMBLY=@MINESET_OPT@/spark/dist/mineset-spark-assembly-1.0.jar
#if [ ! -e $ASSEMBLY ]; then
#   echo "$ASSEMBLY does not exist"
#   exit 1
#fi
#JARS=@MINESET_TMP@/spark-jobserver/jars
#cp -p $ASSEMBLY $JARS/mineset-spark.jar

#ls -1 @MINESET_TMP@/spark-jobserver/sqldao/data/mineset-spark-*.jar | \
#    grep -v `date +%Y-%m-%d` | xargs rm -f



cmd='$SPARK_HOME/bin/spark-submit --class $MAIN --driver-memory $JOBSERVER_MEMORY
  --conf "spark.executor.extraJavaOptions=$LOGGING_OPTS"
  --driver-java-options "$GC_OPTS_SERVER $JAVA_OPTS_SERVER $LOGGING_OPTS $CONFIG_OVERRIDES"
  --driver-class-path "/opt/Mineset/web/lib/mysql-connector-java-5.1.25.jar"
  $@ $appdir/spark-job-server.jar $conffile'
if [ -z "$JOBSERVER_FG" ]; then
  eval $cmd > $LOG_DIR/server_start.log 2>&1 < /dev/null &
  echo $! > $PIDFILE
else
  eval $cmd
fi
