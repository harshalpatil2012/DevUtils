#!/bin/bash

#Checkout all repos with masrter branch
# Set your Stash/Bitbucket Server URL and project key
STASH_URL="https://your-stash-url.com"
PROJECT_KEY="your-project-key"

# Set the destination directory where repositories will be cloned
DESTINATION_DIR="/path/to/destination"

# Loop through repositories in the specified project
curl -s $STASH_URL/rest/api/1.0/projects/$PROJECT_KEY/repos?limit=1000 | \
    jq -r '.values[] | .links.clone[] | select(.name == "ssh") | .href' | \
    while read -r REPO_URL; do
        # Extract repository name from the URL
        REPO_NAME=$(basename "$REPO_URL" | sed 's/\.git$//')

        # Clone the repository using SSH key authentication
        git clone $REPO_URL $DESTINATION_DIR/$REPO_NAME

        # Change into the cloned repository directory
        cd $DESTINATION_DIR/$REPO_NAME

        # Checkout the master branch
        git checkout master

        # Go back to the original directory
        cd -
    done

echo "Cloning and checking out master branch complete."


#Checkout individual repo with given branches
# Define repositories and branches as static constants
repos=("repo1" "repo2" "repo3")
branches=("master" "develop" "feature-branch")

# Loop through each repository and branch
for ((i=0; i<${#repos[@]}; i++)); do
    repo="${repos[i]}"
    branch="${branches[i]}"

    # Construct the full path to the repository
    repo_path="$DESTINATION_DIR/$repo"

    # Navigate to the repository folder
    cd "$repo_path" || { echo "Error: Unable to navigate to $repo_path folder"; exit 1; }

    # Checkout the specified branch
    git checkout "$branch" || { echo "Error: Unable to checkout $branch in $repo_path"; exit 1; }

    # Print a message indicating successful checkout
    echo "Checked out $branch in $repo_path"

    # Move back to the original directory
    cd - || { echo "Error: Unable to navigate back to the original directory"; exit 1; }
done
