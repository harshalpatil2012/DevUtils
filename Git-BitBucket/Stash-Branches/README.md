# Bitbucket Repository Branch Count Script

**Author:** Harshal

This Python script allows you to fetch repository information and the number of branches for each repository in a Bitbucket project using the Bitbucket REST API.

## Prerequisites

Before running the script, ensure that you have the following:

- Python installed on  computer. You can download it from [python.org](https://www.python.org/downloads/).
- The `requests` library installed. You can install it using `pip` by running:



## Usage

### 1. Clone or Download the Script

- Clone this Git repository or download the script (`bitbucket_repo_branch_counts.py`) to computer.

### 2. Edit the Script

- Open the `bitbucket_repo_branch_counts.py` script in a text editor.

### 3. Configure Script Settings

- Replace the following placeholders in the script with Bitbucket project and API credentials:
- `BITBUCKET_BASE_URL`: The base URL of Bitbucket server.
- `PROJECT_KEY`: The key of the Bitbucket project you want to retrieve repositories from.
- `USERNAME`: Bitbucket username or API token.
- `PASSWORD`: Bitbucket password or API token.

### 4. Run the Script
- pip install requests
- python -c "import requests; print(requests.__version__)"

- Open a terminal or command prompt.
- Navigate to the directory where the script is located.
- Run the script using one of the following commands:
- On Windows:
  ```
  python bitbucket_repo_branch_counts.py
  ```
- On Linux/macOS:
  ```
  python3 bitbucket_repo_branch_counts.py
  ```

### 5. View Output

- The script will execute and display the repository names and their corresponding branch counts in the terminal or command prompt.

## Note

- This script is intended for informational purposes and allows you to quickly retrieve branch counts for repositories within a Bitbucket project.
- Keep Bitbucket project and API credentials secure.
- Use this script responsibly in development environment.

Feel free to reach out if you encounter any issues or have questions about using this script.
