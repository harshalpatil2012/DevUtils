@echo off
set /p port="Enter the port number you want to free up: "

echo Checking for processes on port %port%...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr /R /C:":%port% "') do (
    set pid=%%a
)

if defined pid (
    echo Process found with PID %pid%, attempting to kill...
    taskkill /F /PID %pid%
    if %errorlevel% == 0 (
        echo Process killed successfully.
    ) else (
        echo Failed to kill process. Check if you have sufficient permissions.
    )
) else (
    echo No process found running on port %port%.
)

pause
