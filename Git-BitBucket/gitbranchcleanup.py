import os
import datetime
import logging
from git import Repo
import time

LOG_FOLDER = 'C:\\logs'
CODEBASE_PATH = 'C:\\codebase'

def get_repo_branches_with_no_commits(repo_path):
    repo = Repo(repo_path)
    branches_with_no_commits = []

    for branch in repo.remote().refs:
        try:
            if branch.name != 'origin/master' and not branch.commit:
                branches_with_no_commits.append(branch.name)
        except Exception as e:
            logging.error(f"Error checking branch {branch.name} in repo at {repo_path}: {e}")

    return branches_with_no_commits

def get_repo_merged_feature_branches(repo_path):
    repo = Repo(repo_path)
    merged_feature_branches = []

    for release_branch in repo.branches:
        if 'release/' in release_branch.name:
            for commit in repo.iter_commits(release_branch):
                for parent in commit.parents:
                    for feature_branch in repo.branches:
                        try:
                            if 'feature/' in feature_branch.name and feature_branch.commit == parent:
                                merged_feature_branches.append(feature_branch.name)
                        except Exception as e:
                            logging.error(f"Error checking feature branch {feature_branch.name} in repo at {repo_path}: {e}")

    return merged_feature_branches

def write_to_file(file_path, data):
    with open(file_path, 'w') as file:
        file.write('\n'.join(data))

def update_repository(repo_path):
    repo = Repo(repo_path)
    try:
        print(f"Updating repository: {os.path.basename(repo_path)}")
        repo.remotes.origin.fetch()  # Git fetch operation to get updated remote branches
        time.sleep(1)  # Introduce a delay to avoid potential conflicts
        repo.remotes.origin.pull()   # Git pull operation
        print(f"Update for repository {os.path.basename(repo_path)} complete.")
    except git.exc.GitCommandError as e:
        if 'cannot lock ref' in str(e):
            logging.warning(f"Cannot lock ref in Git repo at {repo_path}. Skipping update for now.")
        else:
            logging.error(f"Error updating Git repo at {repo_path}: {e}")

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
                continue

            branches_with_no_commits = get_repo_branches_with_no_commits(repo_path)
            old_branches_no_commits = [branch for branch in branches_with_no_commits if
                                       (datetime.datetime.now() - repo.branches[branch].commit.committed_datetime).days > 60]

            merged_feature_branches = get_repo_merged_feature_branches(repo_path)
            old_merged_feature_branches = [branch for branch in merged_feature_branches if
                                           (datetime.datetime.now() - repo.branches[branch].commit.committed_datetime).days > 365]

            # Writing logs to files
            write_to_file(os.path.join(LOG_FOLDER, f'{repo_folder}_branchwithnocommit.logs'), old_branches_no_commits)
            write_to_file(os.path.join(LOG_FOLDER, f'{repo_folder}_mergedfeaturebranches.logs'), old_merged_feature_branches)

            print(f"Processing for repository {repo_folder} complete.")
            print("--------------------------------------------------")

if __name__ == "__main__":
    main()
