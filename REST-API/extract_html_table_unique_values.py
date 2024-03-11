from bs4 import BeautifulSoup
import os
import re

def get_key_value_from_html(html_file):
    with open(html_file, 'r', encoding='utf-8') as file:
        soup = BeautifulSoup(file, 'html.parser')
        span_elements = soup.find_all('span', attrs={'ng-bind-html': True})

        key_values = []
        for span in span_elements:
            match = re.search(r'ng-bind-html=["\'](.*?)["\'].*class="ng-binding">(.*?)<\/span>', str(span))
            if match:
                key = match.group(1)
                value = match.group(2)
                key_values.append((key, value))

    return key_values

def process_folder(folder_path):
    unique_key_values = set()

    for filename in os.listdir(folder_path):
        if filename.endswith('.html'):
            file_path = os.path.join(folder_path, filename)
            unique_key_values.update(get_key_value_from_html(file_path))

    return unique_key_values

if __name__ == "__main__":
    folder_path = "c:/logs"
    key_values = process_folder(folder_path)

    print("Consolidated Unique Key-Value Pairs:")
    for key, value in key_values:
        print(f"Key: {key}\tValue: {value}")
