import time
import os
import pytesseract
from PIL import Image
import cv2
import pyautogui
import subprocess
import logging
from logging.handlers import RotatingFileHandler
import numpy as np
from datetime import datetime

# Paths
TESSERACT_PATH = r"C:/Program Files/Tesseract-OCR/tesseract.exe"
LOG_PATH = r"D:/GitHubProjects/DevUtils/screencapture/capture.log"
APP_LOG_PATH = r"D:/GitHubProjects/DevUtils/screencapture/app.log"
BASE_IMG_PATH = r"D:/GitHubProjects/DevUtils/screencapture"
REPO_PATH = r"D:/GitHubProjects/DevUtils"

# Tesseract setup
pytesseract.pytesseract.tesseract_cmd = TESSERACT_PATH

# Logging setup
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("ScreenCaptureLogger")
handler = RotatingFileHandler(APP_LOG_PATH, maxBytes=5000000, backupCount=5)
logger.addHandler(handler)

# Caching for OCR results
previous_text = ""

# Optimized image preprocessing
def preprocess_image(image):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    thresh = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)[1]
    kernel = np.ones((1, 1), np.uint8)
    opening = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, kernel, iterations=1)
    sharpened = cv2.filter2D(opening, -1, np.array([[-1, -1, -1], [-1, 9, -1], [-1, -1, -1]]))
    return sharpened 

# Capture screenshot, process and perform OCR
def capture_and_ocr():
    start_time = time.time()
    screenshot = pyautogui.screenshot()
    img = np.array(screenshot)
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)  # Convert to OpenCV format
    preprocessed_img = preprocess_image(img)
    
    logger.info("Starting OCR...")
    custom_config = r'--oem 3 --psm 6'
    text = pytesseract.image_to_string(preprocessed_img, config=custom_config)
    
    end_time = time.time()
    logger.info(f"OCR completed in {end_time - start_time:.2f} seconds")
    
    return text, img

# Create a unique file name for each screenshot and ensure it is saved in the Git folder
def create_unique_filename():
    now = datetime.now().strftime("%d-%m-%Y_%H-%M-%S")
    folder_path = os.path.join(BASE_IMG_PATH, datetime.now().strftime("%d-%m-%Y"))
    if not os.path.exists(folder_path):
        os.makedirs(folder_path)  # Create the folder if it doesn't exist
    screenshot_name = f"screen_capture_{now}.png"
    return os.path.join(folder_path, screenshot_name)

# Save screenshot to the file
def save_screenshot(img):
    screenshot_path = create_unique_filename()
    cv2.imwrite(screenshot_path, img, [cv2.IMWRITE_PNG_COMPRESSION, 0])
    return screenshot_path

# Close log handlers
def close_log_handlers():
    for handler in logger.handlers:
        handler.close()
    logger.handlers = []

# Reopen log handler
def reopen_log_handler():
    handler = RotatingFileHandler(APP_LOG_PATH, maxBytes=5000000, backupCount=5)
    logger.addHandler(handler)

# Upload OCR text and screenshot to GitHub with forced pull and push
def upload_to_git(text, screenshot_path):
    try:
        os.chdir(REPO_PATH)

        # Append OCR text to log file
        with open(LOG_PATH, 'a', encoding='utf-8') as f:
            f.write("\n----------------- SEPARATOR -----------------\n")
            f.write(text)

        # Stage changes (both log and screenshot)
        subprocess.call(["git", "add", LOG_PATH])
        subprocess.call(["git", "add", screenshot_path])
        
        # Commit changes
        subprocess.call(["git", "commit", "-m", "Updated screen capture log and screenshot", "--quiet"])

        # Close log handlers before performing Git operations
        close_log_handlers()

        # Force a hard reset to match the remote branch before pulling
        subprocess.call(["git", "fetch", "--quiet"])
        subprocess.call(["git", "reset", "--hard", "origin/main"])  # Hard reset to remote branch

        # Now, pull the latest changes (after the hard reset)
        subprocess.call(["git", "pull", "--rebase", "--quiet"])

        # Push changes with force to overwrite remote changes
        GIT_TOKEN = os.getenv("GITHUB_TOKEN")  # Fetch Git token from environment variable
        if not GIT_TOKEN:
            raise ValueError("GitHub token is not set in environment variables.")
        
        remote_url = subprocess.check_output(["git", "config", "--get", "remote.origin.url"]).decode().strip()
        if "https://" in remote_url:
            remote_url = remote_url.replace("https://", f"https://{GIT_TOKEN}@")
        subprocess.call(["git", "push", "--force", remote_url, "--quiet"])

        logger.info("Successfully committed, pulled, and force pushed to GitHub")
    except Exception as e:
        logger.error(f"Failed to commit, pull, and push: {str(e)}")
    finally:
        # Reopen log handler after Git operations
        reopen_log_handler()

# Main loop to capture and process the screen at intervals
if __name__ == '__main__':
    while True:
        logger.info("Capturing screen...")
        text, img = capture_and_ocr()

        # Save screenshot with a unique name
        screenshot_path = save_screenshot(img)

        if text.strip() and text != previous_text:
            logger.info("Uploading OCR result and screenshot to Git...")
            upload_to_git(text, screenshot_path)
            previous_text = text
        else:
            logger.warning("No significant changes detected, skipping upload.")

        time.sleep(30)  # Capture every 30 seconds
