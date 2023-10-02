import boto3
from datetime import datetime, timedelta

def get_log_events_count(log_group_name, log_stream_name, start_time, end_time):
    # Initialize the Boto3 CloudWatch Logs client
    logs_client = boto3.client('logs')

    # Set the query parameters
    query_params = {
        'logGroupName': log_group_name,
        'logStreamName': log_stream_name,
        'startTime': start_time,
        'endTime': end_time
    }

    # Initialize variables to keep track of log events and token
    total_log_events = 0
    next_token = None

    # Retrieve log events in batches until there are no more events
    while True:
        if next_token:
            query_params['nextToken'] = next_token

        # Retrieve a batch of log events
        response = logs_client.get_log_events(**query_params)

        # Count the number of log events in this batch
        batch_log_events = len(response['events'])
        total_log_events += batch_log_events

        # Check if there are more log events to retrieve
        if 'nextToken' in response:
            next_token = response['nextToken']
        else:
            break

    return total_log_events

if __name__ == '__main__':
    # Specify the log group name and log stream name
    log_group_name = 'your-log-group-name'
    log_stream_name = 'your-log-stream-name'

    # Calculate the start and end times for the last 7 days
    end_time = int(datetime.now().timestamp()) * 1000  # Current time in milliseconds
    start_time = end_time - (7 * 24 * 60 * 60 * 1000)  # 7 days ago in milliseconds

    # Get the total number of log events for the last 7 days
    log_event_count = get_log_events_count(log_group_name, log_stream_name, start_time, end_time)

    print(f'Total Log Events in the Last 7 Days: {log_event_count}')
