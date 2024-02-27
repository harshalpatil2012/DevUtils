import requests
import os

def get_bitbucket_repositories(project_url, project_key):
    api_url = f"{project_url.rstrip('/')}/rest/api/1.0/projects/{project_key}/repos"
    response = requests.get(api_url)
    
    if response.status_code == 200:
        return response.json()["values"]
    else:
        print(f"Failed to fetch repositories. Status Code: {response.status_code}, Error: {response.text}")
        return None

def write_log(repository_slug, branches_without_commits):
    log_file_path = f"{repository_slug}_branches_without_commits.log"
    with open(log_file_path, 'w') as log_file:
        log_file.write("Branches without commits:\n")
        for branch in branches_without_commits:
            log_file.write(branch + "\n")
    print(f"Log file created for repository {repository_slug}: {log_file_path}")

def get_branches_without_commits(project_url, project_key, repository_slug):
    branches = get_bitbucket_branches(project_url, project_key, repository_slug)

    if branches:
        branches_without_commits = []

        for branch in branches:
            branch_name = branch["displayId"]
            commits = get_branch_commits(project_url, project_key, repository_slug, branch_name)

            if not commits:
                branches_without_commits.append(branch_name)

        return branches_without_commits

def get_bitbucket_branches(project_url, project_key, repository_slug):
    api_url = f"{project_url.rstrip('/')}/rest/api/1.0/projects/{project_key}/repos/{repository_slug}/branches"
    response = requests.get(api_url)
    
    if response.status_code == 200:
        return response.json()["values"]
    else:
        print(f"Failed to fetch branches. Status Code: {response.status_code}, Error: {response.text}")
        return None

def get_branch_commits(project_url, project_key, repository_slug, branch_name):
    api_url = f"{project_url.rstrip('/')}/rest/api/1.0/projects/{project_key}/repos/{repository_slug}/commits"
    params = {'until': branch_name, 'limit': 1}
    response = requests.get(api_url, params=params)
    
    if response.status_code == 200:
        return response.json()["values"]
    else:
        print(f"Failed to fetch commits. Status Code: {response.status_code}, Error: {response.text}")
        return None

if __name__ == "__main__":
    project_url = "https://bitbucket.org/your_username/your_project"
    project_key = "YOUR_PROJECT_KEY"

    repositories = get_bitbucket_repositories(project_url, project_key)

    if repositories:
        for repo in repositories:
            repository_slug = repo["slug"]
            result = get_branches_without_commits(project_url, project_key, repository_slug)

            if result:
                write_log(repository_slug, result)
            else:
                print(f"Failed to fetch branch information for repository {repository_slug}")
    else:
        print("Failed to fetch repository information.")
