import requests
import json
import subprocess
import logging

# Set up logging
logging.basicConfig(filename='project_repo_data.log', level=logging.INFO, format='%(asctime)s - %(levelname)s: %(message)s')

# Bitbucket Server API Details
stash_url = "https://your-stash-instance.com"
username = "your_stash_username"
password = "your_stash_password"

# Specific Project
project_key = "DS"

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
            logging.error(f"Error fetching branches for repo: {repo_url}")
            break  # Exit loop on error

    return branches

# Get repos for the project
repos_endpoint = f"{stash_url}/rest/api/1.0/projects/{project_key}/repos"
repos_response = requests.get(repos_endpoint, auth=(username, password), verify=False)

if repos_response.status_code == 200:
    repos = repos_response.json()['values']

    for repo in repos:
        repo_name = repo['slug']
        logging.info(f"\n--- Repository: {repo_name} ---")  # Log to file
        print(f"\n--- Repository: {repo_name} ---")  # Print to console

        # Check if the repo URL uses ssh: scheme
        if repo['links']['clone'][0]['name'] == 'ssh':
            # Use git command for SSH-based Git operations
            repo_url = repo['links']['clone'][0]['href']
            try:
                branches_process = subprocess.Popen(['git', 'ls-remote', '--heads', repo_url], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
                branches_output, branches_error = branches_process.communicate()
                if branches_process.returncode == 0:
                    branches = branches_output.split('\n')
                    print(f"Git Process Output:\n{branches_output}")
                else:
                    logging.error(f"Git command failed for repo {repo_url}. Error: {branches_error}")
                    print(f"Git command failed for repo {repo_url}. Error: {branches_error}")
                    continue
            except Exception as e:
                logging.error(f"Error executing git command for repo {repo_url}: {str(e)}")
                print(f"Error executing git command for repo {repo_url}: {str(e)}")
                continue
        else:
            # Get total remote branches for the repo using HTTPS-based URL
            repo_url = f"{stash_url}/scm/{project_key}/{repo_name}.git"
            try:
                branches = fetch_branches(repo_url)
            except requests.RequestException as e:
                logging.error(f"Error fetching branches for repo {repo_url}: {str(e)}")
                print(f"Error fetching branches for repo {repo_url}: {str(e)}")
                continue

        total_branches = len(branches)
        logging.info(f"Total branches: {total_branches}")  # Log to file
        print(f"Total branches: {total_branches}")  # Print to console

    # Log the total branches for the project
    logging.info(f"\n--- Project Total Branches: {total_branches} ---")
    print(f"\n--- Project Total Branches: {total_branches} ---")

else:
    logging.error(f"Error fetching repos for project: {project_key}")
    print(f"Error fetching repos for project: {project_key}")
