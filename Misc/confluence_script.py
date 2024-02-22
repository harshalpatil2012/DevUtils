import requests
from requests.auth import HTTPBasicAuth
from datetime import datetime, timedelta

# Function to get the list of Confluence pages not updated in the last 4 years
def get_old_pages(confluence_url, username, password, project_name):
    # Set the Confluence API endpoint
    api_endpoint = f"{confluence_url}/wiki/rest/api/content"

    # Get current date and calculate date 4 years ago
    current_date = datetime.now()
    four_years_ago = current_date - timedelta(days=1461)  # Considering leap years

    # Set the query parameters to filter pages by the last update date
    params = {
        'spaceKey': project_name,
        'expand': 'history',
        'limit': 1000  # You may adjust the limit based on your needs
    }

    # Make the request to Confluence API
    response = requests.get(api_endpoint, params=params, auth=HTTPBasicAuth(username, password))
    response.raise_for_status()

    # Process the response and filter old pages
    old_pages = []
    pages = response.json().get('results', [])
    for page in pages:
        last_updated = datetime.strptime(page['history']['lastUpdated']['when'], "%Y-%m-%dT%H:%M:%S.%f%z")
        if last_updated < four_years_ago:
            old_pages.append(page['title'])

    return old_pages

# Function to write the list of old pages into a text file
def write_to_file(file_path, old_pages):
    with open(file_path, 'w') as file:
        for page in old_pages:
            file.write(f"{page}\n")

# Example usage
if __name__ == "__main__":
    confluence_url = "YOUR_CONFLUENCE_URL"
    username = "YOUR_USERNAME"
    password = "YOUR_PASSWORD"
    project_name = "YOUR_PROJECT_NAME"
    output_file_path = "old_pages.txt"

    old_pages = get_old_pages(confluence_url, username, password, project_name)

    if old_pages:
        write_to_file(output_file_path, old_pages)
        print(f"List of old pages written to {output_file_path}")
    else:
        print("No old pages found.")
