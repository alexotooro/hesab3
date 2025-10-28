#!/usr/bin/env sh

##############################################################################
## Gradle start up script for UN*X
##############################################################################

APP_HOME=$(dirname "$0")
APP_HOME=$(cd "$APP_HOME" && pwd)

if [ -n "$JAVA_HOME" ] ; then
    JAVA_EXE="$JAVA_HOME/bin/java"
else
    JAVA_EXE=java
fi

exec "$JAVA_EXE" -Xmx64m -Xms64m -cp "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
