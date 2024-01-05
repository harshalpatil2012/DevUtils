#!/bin/bash

# Set the destination directory where repositories will be cloned
DESTINATION_DIR="/path/to/destination"

# Define branches and Bitbucket URLs as static constants
branches=("master" "develop" "feature-branch")
bitbucket_urls=("https://bitbucket.org/user/repo1.git" "https://bitbucket.org/user/repo2.git" "https://bitbucket.org/user/repo3.git")

# Loop through each branch and Bitbucket URL
for ((i=0; i<${#branches[@]}; i++)); do
    branch="${branches[i]}"
    bitbucket_url="${bitbucket_urls[i]}"

    # Extract repository name from the Bitbucket URL
    repo_name=$(basename "$bitbucket_url" | sed -n 's/^repo\([0-9]\+\).git/\1/p')

    # Construct the full path to the repository
    repo_path="$DESTINATION_DIR/$repo_name"

    # Clone the repository if it doesn't exist
    if [ ! -d "$repo_path" ]; then
        git clone "$bitbucket_url" "$repo_path" || { echo "Error: Unable to clone $bitbucket_url"; exit 1; }
        echo "Cloned $bitbucket_url to $repo_path"
    fi

    # Navigate to the repository folder
    cd "$repo_path" || { echo "Error: Unable to navigate to $repo_path folder"; exit 1; }

    # Checkout the specified branch
    git checkout "$branch" || { echo "Error: Unable to checkout $branch in $repo_path"; exit 1; }

    # Print a message indicating successful checkout
    echo "Checked out $branch in $repo_path"

    # Move back to the original directory
    cd - || { echo "Error: Unable to navigate back to the original directory"; exit 1; }
done
