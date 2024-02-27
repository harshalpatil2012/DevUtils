import requests
import json
import subprocess

# Bitbucket Server API Details
stash_url = "https://your-stash-instance.com"
username = "your_stash_username"
password = "your_stash_password"

# Specific Project
project_key = "DS"

# Log File
log_file = "project_repo_data.log"

# Function to fetch branches with pagination
def fetch_branches(repo_url):
    branches = []
    start = 0
    limit = 1000  # Adjust the limit based on your Bitbucket Server instance's pagination limit

    while True:
        branches_endpoint = f"{repo_url}/branches?start={start}&limit={limit}"
        branches_response = requests.get(branches_endpoint, auth=(username, password), verify=False)

        if branches_response.status_code == 200:
            current_branches = branches_response.json().get('values', [])
            branches.extend(current_branches)

            if len(current_branches) < limit:
                break  # Reached the end of branches
            else:
                start += limit
        else:
            break  # Exit loop on error

    return branches

# Get repos for the project
repos_endpoint = f"{stash_url}/rest/api/1.0/projects/{project_key}/repos"
repos_response = requests.get(repos_endpoint, auth=(username, password), verify=False)

if repos_response.status_code == 200:
    repos = repos_response.json()['values']

    with open(log_file, 'a') as f:  # Open log file in append mode
        project_total_branches = 0

        for repo in repos:
            repo_name = repo['slug']
            f.write(f"\n--- Repository: {repo_name} ---\n")  # Section header

            # Check if the repo URL uses ssh: scheme
            if repo['links']['clone'][0]['name'] == 'ssh':
                # Use git command for SSH-based Git operations
                repo_url = repo['links']['clone'][0]['href']
                branches = subprocess.run(['git', 'ls-remote', '--heads', repo_url], capture_output=True, text=True)
                branches = branches.stdout.split('\n')
            else:
                # Get total remote branches for the repo using HTTPS-based URL
                repo_url = f"{stash_url}/scm/{project_key}/{repo_name}.git"
                branches = fetch_branches(repo_url)

            total_branches = len(branches)
            project_total_branches += total_branches

            f.write(f"Total branches: {total_branches}\n")

        # Log the total branches for the project
        f.write(f"\n--- Project Total Branches: {project_total_branches} ---\n")

else:
    print(f"Error fetching repos for project: {project_key}")
