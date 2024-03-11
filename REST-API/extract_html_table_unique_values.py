from bs4 import BeautifulSoup
import os

def get_unique_strings_from_html(html_file):
    with open(html_file, 'r', encoding='utf-8') as file:
        soup = BeautifulSoup(file, 'html.parser')
        tr_elements = soup.find_all('tr')
        strings = [tr.get_text(strip=True) for tr in tr_elements]

    return set(strings)

def process_folder(folder_path):
    unique_strings = set()

    for filename in os.listdir(folder_path):
        if filename.endswith('.html'):
            file_path = os.path.join(folder_path, filename)
            unique_strings.update(get_unique_strings_from_html(file_path))

    return list(unique_strings)

if __name__ == "__main__":
    folder_path = "/path/to/your/html/files"
    unique_strings = process_folder(folder_path)

    print("Consolidated Unique Strings:")
    for string in unique_strings:
        print(string)

pip install beautifulsoup4
