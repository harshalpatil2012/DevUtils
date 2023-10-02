#!/bin/bash
# To tun parallel -j 10 ./validate_repos.sh ::: $(curl -X GET https://bitbucket.org/api/2.0/projects/my-project/repos | jq -r '.values[] | .slug')

# Get the current date
CURRENT_DATE=$(date +"%Y-%m-%d")

# Get the date two years ago
TWO_YEARS_AGO=$(date --date='-2 years' +"%Y-%m-%d")

# Get the list of all active repositories in the project using the Stash API
ACTIVE_REPOS=$(curl -X GET https://bitbucket.org/api/2.0/projects/my-project/repos | jq -r '.values[] | .slug')

# Iterate over the active repositories and validate them in parallel
for REPO in $ACTIVE_REPOS; do
  (
    # Check if the repository is cloned locally
    if [[ ! -d ".git" ]]; then
      git clone "https://bitbucket.org/$REPO.git"
    fi

    # Check if the repository has changed in the last two years
    LAST_COMMIT_DATE=$(git log --since="$TWO_YEARS_AGO" -1 --oneline --master | cut -d ' ' -f 1)

    if [[ -z "$LAST_COMMIT_DATE" ]]; then
      echo "Repository $REPO has not changed in the last two years."
    fi
  ) &
done

# Wait for all jobs to finish
wait