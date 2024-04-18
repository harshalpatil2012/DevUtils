import cx_Oracle
from concurrent.futures import ThreadPoolExecutor

# Connection details
dsn = cx_Oracle.makedsn('host', 'port', sid='your_sid')
username = 'username'
password = 'password'

# Create a connection pool
pool = cx_Oracle.SessionPool(user=username, password=password, dsn=dsn, min=2, max=10, increment=1)

# Batch size for each commit
batch_size = 10000  # Adjust based on performance testing

def insert_records(start_id, end_id):
    """Inserts a batch of records from start_id to end_id inclusive."""
    conn = pool.acquire()
    cursor = conn.cursor()

    # Prepare the insert statement
    sql_insert = "INSERT INTO your_table_name (txn_id, data_field) VALUES (:1, :2)"
    cursor.prepare(sql_insert)
    
    # Create a batch of records
    records = [(f"TXN{i}", f"Sample Data {i}") for i in range(start_id, end_id + 1)]

    # Perform batch insert
    cursor.executemany(None, records)
    conn.commit()  # Commit the whole batch
    print(f"Inserted records from TXN{start_id} to TXN{end_id}")

    cursor.close()
    pool.release(conn)

# Total number of records to insert
num_records = 20000000
# Number of threads
num_threads = 10  # Adjust based on your system capabilities

# Using ThreadPoolExecutor for managing parallel inserts
with ThreadPoolExecutor(max_workers=num_threads) as executor:
    # Splitting the work into batches
    ranges = [(i, min(i + batch_size - 1, num_records)) for i in range(1, num_records + 1, batch_size)]
    list(executor.map(lambda x: insert_records(*x), ranges))

pool.close()
print("All records inserted successfully.")