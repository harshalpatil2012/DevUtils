import requests
from requests.auth import HTTPBasicAuth
from datetime import datetime, timedelta

# Function to get all child pages for a given space
def get_all_child_pages(confluence_url, username, password, space_key):
    # Set the Confluence API endpoint for the space
    api_endpoint = f"{confluence_url}/wiki/rest/api/content/{space_key}"

    # Get current date and calculate date 4 years ago
    current_date = datetime.now()
    four_years_ago = current_date - timedelta(days=1461)  # Considering leap years

    # Initialize an empty list to store all child pages
    all_child_pages = []

    # Set the initial query parameters
    params = {
        'expand': 'children.page',
        'limit': 100  # You may adjust the limit based on your needs
    }

    while True:
        # Make the request to Confluence API
        response = requests.get(api_endpoint, params=params, auth=HTTPBasicAuth(username, password))
        response.raise_for_status()

        # Process the response and filter old child pages
        pages = response.json().get('children', {}).get('page', [])
        for page in pages:
            last_updated = datetime.strptime(page['history']['lastUpdated']['when'], "%Y-%m-%dT%H:%M:%S.%f%z")
            if last_updated < four_years_ago:
                all_child_pages.append(page['title'])

        # Check for pagination
        if 'next' in response.json().get('_links', {}):
            api_endpoint = response.json()['_links']['next']
        else:
            break

    return all_child_pages

# Function to write the list of old child pages into a text file
def write_to_file(file_path, child_pages):
    with open(file_path, 'w') as file:
        for page in child_pages:
            file.write(f"{page}\n")

# Example usage
if __name__ == "__main__":
    confluence_url = "YOUR_CONFLUENCE_URL"
    username = "YOUR_USERNAME"
    password = "YOUR_PASSWORD"
    space_key = "YOUR_SPACE_KEY"
    output_file_path = "old_child_pages.txt"

    child_pages = get_all_child_pages(confluence_url, username, password, space_key)

    if child_pages:
        write_to_file(output_file_path, child_pages)
        print(f"List of old child pages written to {output_file_path}")
    else:
        print("No old child pages found.")
