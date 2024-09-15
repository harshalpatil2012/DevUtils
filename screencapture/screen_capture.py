import os
import time
import base64
import logging
import requests
from PIL import ImageGrab
import pytesseract

# Configure logging
log_file = r'D:\GitHubProjects\DevUtils\screencapture\screen_capture_service.log'
logging.basicConfig(filename=log_file, level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s')

def capture_screen(filename):
    try:
        screen = ImageGrab.grab()
        screen.save(filename)
        logging.info(f"Screen captured and saved to {filename}")
    except Exception as e:
        logging.error(f"Failed to capture screen: {e}")

def image_to_text(image_path):
    try:
        img = Image.open(image_path)
        text = pytesseract.image_to_string(img)
        logging.info(f"Text extracted from {image_path}")
        return text
    except Exception as e:
        logging.error(f"Failed to convert image to text: {e}")
        return ""

def update_github_file(token, repo, path, content, message):
    try:
        url = f"https://api.github.com/repos/{repo}/contents/{path}"
        headers = {"Authorization": f"token {token}"}
        
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            file_info = response.json()
            sha = file_info['sha']
            old_content = base64.b64decode(file_info['content']).decode()
        else:
            sha = None
            old_content = ""
        
        new_content = old_content + content
        encoded_content = base64.b64encode(new_content.encode()).decode()
        
        data = {
            "message": message,
            "content": encoded_content,
            "sha": sha
        }
        
        response = requests.put(url, headers=headers, json=data)
        if response.status_code == 200:
            logging.info(f"GitHub file updated successfully: {path}")
        else:
            logging.error(f"Failed to update GitHub file: {response.status_code} {response.json()}")
    except Exception as e:
        logging.error(f"Error updating GitHub file: {e}")

def main():
    github_token = "YOUR_GITHUB_TOKEN"
    repo_name = "YOUR_GITHUB_REPO"
    file_path = "path/to/your/file.txt"
    screen_image_path = r'D:\GitHubProjects\DevUtils\screencapture\screenshot.png'

    while True:
        capture_screen(screen_image_path)
        text = image_to_text(screen_image_path)
        if text:
            update_github_file(github_token, repo_name, file_path, text, "Updating file with new text from screenshot")
        time.sleep(20)

if __name__ == "__main__":
    main()
