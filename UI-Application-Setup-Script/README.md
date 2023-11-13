# Local UI Development Environment Setup

This PowerShell script automates the setup of a local UI development environment for project. It includes Node.js, NPM, Visual Studio Code, and more.

## Quick Start

1. Define variables in the script:
   - `$NodeVersion`: Desired Node.js version.
   - `$Branch`: Default Git branch.
   - `$RepoURL`: Git repository URL.

2. Run the script with administrator privileges.

3. The script will:
   - Install Node.js and NPM.
   - Install Visual Studio Code.
   - Clone Git repository.
   - Download UI dependencies.
   - Download artifacts from Nexus (customize URLs).
   - Run the demo application.

## Customization

- Modify variables at the script's beginning.
- Customize Nexus and Git settings.
- Handle installation fallbacks for permission issues.

## Prerequisites

- PowerShell (with administrator access).
- Git installed.
- Internet connection for downloads.

## License

This script is open-source and available under the MIT License.

For detailed instructions and options, see the full README.
