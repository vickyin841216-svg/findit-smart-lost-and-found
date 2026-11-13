Write-Host "==============================================================" -ForegroundColor Cyan
Write-Host "    FindIt - Smart Lost & Found System (Java 17 / Spring Boot)" -ForegroundColor Cyan
Write-Host "==============================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Resolve Java JDK 17
$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javaCmd) {
    $knownJdkPaths = @(
        "C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot\bin",
        "$env:JAVA_HOME\bin"
    )
    foreach ($p in $knownJdkPaths) {
        if (Test-Path "$p\java.exe") {
            $env:JAVA_HOME = Split-Path $p -Parent
            $env:PATH = "$p;" + $env:PATH
            break
        }
    }
    $javaCmd = Get-Command java -ErrorAction SilentlyContinue
}

if (-not $javaCmd) {
    Write-Host "[ERROR] Java 17+ was not found." -ForegroundColor Red
    Write-Host "Please ensure JDK 17 is installed." -ForegroundColor Yellow
    Read-Host "Press Enter to exit..."
    exit 1
}

Write-Host "[OK] Using Java:" -ForegroundColor Green
java -version
Write-Host ""

# 2. Resolve Maven
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnCmd) {
    $knownMavenPaths = @(
        "C:\Users\VICKY KUMAR SAH\.gemini\antigravity\scratch\apache-maven\apache-maven-3.9.6\bin"
    )
    foreach ($m in $knownMavenPaths) {
        if (Test-Path "$m\mvn.cmd") {
            $env:PATH = "$m;" + $env:PATH
            break
        }
    }
    $mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue
}

if ($mvnCmd) {
    Write-Host "[INFO] Starting FindIt application via Maven..." -ForegroundColor Cyan
    Write-Host ">> Access Portal at: http://localhost:8080" -ForegroundColor Green
    mvn spring-boot:run
} else {
    Write-Host "[ERROR] Maven executable not found." -ForegroundColor Red
    Read-Host "Press Enter to exit..."
}
