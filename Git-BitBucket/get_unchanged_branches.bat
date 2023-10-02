#!/bin/bash

# Get the current date
CURRENT_DATE=$(date +"%Y-%m-%d")

# Get the date two years ago
TWO_YEARS_AGO=$(date --date='-2 years' +"%Y-%m-%d")

# Get the list of all branches in the repository
BRANCHES=$(git branch -r)

# Iterate over the branches and check if they have changed in the last two years
for BRANCH in $BRANCHES; do
  LAST_COMMIT_DATE=$(git log --since="$TWO_YEARS_AGO" -1 --oneline -- $BRANCH | cut -d ' ' -f 1)

  if [[ -z "$LAST_COMMIT_DATE" ]]; then
    echo "$BRANCH"
  fi
done