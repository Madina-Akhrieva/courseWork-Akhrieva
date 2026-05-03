# Windows PowerShell Verification Script
# Run this to verify all components are ready

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  Education System - Deployment Check" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$WORKSPACE = "c:\Users\madinaa\IdeaProjects\courseWork-2026-Marina"
$GREEN = "Green"
$RED = "Red"
$YELLOW = "Yellow"

Write-Host "[INFO] Checking application components..." -ForegroundColor Yellow
Write-Host ""

# 1. Java Version Check
Write-Host -NoNewline "1. Java 11+ installed: "
try {
    $javaVersion = java -version 2>&1 | Select-String "version" | Select-Object -First 1
    if ($javaVersion) {
        Write-Host "✓ YES ($javaVersion)" -ForegroundColor Green
    } else {
        Write-Host "✗ NO" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ NO" -ForegroundColor Red
}

# 2. Maven Check
Write-Host -NoNewline "2. Maven installed: "
try {
    $mavenVersion = mvn -v 2>&1 | Select-String "Apache Maven" | Select-Object -First 1
    if ($mavenVersion) {
        Write-Host "✓ YES" -ForegroundColor Green
    } else {
        Write-Host "✗ NO" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ NO" -ForegroundColor Red
}

# 3. Docker Check
Write-Host -NoNewline "3. Docker installed: "
try {
    $dockerVersion = docker --version 2>&1
    if ($dockerVersion -match "Docker") {
        Write-Host "✓ YES ($dockerVersion)" -ForegroundColor Green
    } else {
        Write-Host "✗ NO" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ NO" -ForegroundColor Red
}

# 4. PostgreSQL Docker Image
Write-Host -NoNewline "4. PostgreSQL image available: "
try {
    $psqlImage = docker images 2>&1 | Select-String "postgres.*16"
    if ($psqlImage) {
        Write-Host "✓ YES" -ForegroundColor Green
    } else {
        Write-Host "⚠ Will auto-download" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠ Docker not running" -ForegroundColor Yellow
}

# 5. Source Files Check
Write-Host -NoNewline "5. Source files compiled: "
if (Test-Path "$WORKSPACE\target\classes") {
    $classCount = (Get-ChildItem -Path "$WORKSPACE\target\classes" -Filter "*.class" -Recurse).Count
        Write-Host "[OK] YES ($classCount classes)" -ForegroundColor Green
} else {
    Write-Host "[FAIL] NO - Run: mvn clean compile" -ForegroundColor Red
}

# 6. FXML Resources Check
Write-Host -NoNewline "6. FXML resources present: "
$fxmlFiles = Get-ChildItem -Path "$WORKSPACE\target\classes" -Filter "*.fxml" -Recurse -ErrorAction SilentlyContinue
$fxmlCount = $fxmlFiles.Count
if ($fxmlCount -eq 3) {
    Write-Host "[OK] YES (3 views)" -ForegroundColor Green
} else {
    Write-Host "[WARN] Found $fxmlCount (expected 3)" -ForegroundColor Yellow
}

# 7. Configuration
Write-Host -NoNewline "7. application.properties: "
if (Test-Path "$WORKSPACE\target\classes\application.properties") {
    Write-Host "[OK] YES" -ForegroundColor Green
} else {
    Write-Host "[FAIL] NO" -ForegroundColor Red
}

# 8. Demo Data
Write-Host -NoNewline "8. demo-data.sql script: "
if (Test-Path "$WORKSPACE\target\classes\db\demo-data.sql") {
    Write-Host "[OK] YES" -ForegroundColor Green
} else {
    Write-Host "[FAIL] NO" -ForegroundColor Red
}

# 9. Docker Compose
Write-Host -NoNewline "9. docker-compose.yml: "
if (Test-Path "$WORKSPACE\docker-compose.yml") {
    Write-Host "[OK] YES" -ForegroundColor Green
} else {
    Write-Host "[FAIL] NO" -ForegroundColor Red
}

# 10. Port Availability
Write-Host -NoNewline "10. Port 5050 available: "
$portInUse = netstat -ano 2>$null | Select-String ":5050"
if (-not $portInUse) {
    Write-Host "[OK] YES" -ForegroundColor Green
} else {
    Write-Host "[WARN] Port may be in use" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  Ready to Launch" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor White
Write-Host ""
Write-Host "  1. Start PostgreSQL:" -ForegroundColor White
    Write-Host "     docker-compose up -d" -ForegroundColor Magenta
Write-Host ""
Write-Host "  2. Start Server (Terminal 1):" -ForegroundColor White
    Write-Host "     mvn exec:java -Dexec.mainClass=`"org.example.coursework2026marina.server.ServerLauncher`"" -ForegroundColor Magenta
Write-Host ""
Write-Host "  3. Start Client (Terminal 2):" -ForegroundColor White
    Write-Host "     mvn javafx:run" -ForegroundColor Magenta
Write-Host ""
Write-Host "  Login with:" -ForegroundColor White
    Write-Host "    Admin:   admin / admin123" -ForegroundColor White
    Write-Host "    Student: student / student123" -ForegroundColor White
Write-Host ""
