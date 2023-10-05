@echo off
:: Define variables
set NodeVersion=14
set Branch=master
set RepoURL=https://your-repo-url.git
set NexusUrl=https://your-nexus-url.com
set Repository=your-nexus-repo
set Artifact=your-artifact
set CustomNodePath=%USERPROFILE%\nodejs
set CustomVSCodePath=%USERPROFILE%\VSCode
set RepoFolder=your-repo-folder-name
set LogFolder=C:\setup-logs
set LocalLogFolder=%USERPROFILE%\local-setup-logs

:: Create the log folder if it doesn't exist
if not exist "%LogFolder%" (
    mkdir "%LogFolder%"
) else (
    echo Log folder already exists.
)

:: Set the log file path
set LogFile=%LogFolder%\setup.log

:: Create a local log folder in the user profile if it doesn't exist
if not exist "%LocalLogFolder%" (
    mkdir "%LocalLogFolder%"
) else (
    echo Local log folder already exists.
)

:: Set the local log file path
set LocalLogFile=%LocalLogFolder%\setup.log

:: Redirect all output to the log file
(
    echo %DATE% %TIME% - Script started.
    :: ... (previous script code)

    :: Function to install Node.js and NPM
    :InstallNode
    echo %DATE% %TIME% - Installing Node.js and NPM...
    if not exist "%CustomNodePath%\node.exe" (
        echo %DATE% %TIME% - Attempting to install in the specified path...
        mkdir "%CustomNodePath%"
        bitsadmin /transfer NodeZip https://nodejs.org/dist/v%NodeVersion%/node-v%NodeVersion%-win-x64.zip "%TEMP%\node-v%NodeVersion%-win-x64.zip"
        powershell -Command "Expand-Archive -Path '%TEMP%\node-v%NodeVersion%-win-x64.zip' -DestinationPath '%CustomNodePath%' -Force"
        del "%TEMP%\node-v%NodeVersion%-win-x64.zip"
        setx PATH "%CustomNodePath%;%PATH%" /M
    ) else (
        echo %DATE% %TIME% - Node.js and NPM found in the specified path.
    )

    if not exist "%CustomNodePath%\node.exe" (
