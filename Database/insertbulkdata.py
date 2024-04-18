import cx_Oracle
from concurrent.futures import ThreadPoolExecutor

# Define connection details
dsn = cx_Oracle.makedsn('host', 'port', sid='your_sid')
username = 'username'
password = 'password'

# Batch size for each commit
batch_size = 10000  # Adjust this based on your performance tests and database capabilities

def insert_records(start_id, end_id):
    """Inserts a batch of records from start_id to end_id inclusive."""
    conn = cx_Oracle.connect(username, password, dsn)
    cursor = conn.cursor()

    # Prepare the insert statement
    sql_insert = "INSERT INTO your_table_name (txn_id, data_field) VALUES (:1, :2)"
    
    try:
        # Create a batch of records
        records = [(f"TXN{i}", f"Sample Data {i}") for i in range(start_id, end_id + 1)]
        cursor.executemany(sql_insert, records)
        conn.commit()  # Commit the whole batch
        print(f"Inserted records from TXN{start_id} to TXN{end_id}")
    except Exception as e:
        print(f"Error in batch from TXN{start_id} to TXN{end_id}: {e}")
    finally:
        cursor.close()
        conn.close()

# Total number of records to insert
num_records = 20000000
# Number of threads
num_threads = 10  # Adjust based on your environment

# Using ThreadPoolExecutor for managing parallel inserts
with ThreadPoolExecutor(max_workers=num_threads) as executor:
    # Splitting the work into batches
    ranges = [(i, min(i + batch_size - 1, num_records)) for i in range(1, num_records + 1, batch_size)]
    results = executor.map(lambda x: insert_records(*x), ranges)

print("All records inserted successfully.")