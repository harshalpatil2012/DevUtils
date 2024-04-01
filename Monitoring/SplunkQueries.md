1. Identifying Slow-Performing Transactions

index=app_logs 
| transaction request_id 
| eval duration=max(timestamp)-min(timestamp) 
| where duration > 5  
| sort - duration 
| table request_id, duration, user, status_code
Logic: Groups events by request_id, calculates request duration, filters for transactions taking longer than 5 seconds, and displays key details for investigation.

2. Error Spike Visualization

index=web_logs status_code>=500 
| timechart count by status_code
Logic: Counts error occurrences over time, grouped by status code. This helps detect if error patterns have a temporal aspect.

3. Unusual User Activity (Using Regex)

index=security_logs action="*" 
| rex field=user_action "(?i)deleted\s+(critical|sensitive)\s+file" 
| table user, file_name, action
Logic: Uses a regular expression to find actions indicating deletion of sensitive files, making the search case-insensitive.

4. Debugging Search Performance Issues

index=_internal sourcetype=splunkd source=*search.log* 
| stats avg(search_duration) as avg_duration by search
Logic: Analyzes Splunk's own internal logs to identify long-running searches, pinpointing potential bottlenecks.

5. Field Extraction Verification

index=network_logs 
| eval extracted_ip=if(isnull(src_ip), "MISSING_EXTRACTION", src_ip)
| stats count by extracted_ip
Logic: Checks if extractions are working as intended. A high count for "MISSING_EXTRACTION" suggests a field extraction problem.

Search for Errors and Exceptions:

index=* (error OR exception OR fail*) | table _time, sourcetype, host, source, _raw
This query searches for errors, exceptions, or failure messages across all indexes. It's useful for quickly identifying issues in your logs.

Identify Slow Queries:

index=* sourcetype=your_sourcetype | transaction your_transaction_field | search duration > your_threshold
Replace your_sourcetype with your actual sourcetype and your_transaction_field with the field representing a transaction (e.g., session ID). This query helps identify slow transactions that exceed a certain threshold.

Check Server Response Time:

index=* sourcetype=access_combined | stats avg(response_time) as avg_response_time by host | sort -avg_response_time
This query calculates the average response time for each server based on access logs and sorts them in descending order. It helps identify servers with slower response times.

Identify Top Errors by Count:


index=* (error OR exception) | top limit=10 _raw
This query identifies the top 10 most frequent errors or exceptions in your logs.

Search for Specific Error Codes:

index=* (error_code=500 OR error_code=404) | table _time, sourcetype, host, source, error_code
Replace error_code=500 and error_code=404 with the specific error codes you want to search for. This query helps identify occurrences of specific error codes across your logs.

Check for Disk Space Errors:
index=os sourcetype=ps | stats latest(percent_disk_used) as "Disk Used (%)" by host
This query checks the disk usage on each host using the ps sourcetype. It helps identify hosts with high disk usage.

Check for Memory Usage:

index=os sourcetype=ps | stats latest(percent_memory_used) as "Memory Used (%)" by host
Similar to the previous query, this one checks the memory usage on each host using the ps sourcetype.

Search for Network Errors:

index=* sourcetype=your_network_sourcetype "error" OR "exception" | table _time, sourcetype, host, source, _raw


Count total records based on repeated count logs like total records =111 

index=your_index_name_here "total records="
| rex field=_raw "total records=(?<record_count>\d+)"
| stats sum(record_count) as total_records

