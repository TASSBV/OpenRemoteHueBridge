#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ============================================================================
#
# Modifications added for specific OpenRemote use scenarios. 
# Copyright 2008-2012 OpenRemote, Inc.
#
# Authors:
#   Juha Lindfors (juha@openremote.org)
#
#
# Following environment variables are supported to configure logging:
#
#   CONTROLLER_CONSOLE_THRESHOLD
#     
#     Limits the messages targeted for standard output stream based on log
#     message level. Valid level values in descending order of importance
#     are:
#
#       1) OFF
#       2) ERROR
#       3) WARN or WARNING
#       4) INFO
#       5) DEBUG
#       6) TRACE
#       7) ALL
#
#     Default value for standard output stream is to print messages with
#     level INFO or above. To set a different level, use for example:
#
#       > export CONTROLLER_CONSOLE_THRESHOLD=ERROR
#
#     before executing this script.
#
#
#  CONTROLLER_STARTUP_LOG_LEVEL
#
#    Sets the level of log messages recorded by controller bootstrap
#    services. See valid level values in CONTROLLER_CONSOLE_THRESHOLD
#    above. Notice that individual log targets may use their threshold
#    settings to override recording these log messages.
#
# ============================================================================
#
#
# -----------------------------------------------------------------------------
# Start/Stop Script for the CATALINA Server
#
# Environment Variable Prequisites
#
#   CATALINA_HOME   May point at your Catalina "build" directory.
#
#   CATALINA_BASE   (Optional) Base directory for resolving dynamic portions
#                   of a Catalina installation.  If not present, resolves to
#                   the same directory that CATALINA_HOME points to.
#
#   CATALINA_OPTS   (Optional) Java runtime options used when the "start",
#                   or "run" command is executed.
#
#   CATALINA_TMPDIR (Optional) Directory path location of temporary directory
#                   the JVM should use (java.io.tmpdir).  Defaults to
#                   $CATALINA_BASE/temp.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#                   Required to run the with the "debug" or "javac" argument.
#
#   JRE_HOME        Must point at your Java Development Kit installation.
#                   Defaults to JAVA_HOME if empty.
#
#   JAVA_OPTS       (Optional) Java runtime options used when the "start",
#                   "stop", or "run" command is executed.
#
#   JPDA_TRANSPORT  (Optional) JPDA transport used when the "jpda start"
#                   command is executed. The default is "dt_socket".
#
#   JPDA_ADDRESS    (Optional) Java runtime options used when the "jpda start"
#                   command is executed. The default is 8000.
#
#   JPDA_SUSPEND    (Optional) Java runtime options used when the "jpda start"
#                   command is executed. Specifies whether JVM should suspend
#                   execution immediately after startup. Default is "n".
#
#   JPDA_OPTS       (Optional) Java runtime options used when the "jpda start"
#                   command is executed. If used, JPDA_TRANSPORT, JPDA_ADDRESS,
#                   and JPDA_SUSPEND are ignored. Thus, all required jpda
#                   options MUST be specified. The default is:
#
#                   -Xdebug -Xrunjdwp:transport=$JPDA_TRANSPORT,
#                       address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND
#
#   JSSE_HOME       (Optional) May point at your Java Secure Sockets Extension
#                   (JSSE) installation, whose JAR files will be added to the
#                   system class path used to start Tomcat.
#
#   CATALINA_PID    (Optional) Path of the file which should contains the pid
#                   of catalina startup java process, when start (fork) is used
#
# $Id: catalina.sh 656834 2008-05-15 21:04:04Z markt $
# -----------------------------------------------------------------------------

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
Darwin*) darwin=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set CATALINA_HOME if not already set
[ -z "$CATALINA_HOME" ] && CATALINA_HOME=`cd "$PRGDIR/.." ; pwd`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$JRE_HOME" ] && JRE_HOME=`cygpath --unix "$JRE_HOME"`
  [ -n "$CATALINA_HOME" ] && CATALINA_HOME=`cygpath --unix "$CATALINA_HOME"`
  [ -n "$CATALINA_BASE" ] && CATALINA_BASE=`cygpath --unix "$CATALINA_BASE"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
  [ -n "$JSSE_HOME" ] && JSSE_HOME=`cygpath --absolute --unix "$JSSE_HOME"`
fi

if [ -r "$CATALINA_HOME"/bin/tomcat/setclasspath.sh ]; then
  BASEDIR="$CATALINA_HOME"
  . "$CATALINA_HOME"/bin/tomcat/setclasspath.sh
else
  echo "Cannot find $CATALINA_HOME/bin/tomcat/setclasspath.sh"
  echo "This file is needed to run this program"
  exit 1
fi

# Add on extra jar files to CLASSPATH
if [ -n "$JSSE_HOME" ]; then
  CLASSPATH="$CLASSPATH":"$JSSE_HOME"/lib/jcert.jar:"$JSSE_HOME"/lib/jnet.jar:"$JSSE_HOME"/lib/jsse.jar
fi
CLASSPATH="$CLASSPATH":"$CATALINA_HOME"/bin/tomcat/bootstrap.jar

if [ -z "$CATALINA_BASE" ] ; then
  CATALINA_BASE="$CATALINA_HOME"
fi

if [ -z "$CATALINA_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for Catalina
  CATALINA_TMPDIR="$CATALINA_BASE"/temp
fi

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JRE_HOME=`cygpath --absolute --windows "$JRE_HOME"`
  CATALINA_HOME=`cygpath --absolute --windows "$CATALINA_HOME"`
  CATALINA_BASE=`cygpath --absolute --windows "$CATALINA_BASE"`
  CATALINA_TMPDIR=`cygpath --absolute --windows "$CATALINA_TMPDIR"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  [ -n "$JSSE_HOME" ] && JSSE_HOME=`cygpath --absolute --windows "$JSSE_HOME"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi

# Set juli LogManager if it is present
if [ -r "$CATALINA_BASE"/conf/logging.properties ]; then
  JAVA_OPTS="$JAVA_OPTS -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager"
  LOGGING_CONFIG="-Djava.util.logging.config.file=$CATALINA_BASE/conf/logging.properties"
fi



# ===== OPENREMOTE SETUP ==============================================================


# Set up the directory that contains native libraries for the OpenRemote Controller
LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$CATALINA_BASE/webapps/controller/WEB-INF/lib/native"


##
# Set Tomcat's console logging to match controller's console output threshold.
# Note that TC uses JUL log level names where as the controller uses Log4j
# threshold names so we need to do some mapping of values below...
##
setTomcatConsoleLevel()
{

  TOMCAT_SERVER_CONSOLE_LOG_LEVEL="$CONTROLLER_CONSOLE_THRESHOLD";

  case "$CONTROLLER_CONSOLE_THRESHOLD" in

    # Map ERROR to JUL SEVERE (log4j uses ERROR directly)

    ERROR)
      TOMCAT_SERVER_CONSOLE_LOG_LEVEL=SEVERE ;;

    # Map WARN or WARNING to JUL WARNING and log4j WARN levels...

    WARN | WARNING)
      TOMCAT_SERVER_CONSOLE_LOG_LEVEL=WARNING
      CONTROLLER_CONSOLE_THRESHOLD=WARN
      ;;

    # Map DEBUG to JUL FINE (log4j uses DEBUG directly)...

    DEBUG)
      TOMCAT_SERVER_CONSOLE_LOG_LEVEL=FINE ;;

    # Map TRACE to JUL FINER (log4j uses TRACE directly)...

    TRACE)
      TOMCAT_SERVER_CONSOLE_LOG_LEVEL=FINER ;;

  esac

}

printTomcatEnvVariables()
{
  # only output this if we have a TTY
  if [ $have_tty -eq 1 ]; then

    echo "Using CATALINA_BASE:   $CATALINA_BASE"
    echo "Using CATALINA_HOME:   $CATALINA_HOME"
    echo "Using CATALINA_TMPDIR: $CATALINA_TMPDIR"

    if [ "$1" = "debug" -o "$1" = "javac" ] ; then
      echo "Using JAVA_HOME:       $JAVA_HOME"
    else
      echo "Using JRE_HOME:       $JRE_HOME"
    fi
  fi
}


##
#  Executes the Tomcat runtime.
#
#  1st arg -- path to java executable
#  2nd arg -- JAVA_OPTS
#  3rd arg -- classpath
#  4th arg -- if contains 'service' runs tomcat as background process with redirected std streams
#
##
executeTomcat()
{
  local PID_REDIRECT=")"

  if [ "$4" = "service" ]; then

    # Send standard error stream to standard out, direct standard out to a file, limit std out
    # file to first 50000 characters. Note that the redirect will automatically switch to a
    # buffered mode so stdout will appear in chunks, size of which is defined by the operating
    # system. For more detailed handling of std output, tools such as logrotate can be used.
    local REDIRECT="| head -c 50000 >> \"$CATALINA_BASE/logs/container/stderrout.log\" 2>&1 &"

    # If CATALINA_PID has been specified, we need to capture the PID of the Java process
    # Must use a separate file descriptor to capture that as the output is piped to a file
    # See http://stackoverflow.com/questions/1652680/how-to-get-the-pid-of-a-process-that-is-piped-to-another-process-in-bash
    if [ ! -z "$CATALINA_PID" ]; then
      local PID_REDIRECT=" & echo \$! >&3) 3>"$CATALINA_PID
    fi

  fi

  eval "(\"$1\"" \
            -Dcatalina.home=\"$CATALINA_HOME\" \
            -Dcatalina.base=\"$CATALINA_BASE\" \
            -Djava.io.tmpdir=\"$CATALINA_TMPDIR\" \
            -Dtomcat.server.console.log.level="$TOMCAT_SERVER_CONSOLE_LOG_LEVEL" \
            -Dopenremote.controller.startup.log.level="$CONTROLLER_STARTUP_LOG_LEVEL" \
            -Dopenremote.controller.console.threshold="$CONTROLLER_CONSOLE_THRESHOLD" \
            -Djava.library.path=\"$CATALINA_BASE/webapps/controller/WEB-INF/lib/native\" \
            \"$LOGGING_CONFIG\" $2  -classpath \"$3\" org.apache.catalina.startup.Bootstrap start $PID_REDIRECT $REDIRECT

}

# Execute OpenRemote 'run' target...

if [ "$1" = "run" ]; then

  # Configure logging when 'blocking' run target is executed (assumes development
  # or troubleshoot environment)...

  # Default startup log to DEBUG level unless explicitly set with env variable...

  if [ -z "${CONTROLLER_STARTUP_LOG_LEVEL}" ] ; then
    CONTROLLER_STARTUP_LOG_LEVEL=DEBUG
  fi  

  # Default standard out (console) output to INFO level unless explicitly set
  # with env variable...

  if [ -z "${CONTROLLER_CONSOLE_THRESHOLD}" ] ; then
    CONTROLLER_CONSOLE_THRESHOLD=INFO
  fi

  # set Tomcat's console logging level to match controller console logging level,
  # pass log options to JVM and print the env variable values...

  setTomcatConsoleLevel
  printTomcatEnvVariables

  # Let the user know how the logging has been configured...

  echo ""
  echo "---- Logging ----------------------------------------------------------"
  echo ""
  echo " Console (stdout) threshold [CONTROLLER_CONSOLE_THRESHOLD]: $CONTROLLER_CONSOLE_THRESHOLD"
  echo ""
  echo " System logs:"
  echo ""
  echo "   - Controller startup log [CONTROLLER_STARTUP_LOG_LEVEL]: $CONTROLLER_STARTUP_LOG_LEVEL"
  echo "     "
  echo ""
  echo "-----------------------------------------------------------------------"

  # run tomcat...

  executeTomcat "$_RUNJAVA" "$JAVA_OPTS" "$CLASSPATH"



# Execute OpenRemote 'start' target...

elif [ "$1" = "start" ]; then

  # Default startup log to INFO level unless explicitly set with env variable...

  if [ -z "${CONTROLLER_STARTUP_LOG_LEVEL}" ] ; then
    CONTROLLER_STARTUP_LOG_LEVEL=INFO
  fi

  # Default standard out (console) output to OFF unless explicitly set
  # with env variable...

  if [ -z "${CONTROLLER_CONSOLE_THRESHOLD}" ] ; then
    CONTROLLER_CONSOLE_THRESHOLD=OFF
  fi


  # set Tomcat's console logging level to match controller console logging level,
  # pass log options to JVM and print the env variable values...

  setTomcatConsoleLevel
  printTomcatEnvVariables

  # run Tomcat as service...

  executeTomcat "$_RUNJAVA" "$JAVA_OPTS" "$CLASSPATH" service


elif [ "$1" = "stop" ] ; then

  shift
  FORCE=0
  if [ "$1" = "-force" ]; then
    shift
    FORCE=1
  fi

  "$_RUNJAVA" $JAVA_OPTS \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
    -Dcatalina.base="$CATALINA_BASE" \
    -Dcatalina.home="$CATALINA_HOME" \
    -Djava.io.tmpdir="$CATALINA_TMPDIR" \
    org.apache.catalina.startup.Bootstrap "$@" stop

  if [ $FORCE -eq 1 ]; then
    if [ ! -z "$CATALINA_PID" ]; then
       echo "Killing: `cat $CATALINA_PID`"
       kill -9 `cat $CATALINA_PID`
    else
       echo "Kill failed: \$CATALINA_PID not set"
    fi
  fi

else

  echo "Usage: openremote.sh ( commands ... )"
  echo "commands:"
  echo "  run"
  echo "       Start OpenRemote Controller in development mode in the current window"
  echo ""
  echo "  start"
  echo "       Start OpenRemote Controller as a background process"
  echo ""
  echo "  stop"
  echo "       Stop OpenRemote Controller"
  echo ""
  echo "  stop -force"
  echo "       Stop OpenRemote Controller (followed by kill -KILL)"
  exit 1

fi

# ======================================================================================



# ----- Execute The Requested Command -----------------------------------------
#
# Bugzilla 37848: only output this if we have a TTY
#if [ $have_tty -eq 1 ]; then
#  echo "Using CATALINA_BASE:   $CATALINA_BASE"
#  echo "Using CATALINA_HOME:   $CATALINA_HOME"
#  echo "Using CATALINA_TMPDIR: $CATALINA_TMPDIR"
#  if [ "$1" = "debug" -o "$1" = "javac" ] ; then
#    echo "Using JAVA_HOME:       $JAVA_HOME"
#  else
#    echo "Using JRE_HOME:       $JRE_HOME"
#  fi
#fi
#
#if [ "$1" = "jpda" ] ; then
#  if [ -z "$JPDA_TRANSPORT" ]; then
#    JPDA_TRANSPORT="dt_socket"
#  fi
#  if [ -z "$JPDA_ADDRESS" ]; then
#    JPDA_ADDRESS="8000"
#  fi
#  if [ -z "$JPDA_SUSPEND" ]; then
#    JPDA_SUSPEND="n"
#  fi
#  if [ -z "$JPDA_OPTS" ]; then
#    JPDA_OPTS="-agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND"
#  fi
#  CATALINA_OPTS="$CATALINA_OPTS $JPDA_OPTS"
#  shift
#fi
#
#if [ "$1" = "debug" ] ; then
#  shift
#  if [ "$1" = "-security" ] ; then
#    echo "Using Security Manager"
#    shift
#    exec "$_RUNJDB" $JAVA_OPTS "$LOGGING_CONFIG" $CATALINA_OPTS \
#      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
#      -sourcepath "$CATALINA_HOME"/../../java \
#      -Djava.security.manager \
#      -Djava.security.policy=="$CATALINA_BASE"/conf/catalina.policy \
#      -Dcatalina.base="$CATALINA_BASE" \
#      -Dcatalina.home="$CATALINA_HOME" \
#      -Djava.io.tmpdir="$CATALINA_TMPDIR" \
#      org.apache.catalina.startup.Bootstrap "$@" start
#  else
#    exec "$_RUNJDB" $JAVA_OPTS "$LOGGING_CONFIG" $CATALINA_OPTS \
#      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
#      -sourcepath "$CATALINA_HOME"/../../java \
#      -Dcatalina.base="$CATALINA_BASE" \
#      -Dcatalina.home="$CATALINA_HOME" \
#      -Djava.io.tmpdir="$CATALINA_TMPDIR" \
#      org.apache.catalina.startup.Bootstrap "$@" start
#  fi
#
#elif [ "$1" = "run" ]; then
#
#  shift
#  if [ "$1" = "-security" ] ; then
#    echo "Using Security Manager"
#    shift
#    exec "$_RUNJAVA" $JAVA_OPTS "$LOGGING_CONFIG" $CATALINA_OPTS \
#      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
#      -Djava.security.manager \
#      -Djava.security.policy=="$CATALINA_BASE"/conf/catalina.policy \
#      -Dcatalina.base="$CATALINA_BASE" \
#      -Dcatalina.home="$CATALINA_HOME" \
#      -Djava.io.tmpdir="$CATALINA_TMPDIR" \
#      org.apache.catalina.startup.Bootstrap "$@" start
#  else
#    exec "$_RUNJAVA" $JAVA_OPTS "$LOGGING_CONFIG" $CATALINA_OPTS \
#      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
#      -Dcatalina.base="$CATALINA_BASE" \
#      -Dcatalina.home="$CATALINA_HOME" \
#      -Djava.io.tmpdir="$CATALINA_TMPDIR" \
#      org.apache.catalina.startup.Bootstrap "$@" start
#  fi
#
#elif [ "$1" = "start" ] ; then
#
#  shift
#  touch "$CATALINA_BASE"/logs/catalina.out
#  if [ "$1" = "-security" ] ; then
#    echo "Using Security Manager"
#    shift
#    "$_RUNJAVA" $JAVA_OPTS "$LOGGING_CONFIG" $CATALINA_OPTS \
#      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
#      -Djava.security.manager \
#      -Djava.security.policy=="$CATALINA_BASE"/conf/catalina.policy \
#      -Dcatalina.base="$CATALINA_BASE" \
#      -Dcatalina.home="$CATALINA_HOME" \
#      -Djava.io.tmpdir="$CATALINA_TMPDIR" \
#      org.apache.catalina.startup.Bootstrap "$@" start \
#      |head -c 500 >> "$CATALINA_BASE"/logs/catalina.out 2>&1 &
#
#      if [ ! -z "$CATALINA_PID" ]; then
#        echo $! > $CATALINA_PID
#      fi
#  else
#    "$_RUNJAVA" $JAVA_OPTS "$LOGGING_CONFIG" $CATALINA_OPTS \
#      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
#      -Dcatalina.base="$CATALINA_BASE" \
#      -Dcatalina.home="$CATALINA_HOME" \
#      -Djava.io.tmpdir="$CATALINA_TMPDIR" \
#      org.apache.catalina.startup.Bootstrap "$@" start \
#      |head -c 500 >> "$CATALINA_BASE"/logs/catalina.out 2>&1 &
#
#      if [ ! -z "$CATALINA_PID" ]; then
#        echo $! > $CATALINA_PID
#      fi
#  fi
#
#if [ "$1" = "stop" ] ; then
#
#  shift
#  FORCE=0
#  if [ "$1" = "-force" ]; then
#    shift
#    FORCE=1
#  fi
#
#  "$_RUNJAVA" $JAVA_OPTS \
#    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
#    -Dcatalina.base="$CATALINA_BASE" \
#    -Dcatalina.home="$CATALINA_HOME" \
#    -Djava.io.tmpdir="$CATALINA_TMPDIR" \
#    org.apache.catalina.startup.Bootstrap "$@" stop
#
#  if [ $FORCE -eq 1 ]; then
#    if [ ! -z "$CATALINA_PID" ]; then
#       echo "Killing: `cat $CATALINA_PID`"
#       kill -9 `cat $CATALINA_PID`
#    else
#       echo "Kill failed: \$CATALINA_PID not set"
#    fi
#  fi
#
#elif [ "$1" = "version" ] ; then
#
#    "$_RUNJAVA"   \
#      -classpath "$CATALINA_HOME/lib/catalina.jar" \
#      org.apache.catalina.util.ServerInfo
#
#else
#
#  echo "Usage: openremote.sh ( commands ... )"
#  echo "commands:"
#  echo "  debug             Start Catalina in a debugger"
#  echo "  debug -security   Debug Catalina with a security manager"
#  echo "  jpda start        Start Catalina under JPDA debugger"
#  echo "  run               Start OpenRemote Controller in the current window"
#  echo "  run -security     Start in the current window with security manager"
#  echo "  start             Start OpenRemote Controller as a background process"
#  echo "  start -security   Start in a separate window with security manager"
#  echo "  stop              Stop OpenRemote Controller"
#  echo "  stop -force       Stop OpenRemote Controller (followed by kill -KILL)"
#  echo "  version           Runtime server information"
#  exit 1
#
#fi


