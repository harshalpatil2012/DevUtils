import os
import subprocess
import datetime
import logging

# Configure logging
logging.basicConfig(filename='branch_analysis.log', level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s')

def get_remote_branches(repo_path):
    """Fetches all remote branches for a git repository."""
    try:
        result = subprocess.run(
            ['git', 'branch', '-r'],
            cwd=repo_path,
            check=True,
            stdout=subprocess.PIPE,
            universal_newlines=True
        )
        return [branch.strip() for branch in result.stdout.splitlines()]
    except subprocess.CalledProcessError as e:
        logging.error(f"Error fetching branches for {repo_path}: {e}")
        return []

def get_branches_no_commits(repo_path, remote_branches):
    """Identifies branches with no commits."""
    try:
        branches_no_commits = []
        for branch in remote_branches:
            result = subprocess.run(
                ['git', 'rev-list', '--count', branch],
                cwd=repo_path,
                check=True,
                stdout=subprocess.PIPE,
                universal_newlines=True
            )
            if int(result.stdout.strip()) == 0:
                branches_no_commits.append(branch)
        return branches_no_commits
    except subprocess.CalledProcessError as e:
        logging.error(f"Error analyzing branches for {repo_path}: {e}")
        return []

def filter_old_branches(branches, days_threshold):
    """Filters branches older than a specified number of days."""
    today = datetime.date.today()
    threshold_date = today - datetime.timedelta(days=days_threshold)
    old_branches = []
    for branch in branches:
        result = subprocess.run(
            ['git', 'log', '-1', '--format=%ad', '--date=iso', branch],
            cwd=repo_path,
            check=True,
            stdout=subprocess.PIPE,
            universal_newlines=True
        )
        last_commit_date_str = result.stdout.strip()
        if last_commit_date_str:
            last_commit_date = datetime.datetime.strptime(last_commit_date_str, '%Y-%m-%d').date()
            if last_commit_date <= threshold_date:
                old_branches.append(branch)
    return old_branches

def find_merged_feature_branches(repo_path):
    """Finds feature branches merged into release branches (assuming naming conventions)."""
    merged_feature_branches = []
    for branch in get_remote_branches(repo_path):
        if branch.startswith('feature/') and branch in subprocess.check_output(
                ['git', 'branch', '--merged', 'release/'],
                cwd=repo_path,
                universal_newlines=True
        ).decode():
            merged_feature_branches.append(branch)
    return merged_feature_branches

# --- Main Script Logic ---
codebase_folder = "your/codebase/folder"  # Replace with your actual path
output_folder = "C:\\output_logs"         # Replace with your desired Windows output folder

# ... (Rest of the script from previous answer)
