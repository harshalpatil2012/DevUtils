import os
import datetime
import logging
from git import Repo

LOG_FOLDER = 'C:\\logs'
CODEBASE_PATH = 'C:\\codebase'

def get_repo_branches_with_no_commits(repo_path):
    repo = Repo(repo_path)
    branches_with_no_commits = []

    for branch in repo.remote().refs:
        if branch.name != 'origin/master' and not branch.commit:
            branches_with_no_commits.append(branch.name)

    return branches_with_no_commits

def get_repo_merged_feature_branches(repo_path):
    repo = Repo(repo_path)
    merged_feature_branches = []

    for release_branch in repo.branches:
        if 'release/' in release_branch.name:
            for commit in repo.iter_commits(release_branch):
                for parent in commit.parents:
                    for feature_branch in repo.branches:
                        if 'feature/' in feature_branch.name and feature_branch.commit == parent:
                            merged_feature_branches.append(feature_branch.name)

    return merged_feature_branches

def write_to_file(file_path, data):
    with open(file_path, 'w') as file:
        file.write('\n'.join(data))

def update_repository(repo_path):
    repo = Repo(repo_path)
    try:
        repo.remotes.origin.pull()  # Git pull operation
    except Exception as e:
        logging.error(f"Error updating Git repo at {repo_path}: {e}")

def main(base_folder):
    log_file = os.path.join(LOG_FOLDER, 'script_log.log')  # Update the log file path
    logging.basicConfig(filename=log_file, level=logging.ERROR, format='%(asctime)s - %(levelname)s: %(message)s')

    for repo_folder in os.listdir(base_folder):
        repo_path = os.path.join(base_folder, repo_folder)

        if os.path.isdir(repo_path):
            try:
                update_repository(repo_path)  # Update the repository before analysis

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

if __name__ == "__main__":
    base_folder = os.path.join(CODEBASE_PATH, input("Enter the relative base folder path: "))
    main(base_folder)
