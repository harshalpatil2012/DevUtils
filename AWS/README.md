===============================================
Amazon CloudWatch- Log Events Count Script
===============================================
**Author:** Harshal

This Python script retrieves the log event count from Amazon CloudWatch Logs for a specified log group and log stream within the last 7 days.

Prerequisites:
--------------
- Python installed on your system.
- Boto3 library installed (use 'pip install boto3').
- AWS credentials configured (environment variables, AWS CLI config, or IAM role).

Usage:
------
1. Open 'get_log_events_count.py' in a text editor.
2. Modify 'log_group_name' and 'log_stream_name' to match your environment.
3. Optionally, customize the 'start_time' and 'end_time' for a different time range.
4. Save your changes.
5. Open a terminal/command prompt and navigate to the script directory.
6. Run the script: 'python get_log_events_count.py'.
7. The script will display the total log event count for the last 7 days.

Example Output:
---------------
Total Log Events in the Last 7 Days: 12345

Notes:
------
- Ensure AWS credentials are configured correctly.
- Customize time range by modifying 'start_time' and 'end_time'.
- For assistance or questions, contact the script author.

For more details and updates, visit the GitHub repository: [link to repository].
