import pyaudio
import json
import os
import subprocess
import logging
from vosk import Model, KaldiRecognizer

# Paths to files and directories
transcript_file_path = 'D:/GitHubProjects/DevUtils/transcribe/transcript.txt'
log_file_path = 'D:/GitHubProjects/DevUtils/transcribe/app.logs'
repo_dir = 'D:/GitHubProjects/DevUtils'  # Replace with your local repo path

# Configure logging
logging.basicConfig(filename=log_file_path, level=logging.ERROR, format='%(asctime)s - %(levelname)s - %(message)s')

def main():
    # Load the Vosk model
    model_path = "path_to_unzipped_model"
    model = Model(model_path)

    # Initialize PyAudio
    p = pyaudio.PyAudio()

    try:
        # Open microphone stream
        stream = p.open(format=pyaudio.paInt16, channels=1, rate=16000, input=True, frames_per_buffer=8192)
        stream.start_stream()

        # Initialize recognizer
        recognizer = KaldiRecognizer(model, 16000)

        def transcribe_audio():
            while True:
                try:
                    data = stream.read(4096, timeout=1.0)
                    if recognizer.AcceptWaveform(data):
                        result = recognizer.Result()
                        result_json = json.loads(result)
                        if 'text' in result_json:
                            transcription = result_json['text']
                            print(f"Transcription: {transcription}")
                            # Append to file
                            with open(transcript_file_path, 'a') as f:
                                f.write(transcription + '\n')
                            # Commit and push to GitHub
                            git_commit_and_push()
                except IOError as e:
                    logging.error(f"IOError: {e}")
                except KeyboardInterrupt:
                    logging.info("Interrupted by user")
                    break
                except Exception as e:
                    logging.error(f"Exception: {e}")

        def git_commit_and_push():
            try:
                # Change directory to the local Git repository
                os.chdir(repo_dir)
                # Add changes to staging
                subprocess.run(['git', 'add', transcript_file_path], check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                # Commit changes
                commit_message = "Update transcription file"
                subprocess.run(['git', 'commit', '-m', commit_message], check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                # Push changes to the remote repository
                subprocess.run(['git', 'push'], check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                print("Committed and pushed changes to GitHub.")
            except subprocess.CalledProcessError as e:
                logging.error(f"Git command error: {e}")

        # Start transcription
        transcribe_audio()

    finally:
        # Ensure resources are cleaned up
        if stream.is_active():
            stream.stop_stream()
            stream.close()
        p.terminate()

if __name__ == "__main__":
    main()
