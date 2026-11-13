@echo off
title FindIt - Smart Lost & Found System
echo ==============================================================
echo     FindIt - Smart Lost and Found System (Java 17 / Spring Boot)
echo ==============================================================
echo.

:: Detect Java
where java >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot\bin\java.exe" (
        set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot"
        set "PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot\bin;%PATH%"
    ) else if defined JAVA_HOME (
        set "PATH=%JAVA_HOME%\bin;%PATH%"
    ) else (
        echo [ERROR] Java 17 was not found in your PATH or JAVA_HOME.
        pause
        exit /b 1
    )
)

echo [OK] Using Java:
java -version
echo.

:: Detect Maven
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Users\VICKY KUMAR SAH\.gemini\antigravity\scratch\apache-maven\apache-maven-3.9.6\bin\mvn.cmd" (
        set "PATH=C:\Users\VICKY KUMAR SAH\.gemini\antigravity\scratch\apache-maven\apache-maven-3.9.6\bin;%PATH%"
    )
)

echo [INFO] Starting FindIt application on http://localhost:8080...
mvn spring-boot:run
pause
