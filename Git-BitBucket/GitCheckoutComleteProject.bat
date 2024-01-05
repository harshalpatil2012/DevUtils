#!/bin/bash

# Set the destination directory where repositories will be cloned
DESTINATION_DIR="/path/to/destination"

# Define a map for repositories and branches
declare -A repo_branch_map=(
    ["repo1"]="master"
    ["repo2"]="develop"
    ["repo3"]="feature-branch"
)

# Define Bitbucket URLs
bitbucket_urls=("https://bitbucket.org/user/repo1.git" "https://bitbucket.org/user/repo2.git" "https://bitbucket.org/user/repo3.git")

IFS=',' read -ra url_array <<< "$bitbucket_urls"

# Loop through each repository in the map
for repo in "${!repo_branch_map[@]}"; do
    branch="${repo_branch_map[$repo]}"
    bitbucket_url="${url_array[i]}"

    # Extract repository name from the Bitbucket URL
    repo_name=$(basename "$bitbucket_url" | sed 's/\.git$//')

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
