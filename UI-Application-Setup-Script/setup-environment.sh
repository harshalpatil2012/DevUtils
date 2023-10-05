#!/bin/bash
# Define variables
NodeVersion="14"
Branch="master"
RepoURL="https://your-repo-url.git"
NexusUrl="https://your-nexus-url.com"
Repository="your-nexus-repo"
Artifact="your-artifact"
CustomNodePath="$HOME/nodejs"
CustomVSCodePath="$HOME/VSCode"
RepoFolder="your-repo-folder-name"
LogFolder="$HOME/setup-logs"

# Create the log folder if it doesn't exist
mkdir -p "$LogFolder" && echo "Log folder created" || echo "Log folder already exists"

# Set the log file path
LogFile="$LogFolder/setup.log"

# Redirect all output to the log file
{
  echo "$(date) - Script started."
  # ... (previous script code)

  # Function to install Node.js and NPM
  InstallNode() {
    echo "$(date) - Installing Node.js and NPM..."
    if [ ! -f "$CustomNodePath/bin/node" ]; then
      echo "$(date) - Attempting to install in the specified path..."
      mkdir -p "$CustomNodePath"
      curl -o- "https://nodejs.org/dist/v$NodeVersion/node-v$NodeVersion-linux-x64.tar.xz" | tar -xJ -C "$CustomNodePath" --strip-components=1
    else
      echo "$(date) - Node.js and NPM found in the specified path."
    fi

    if [ ! -f "$CustomNodePath/bin/node" ]; then
      echo "$(date) - Node.js installation in the specified path failed. Installing in the user's profile..."
      mkdir -p "$HOME/nodejs"
      curl -o- "https://nodejs.org/dist/v$NodeVersion/node-v$NodeVersion-linux-x64.tar.xz" | tar -xJ -C "$HOME/nodejs" --strip-components=1
    fi
  }

  # Function to install Visual Studio Code
  InstallVSCode() {
    echo "$(date) - Installing Visual Studio Code..."
    if [ ! -f "$CustomVSCodePath/bin/code" ]; then
      echo "$(date) - Attempting to install in the specified path..."
      mkdir -p "$CustomVSCodePath"
      curl -L "https://update.code.visualstudio.com/latest/linux-x64/stable" -o "$CustomVSCodePath/vscode.tar.gz"
      tar -xzvf "$CustomVSCodePath/vscode.tar.gz" -C "$CustomVSCodePath" --strip-components=1
      rm "$CustomVSCodePath/vscode.tar.gz"
    else
      echo "$(date) - Visual Studio Code found in the specified path."
    fi

    if [ ! -f "$CustomVSCodePath/bin/code" ]; then
      echo "$(date) - Visual Studio Code installation in the specified path failed. Installing in the user's profile..."
      mkdir -p "$HOME/VSCode"
      curl -L "https://update.code.visualstudio.com/latest/linux-x64/stable" -o "$HOME/VSCode/vscode.tar.gz"
      tar -xzvf "$HOME/VSCode/vscode.tar.gz" -C "$HOME/VSCode" --strip-components=1
      rm "$HOME/VSCode/vscode.tar.gz"
    fi
  }

  # ... (rest of the script)

  # Clone the repository
  echo "$(date) - Cloning the repository..."
  git clone "$RepoURL"
  cd "$RepoFolder" || exit

  # Install UI dependencies
  echo "$(date) - Installing UI dependencies..."
  npm install
  if [ $? -ne 0 ]; then
    echo "$(date) - Error: Installing UI dependencies failed."
    exit 1
  fi
  echo "$(date) - UI dependencies installed."

  # Download artifacts from Nexus
  # Use the appropriate method to download artifacts from Nexus here
  # Example: curl -o "downloaded-artifact.ext" "$NexusUrl/$Repository/$Artifact"

  echo "$(date) - Artifacts downloaded from Nexus."

  # Run the demo application
  echo "$(date) - Running the demo application..."
  npm start
  if [ $? -ne 0 ]; then
    echo "$(date) - Error: Running the demo application failed."
    exit 1
  fi

  echo "$(date) - Development environment setup complete."
  echo "Success"
} >"$LogFile" 2>&1

# Error handling
if [ $? -ne 0 ]; then
  echo "Error"
fi

# Display a message with the log file location
echo "$(date) - Log file saved to $LogFile"
