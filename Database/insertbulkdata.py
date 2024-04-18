import cx_Oracle
from concurrent.futures import ThreadPoolExecutor, as_completed

# Connection details
dsn = cx_Oracle.makedsn('host', 'port', sid='your_sid')
username = 'username'
password = 'password'

# Function to perform database insert
def insert_record(i):
    conn = cx_Oracle.connect(username, password, dsn)
    cursor = conn.cursor()
    
    txn_id = f"TXN{i}"
    data = f"Sample Data {i}"
    sql_insert = "INSERT INTO your_table_name (txn_id, data_field) VALUES (:1, :2)"
    
    try:
        cursor.execute(sql_insert, [txn_id, data])
        conn.commit()  # Commit per insert, consider batch commits for better performance
    except Exception as e:
        print(f"Error inserting record {i}: {e}")
    finally:
        cursor.close()
        conn.close()
    
    return f"Record {i} inserted"

# Number of records to insert
num_records = 20000000
# Number of threads to use
num_threads = 10  # Adjust based on your system's capabilities and database's ability to handle concurrent connections

# Using ThreadPoolExecutor to manage parallel inserts
with ThreadPoolExecutor(max_workers=num_threads) as executor:
    # Submitting all jobs to the executor
    futures = [executor.submit(insert_record, i) for i in range(1, num_records + 1)]
    
    # Optional: Print out results as they complete (for tracking progress)
    for future in as_completed(futures):
        print(future.result())

print("All records inserted successfully.")