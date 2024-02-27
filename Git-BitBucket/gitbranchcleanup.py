import os
import datetime
import logging
from git import Repo
import time

# *** Configuration ***
LOG_FOLDER = 'C:\\logs'  # Customize if needed
CODEBASE_PATH = 'C:\\codebase'  # Update with the path to the folder containing your Git repositories

def get_repo_branches_with_no_commits(repo_path):
    repo = Repo(repo_path)
    branches_with_no_commits = []

    for branch in repo.remote().refs: 
        if branch.name != 'origin/master':  # Exclude 'origin/master'
            origin_branch = branch.remote_head  

            try:
                if not repo.is_ancestor(branch.commit, origin_branch):
                    branches_with_no_commits.append(branch.name)
            except git.exc.GitCommandError as e:
                logging.error(f"Error in is_ancestor for branch: {branch.name}, origin: {origin_branch}, error: {e}")

    return branches_with_no_commits

def get_repo_merged_feature_branches(repo_path):
    repo = Repo(repo_path)
    merged_feature_branches = []

    for release_branch in repo.branches:
        if 'release/' in release_branch.name:
            for commit in repo.iter_commits(release_branch):
                for feature_branch in repo.branches:
                    if 'feature/' in feature_branch.name and commit in feature_branch.iter_commits():
                        merged_feature_branches.append(feature_branch.name)
                        break  

    return merged_feature_branches

def write_to_file(file_path, data):
    with open(file_path, 'w') as file:
        file.write('\n'.join(data))

def update_repository(repo_path):
    repo = Repo(repo_path)
    try:
        print(f"Updating repository: {os.path.basename(repo_path)}")
        repo.remotes.origin.fetch()  
        time.sleep(1)  
        repo.remotes.origin.pull()  
        print(f"Update for repository {os.path.basename(repo_path)} complete.")
    except git.exc.GitCommandError as e:
        if 'cannot lock ref' in str(e):
            logging.warning(f"Cannot lock ref in Git repo at {repo_path}. Skipping update for now.")
        else:
            logging.error(f"Error updating Git repo at {repo_path}: {e}")
            print(f"Error updating Git repo at {repo_path}: {e}")

def main():
    log_file = r'C:\logs\script_log.log'
    logging.basicConfig(filename=log_file, level=logging.ERROR, format='%(asctime)s - %(levelname)s: %(message)s')

    for repo_folder in os.listdir(CODEBASE_PATH):
        repo_path = os.path.join(CODEBASE_PATH, repo_folder)

        if os.path.isdir(repo_path):
            try:
                print(f"Processing repository: {repo_folder}")
                update_repository(repo_path)

                repo = Repo(repo_path)
            except Exception as e:
                logging.error(f"Error accessing Git repo at {repo_path}: {e}")
                print(f"Error accessing Git repo at {repo_path}: {e}")
                continue

            branches_with_no_commits = get_repo_branches_with_no_commits(repo_path)
            print(f"Branches with no commits in {repo_folder}: {branches_with_no_commits}")

            # Age calculation for branches with no commits
            old_branches_no_commits = [branch for branch in branches_with_no_commits if
                                       (datetime.datetime.now(datetime.timezone.utc) - repo.branches[branch].commit.committed_datetime).days > 60]

            merged_feature_branches = get_repo_merged_feature_branches(repo_path)
            print(f"Merged feature branches in {repo_folder}: {merged_feature_branches}")

            # ... (rest of your code) 

if __name__ == "__main__":
    main()
