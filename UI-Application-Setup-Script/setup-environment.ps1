# Define variables
$NodeVersion = "14"  # Change this to your desired Node.js version
$Branch = "master"   # Default branch
$RepoURL = "https://your-repo-url.git"  # Replace with your Git repository URL

# Set system environment variables (requires administrator privileges)
[Environment]::SetEnvironmentVariable("NODE_ENV", "development", [System.EnvironmentVariableTarget]::Machine)
[Environment]::SetEnvironmentVariable("BRANCH", $Branch, [System.EnvironmentVariableTarget]::Machine)

# Function to install Node.js and NPM in the default path if possible
function InstallNodeInDefaultPath {
    if (-Not (Test-Path "C:\Program Files\nodejs\node.exe")) {
        Write-Host "Node.js not found. Attempting to install in the default path..."
        $NodeInstallerPath = Join-Path $env:TEMP "node-v$NodeVersion-x64.msi"
        Invoke-WebRequest -Uri "https://nodejs.org/dist/v$NodeVersion/node-v$NodeVersion-x64.msi" -OutFile $NodeInstallerPath
        Start-Process -Wait -FilePath "msiexec.exe" -ArgumentList "/i $NodeInstallerPath /quiet"
        Remove-Item -Path $NodeInstallerPath
    }
}

# Function to install Node.js and NPM in a custom path if permission issues occur
function InstallNodeInCustomPath {
    Write-Host "Node.js installation in the default path failed. Installing in a custom path..."
    $CustomNodePath = "$env:USERPROFILE\nodejs"
    $NodeInstallerPath = Join-Path $env:TEMP "node-v$NodeVersion-x64.msi"
    Invoke-WebRequest -Uri "https://nodejs.org/dist/v$NodeVersion/node-v$NodeVersion-x64.msi" -OutFile $NodeInstallerPath
    Start-Process -Wait -FilePath "msiexec.exe" -ArgumentList "/i $NodeInstallerPath /quiet INSTALLDIR=$CustomNodePath"
    Remove-Item -Path $NodeInstallerPath
    [Environment]::SetEnvironmentVariable("PATH", "$CustomNodePath;$env:PATH", [System.EnvironmentVariableTarget]::Machine)
}

# Function to install Visual Studio Code in the default path if possible
function InstallVSCodeInDefaultPath {
    if (-Not (Test-Path "C:\Program Files\Microsoft VS Code\Code.exe")) {
        Write-Host "Visual Studio Code not found. Attempting to install in the default path..."
        $VSCodeInstallerPath = Join-Path $env:TEMP "vscode.zip"
        Invoke-WebRequest -Uri "https://update.code.visualstudio.com/latest/win32-archive/stable" -OutFile $VSCodeInstallerPath
        Expand-Archive -Path $VSCodeInstallerPath -DestinationPath "C:\Program Files\Microsoft VS Code" -Force
        Remove-Item -Path $VSCodeInstallerPath
    }
}

# Function to install Visual Studio Code in a custom path if permission issues occur
function InstallVSCodeInCustomPath {
    Write-Host "Visual Studio Code installation in the default path failed. Installing in a custom path..."
    $CustomVSCodePath = "$env:USERPROFILE\VSCode"
    $VSCodeInstallerPath = Join-Path $env:TEMP "vscode.zip"
    Invoke-WebRequest -Uri "https://update.code.visualstudio.com/latest/win32-archive/stable" -OutFile $VSCodeInstallerPath
    Expand-Archive -Path $VSCodeInstallerPath -DestinationPath $CustomVSCodePath -Force
    Remove-Item -Path $VSCodeInstallerPath
}

# Attempt to install Node.js and Visual Studio Code in default paths
InstallNodeInDefaultPath
InstallVSCodeInDefaultPath

# If installations failed, install in custom paths
if (-Not (Test-Path "C:\Program Files\nodejs\node.exe")) {
    InstallNodeInCustomPath
}

if (-Not (Test-Path "C:\Program Files\Microsoft VS Code\Code.exe")) {
    InstallVSCodeInCustomPath
}

# Download the code repository
Write-Host "Cloning the repository..."
git clone $RepoURL
Set-Location "your-repo-folder-name"  # Replace with the actual folder name

# Download UI dependencies
Write-Host "Installing UI dependencies..."
npm install
Write-Host "UI dependencies installed."

# Download artifacts from Nexus (replace with your Nexus URL and repository)
$NexusUrl = "https://your-nexus-url.com"
$Repository = "your-nexus-repo"
$Artifact = "your-artifact"

# Use Invoke-WebRequest to download artifacts from Nexus
# Example: Invoke-WebRequest -Uri "$NexusUrl/$Repository/$Artifact" -OutFile "downloaded-artifact.ext"

Write-Host "Artifacts downloaded from Nexus."

# Run the demo application
Write-Host "Running the demo application..."
npm start

Write-Host "Development environment setup complete."
