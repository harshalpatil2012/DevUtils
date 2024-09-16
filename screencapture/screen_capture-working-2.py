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

# Paths
TESSERACT_PATH = r"C:/Program Files/Tesseract-OCR/tesseract.exe"
LOG_PATH = r"D:/GitHubProjects/DevUtils/screencapture/capture.log"
APP_LOG_PATH = r"D:/GitHubProjects/DevUtils/screencapture/app.log"
LOCAL_IMG_PATH = r"D:/GitHubProjects/DevUtils/screencapture/screen_capture.png"
REPO_PATH = r"D:\GitHubProjects\DevUtils\screencapture"

# Tesseract setup
pytesseract.pytesseract.tesseract_cmd = TESSERACT_PATH

# Logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("ScreenCaptureLogger")
handler = RotatingFileHandler(APP_LOG_PATH, maxBytes=5000000, backupCount=5)
logger.addHandler(handler)

# Caching for OCR results
previous_text = ""

# Optimized image preprocessing (with sharpening)
def preprocess_image(image):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    thresh = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)[1]

    # Denoising 
    kernel = np.ones((1, 1), np.uint8)
    opening = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, kernel, iterations=1)

    # Sharpening
    kernel = np.array([[-1,-1,-1], 
                       [-1, 9,-1],
                       [-1,-1,-1]])
    sharpened = cv2.filter2D(opening, -1, kernel)

    return sharpened 

# Capture and OCR 
def capture_and_ocr():
    start_time = time.time()

    screenshot = pyautogui.screenshot()
    img = np.array(screenshot)
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR) 
    preprocessed_img = preprocess_image(img)

    logger.info("Starting OCR...")

    custom_config = r'--oem 3 --psm 6' 
    text = pytesseract.image_to_string(preprocessed_img, config=custom_config)

    end_time = time.time()
    logger.info(f"OCR completed in {end_time - start_time:.2f} seconds")

    return text

# Upload to Git (with silent Git operations and hardcoded token)
def upload_to_git(text):
    try:
        os.chdir(REPO_PATH)

        with open(LOG_PATH, 'a', encoding='utf-8') as f:
            f.write("\n----------------- SEPARATOR -----------------\n")
            f.write(text)

        subprocess.call(["git", "add", LOG_PATH])
        subprocess.call(["git", "commit", "-m", "Updated screen capture log", "--quiet"])

        # Hardcoded Git token (replace with your actual token)
        GIT_TOKEN = "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"  
        remote_url = subprocess.check_output(["git", "config", "--get", "remote.origin.url"]).decode().strip()
        if "https://" in remote_url:
            remote_url = remote_url.replace("https://", f"https://{GIT_TOKEN}@")
        subprocess.call(["git", "push", remote_url, "--quiet"])

        logger.info("Successfully committed and pushed to GitHub")
    except Exception as e:
        logger.error(f"Failed to commit and push: {str(e)}")

# Main loop
if __name__ == '__main__':
    while True:
        logger.info("Capturing screen...")
        text = capture_and_ocr()

        if text.strip() and text != previous_text:
            logger.info("Uploading to Git...")
            upload_to_git(text)
            previous_text = text 
        else:
            logger.warning("No significant changes detected, skipping upload.")

        time.sleep(30)