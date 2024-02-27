import requests
import json
import os

# Stash API Details
stash_url = "https://your-stash-instance.com/rest/api/1.0"
username = "your_stash_username"
password = "your_stash_password"

# Specific Project
project_key = "DS"

# Log File
log_file = "project_repo_data.log"

# Get repos for the project
repos_endpoint = f"{stash_url}/projects/{project_key}/repos"
repos_response = requests.get(repos_endpoint, auth=(username, password), verify=False)

if repos_response.status_code == 200:
    repos = repos_response.json()['values']

    with open(log_file, 'a') as f:  # Open log file in append mode
        for repo in repos:
            repo_name = repo['slug']
            f.write(f"\n--- Repository: {repo_name} ---\n")  # Section header

            # Get branches (using Git command in the repo directory)
            os.chdir('C:\\repo')
            branches_output = os.popen(f'git -C {repo_name} branch').read()
            total_branches = len([line for line in branches_output.split('\n') if line.strip() != ''])

            f.write(f"Total branches: {total_branches}\n")

else:
    print(f"Error fetching repos for project: {project_key}")
