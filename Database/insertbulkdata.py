import cx_Oracle

# Set up the connection (update these details with your actual database credentials)
dsn = cx_Oracle.makedsn('host', 'port', sid='your_sid')  # or use service_name='your_service_name'
connection = cx_Oracle.connect('username', 'password', dsn)
cursor = connection.cursor()

# SQL Insert Statement Template
# Assuming you have a column named txn_id where the counter will be appended
sql_insert = "INSERT INTO your_table_name (txn_id, data_field) VALUES (:1, :2)"

try:
    # Loop to insert 20 million records
    for i in range(1, 20000001):
        # Generate transaction ID by appending the counter
        txn_id = f"TXN{i}"
        # Data can be modified to whatever you need it to be
        data = f"Sample Data {i}"

        # Execute the SQL statement with the generated txn_id and data
        cursor.execute(sql_insert, [txn_id, data])

        # Commit in batches to avoid excessive memory use and manage transactions better
        if i % 10000 == 0:
            connection.commit()
            print(f"Committed {i} records")

    # Final commit for any remaining records
    connection.commit()
    print("Final commit completed.")

except Exception as e:
    print(f"An error occurred: {e}")
finally:
    # Close the cursor and connection
    cursor.close()
    connection.close()

print("All records inserted successfully.")