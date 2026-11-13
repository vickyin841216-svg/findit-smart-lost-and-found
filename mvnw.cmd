@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SETLOCAL LEAVE_READONLY=1

@SET MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
@IF NOT EXIST %MAVEN_JAVA_EXE% (
  @SET MAVEN_JAVA_EXE=java.exe
)

where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
  mvn %*
  exit /B %ERRORLEVEL%
)

echo [mvnw] Apache Maven not detected in PATH.
echo [mvnw] Please ensure Maven is installed or import this Maven project into IntelliJ IDEA, Eclipse, or VS Code.
pause
exit /B 1
