@echo off
set LOG_FILE=C:\logs\cleanup_logs.txt

for /D %%i in (C:\path\to\base\*) do (
    echo Processing repository in folder: %%i
    cd /D "%%i"

    REM Check if the current folder is a Git repository
    if exist ".git" (
        echo Cleaning up merged branches and finding branches with no commits...
        call :cleanup
    ) else (
        echo Skipped: Not a Git repository.
    )

    echo.
)

echo "Cleanup process complete. Logs written to %LOG_FILE%"
pause
exit /b

:cleanup
git checkout main
git fetch --all

echo "Merged branches:" >> "%LOG_FILE%"
git branch --merged main | findstr /v /c:"*" | findstr /r "release/ develop/" | sed "s/\r//" >> "%LOG_FILE%"

echo -e "\nBranches with no commits:" >> "%LOG_FILE%"
for /f "delims=" %%b in ('git for-each-ref --format "%%(refname:short)" refs/heads/*') do (
    set branch=%%b
    git log -n 1 !branch! > nul 2>&1
    if not errorlevel 1 (
        echo !branch! >> "%LOG_FILE%"
    )
)

echo "Cleanup logs written to %LOG_FILE%"
exit /b
