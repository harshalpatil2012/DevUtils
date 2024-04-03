@echo off
setlocal enabledelayedexpansion

rem Set input file and output folder paths
set "input_file=C:\Logs\500000 Sales Records.csv"
set "output_folder=C:\updates"

rem Record start time
set "start_time=!TIME!"

rem Define variables
set "output_file=!output_folder!\%~n1_unique.txt"
set "total_records=0"
set "total_unique_records=0"
set "total_duplicate_records=0"
set "seen="

rem Process input file
for /f "usebackq delims=" %%a in ("%input_file%") do (
    set /a total_records+=1
    echo !seen! | findstr /c:"%%a" >nul
    if errorlevel 1 (
        echo %%a>> "!output_file!"
        set /a total_unique_records+=1
        set "seen=!seen!%%a
    ) else (
        set /a total_duplicate_records+=1
    )
)

rem Record end time
set "end_time=!TIME!"

rem Print statistics
echo Total records: %total_records%
echo Total unique records: %total_unique_records%
echo Total duplicate records: %total_duplicate_records%
echo Start time: %start_time%
echo End time: %end_time%

rem Calculate and print execution time
set "start_hour=!start_time:~0,2!"
set "start_minute=!start_time:~3,2!"
set "start_second=!start_time:~6,2!"
set "start_centisecond=!start_time:~9,2!"
set "end_hour=!end_time:~0,2!"
set "end_minute=!end_time:~3,2!"
set "end_second=!end_time:~6,2!"
set "end_centisecond=!end_time:~9,2!"

set /a "start_total_seconds=start_hour*3600+start_minute*60+start_second"
set /a "end_total_seconds=end_hour*3600+end_minute*60+end_second"
set /a "total_seconds=end_total_seconds-start_total_seconds"

echo Total execution time: !total_seconds! seconds
