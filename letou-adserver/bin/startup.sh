#!/bin/sh
# script directory
SCRIPT_PATH=$(readlink -f $0)
# bin directory, It's like /opt/appname/bin, also same with SCRIPT_PATH
BIN_DIR=$(dirname $SCRIPT_PATH)
echo "BIN_DIR=$BIN_DIR"
# work directory
WORK_DIR=$(dirname $BIN_DIR)
echo "WORK_DIR=$WORK_DIR"
# where is app root path in location
APP_ROOT=`cat $BIN_DIR'/base.conf' | grep 'app.root.dir' | cut -d = -f 2`
echo "APP_ROOT=$APP_ROOT"
# app mainclass
MAINCLASS=`cat $BIN_DIR'/base.conf' |grep 'app.mainclass' | cut -d = -f 2`
echo "Execution=$MAINCLASS"
# where is app lib path in location
LIB_DIR=${APP_ROOT}'/lib'
# app conf directory
APP_CONF=${APP_ROOT}'/conf/'
# app all jar file path
FILE_LIST=''
for filename in `ls $LIB_DIR`
        do
                if [ -f $LIB_DIR'/'$filename ] ; then
                        FILE_LIST=${FILE_LIST}${LIB_DIR}'/'$filename':'
                fi
        done
if [ "$JAVA_HOME" = "" ]; then
        echo "JAVA_HOME not found."
        exit 1
fi
echo "Use JAVA_HOME=$JAVA_HOME"
JAVA_CMD=${JAVA_HOME}'/bin/java'
echo "JAVA_CMD=$JAVA_CMD"
JAVA_OPTIONS=" -server -Xms1g -Xmx4g -XX:MaxPermSize=256m "
CLASSPATH="-cp ${FILE_LIST}${APP_CONF}"
echo "CLASSPATH=$CLASSPATH"
$JAVA_CMD $JAVA_OPTIONS $CLASSPATH -Dworkdir=$WORK_DIR $MAINCLASS

JAVA_PID=$!
echo "$JAVA_PID">$BIN_DIR/server.pid