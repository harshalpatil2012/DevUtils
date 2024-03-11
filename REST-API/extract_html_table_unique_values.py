from bs4 import BeautifulSoup
import os
import re

def get_unique_values_from_html(html_file, attribute_key):
    with open(html_file, 'r', encoding='utf-8') as file:
        soup = BeautifulSoup(file, 'html.parser')
        span_elements = soup.find_all('span', attrs={'ng-bind-html': re.compile('.*' + attribute_key + '.*')})
        
        values = []
        for span in span_elements:
            match = re.search(r'ng-bind-html=["\'](.*?)["\']', str(span))
            if match:
                values.append(match.group(1))

    return set(values)

def process_folder(folder_path, attribute_key):
    unique_values = set()

    for filename in os.listdir(folder_path):
        if filename.endswith('.html'):
            file_path = os.path.join(folder_path, filename)
            unique_values.update(get_unique_values_from_html(file_path, attribute_key))

    return list(unique_values)

if __name__ == "__main__":
    folder_path = "c:/logs"
    attribute_key = 'selectedRequest.url'
    unique_values = process_folder(folder_path, attribute_key)

    print(f"Consolidated Unique Values for {attribute_key}:")
    for value in unique_values:
        print(value)
