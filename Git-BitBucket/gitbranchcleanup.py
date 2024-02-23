import os
import subprocess
import datetime
import logging

# Settings
root_folder = os.path.join(".", "path", "to", "your", "root", "folder")  
log_dir = os.path.join(".", "logs")
cutoff_days_no_commit = 90
cutoff_days_merged = 365

# Create log directory if it doesn't exist
os.makedirs(log_dir, exist_ok=True)

# Configure logging
logging.basicConfig(level=logging.DEBUG)

def is_git_repo(repo_dir):
    """Checks if a folder is a Git repository"""
    return os.path.exists(os.path.join(repo_dir, ".git"))

def find_branches_no_commit(repo_dir):
    """Finds branches with no commits other than the initial commit"""
    repo_name = os.path.basename(repo_dir)

    try:
        output = subprocess.check_output(
            ["git", "for-each-ref", "--format=%(refname:short) %(creatordate:relative)", "refs/heads/"],
            cwd=repo_dir
        )
        lines = output.decode().splitlines()
    except subprocess.CalledProcessError:
        return  # Error running Git commands

    for line in lines:
        logging.debug(f"Processing line: {line}")  # Log the raw line
        try:
            branch_name, creation_date = line.split(" ")
            days_old = int(creation_date.split(" ")[0])

            if days_old > cutoff_days_no_commit:
                count_output = subprocess.check_output(
                    ["git", "rev-list", "-1", "--count", branch_name], cwd=repo_dir
                )
                if count_output.strip() == b'1':  # Only one commit (the initial)
                    with open(os.path.join(log_dir, f"{repo_name}_branchwithnocommit.log"), "a") as f:
                        f.write(branch_name + "\n")

        except ValueError:
            logging.warning(f"Error splitting line: {line}. Skipping...")

def find_merged_branches(repo_dir):
    """Finds merged feature branches"""
    # ... (Implementation remains the same)

# Main Execution
for dirpath, dirnames, filenames in os.walk(root_folder):
    if is_git_repo(dirpath):
        find_branches_no_commit(dirpath)
        find_merged_branches(dirpath)
