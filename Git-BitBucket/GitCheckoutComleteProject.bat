#!/bin/bash

# Set the destination directory where repositories will be cloned
DESTINATION_DIR="/path/to/destination"

# Define Bitbucket URLs and associated branches using a map
declare -A bitbucket_url_branch_map=(
    ["https://bitbucket.org/user/repo1.git"]="master" \
    ["https://bitbucket.org/user/repo2.git"]="develop" \
    ["https://bitbucket.org/user/repo3.git"]="feature-branch"
)

# Loop through each Bitbucket URL in the map
for bitbucket_url in "${!bitbucket_url_branch_map[@]}"; do
    branch="${bitbucket_url_branch_map[$bitbucket_url]}"

    # Extract repository name from the Bitbucket URL
    repo_name=$(basename "$bitbucket_url" | sed 's/\.git$//')

    # Construct the full path to the repository
    repo_path="$DESTINATION_DIR/$repo_name"

    # Clone the repository or pull the latest changes if it already exists
    if [ -d "$repo_path" ]; then
        cd "$repo_path" || { echo "Error: Unable to navigate to $repo_path folder"; exit 1; }
        git pull origin "$branch" || { echo "Error: Unable to pull latest changes in $repo_path"; exit 1; }
    else
        git clone --branch "$branch" --depth 1 "$bitbucket_url" "$repo_path" || { echo "Error: Unable to clone $bitbucket_url"; exit 1; }
        echo "Cloned $bitbucket_url to $repo_path"
    fi

    # Print a message indicating successful checkout or pull
    echo "Checked out/pulled $branch in $repo_path"
done


#Windows:

@echo off

set DESTINATION_DIR=C:\path\to\destination  :: Replace with your desired destination

set bitbucket_url_branch_map[0]=https://bitbucket.org/user/repo1.git master
set bitbucket_url_branch_map[1]=https://bitbucket.org/user/repo2.git develop
set bitbucket_url_branch_map[2]=https://bitbucket.org/user/repo3.git feature-branch

for /F "tokens=1,2 delims==" %%a in ('set bitbucket_url_branch_map[') do (
    set "bitbucket_url=%%a"
    set "branch=%%b"

    for %%p in ("%bitbucket_url%") do set "repo_name=%%~np"
    set "repo_path=%DESTINATION_DIR%\%repo_name%"

    if exist "%repo_path%" (
        cd "%repo_path%" || ( echo Error: Unable to navigate to %repo_path% folder & exit /B 1 )
        git pull origin "%branch%" || ( echo Error: Unable to pull latest changes in %repo_path% & exit /B 1 )
    ) else (
        git clone --branch "%branch%" --depth 1 "%bitbucket_url%" "%repo_path%" || ( echo Error: Unable to clone %bitbucket_url% & exit /B 1 )
        echo Cloned %bitbucket_url% to %repo_path%
    )

    echo Checked out/pulled %branch% in %repo_path%

    cd .. || ( echo Error: Unable to navigate back to the original directory & exit /B 1 )
)
