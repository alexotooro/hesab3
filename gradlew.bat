@echo off
set DIR=%~dp0
set APP_HOME=%DIR%
set JAVA_EXE=java
if defined JAVA_HOME (
    set JAVA_EXE=%JAVA_HOME%\bin\java.exe
)
"%JAVA_EXE%" -Xmx64m -Xms64m -cp "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
