import requests

# Bitbucket base URL
BITBUCKET_BASE_URL = "https://bitbucket.example.com"

# Bitbucket project key
PROJECT_KEY = "your-project-key"

# Bitbucket API credentials
USERNAME = "your-username"
PASSWORD = "your-password"

# Function to get repository branch counts
def get_repo_branch_counts():
    # Create a session for making authenticated requests
    session = requests.Session()
    session.auth = (USERNAME, PASSWORD)

    # Get the list of repositories in the project
    repo_list_url = f"{BITBUCKET_BASE_URL}/rest/api/1.0/projects/{PROJECT_KEY}/repos"
    response = session.get(repo_list_url)
    response.raise_for_status()
    repos = response.json()["values"]

    # Loop through each repository and retrieve the number of branches
    for repo in repos:
        repo_name = repo["name"]
        branches_url = f"{BITBUCKET_BASE_URL}/rest/api/1.0/projects/{PROJECT_KEY}/repos/{repo_name}/branches"
        response = session.get(branches_url)
        response.raise_for_status()
        branches = response.json()["values"]
        branch_count = len(branches)
        print(f"Repository: {repo_name}, Branch Count: {branch_count}")

if __name__ == "__main__":
    get_repo_branch_counts()
