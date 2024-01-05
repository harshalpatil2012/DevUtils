@echo off

set DESTINATION_DIR=C:\path\to\destination  :: Replace with your desired destination

setlocal enabledelayedexpansion

set bitbucket_url_branch_map[0]=https://bitbucket.org/user/repo1.git master
set bitbucket_url_branch_map[1]=https://bitbucket.org/user/repo2.git develop
set bitbucket_url_branch_map[2]=https://bitbucket.org/user/repo3.git feature-branch

rem Get the count of elements in the array
set "bitbucket_url_branch_map_count=0"
for /L %%i in (0,1,100) do (
    set "key=!bitbucket_url_branch_map[%%i]!"
    if defined key (
        set /a bitbucket_url_branch_map_count+=1
    ) else (
        rem Exit the loop when there are no more elements
        goto :break_loop
    )
)
:break_loop

rem Create or clear the error log file
type nul > error_log.txt

rem Iterate through the array
for /L %%i in (0,1,%bitbucket_url_branch_map_count%) do (
    set "line=!bitbucket_url_branch_map[%%i]!"
    for /F "tokens=1,* delims= " %%a in ("!line!") do (
        set "bitbucket_url=%%a"
        set "branch=%%b"

        for %%p in ("!bitbucket_url!") do set "repo_name=%%~np"
        set "repo_path=!DESTINATION_DIR!\!repo_name!"

        if exist "!repo_path!" (
            cd "!repo_path!" || ( echo Error: Unable to navigate to !repo_path! folder & echo Error: Unable to navigate to !repo_path! folder >> error_log.txt & exit /B 1 )
            git pull origin "!branch!" || ( echo Error: Unable to pull latest changes in !repo_path! & echo Error: Unable to pull latest changes in !repo_path! >> error_log.txt & exit /B 1 )
        ) else (
            git clone --branch "!branch!" --depth 1 "!bitbucket_url!" "!repo_path!" || ( echo Error: Unable to clone !bitbucket_url! & echo Error: Unable to clone !bitbucket_url! >> error_log.txt & exit /B 1 )
            echo Cloned !bitbucket_url! to !repo_path!
        )

        echo Checked out/pulled !branch! in !repo_path!

        cd .. || ( echo Error: Unable to navigate back to the original directory & echo Error: Unable to navigate back to the original directory >> error_log.txt & exit /B 1 )
    )
)

endlocal
