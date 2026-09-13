@echo off
cd /d "%~dp0"
echo Starting Stock Trading Platform...
java -cp target/classes com.stocktrading.Main
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with an error.
    pause
)
