import cx_Oracle

# Establish the database connection
connection = cx_Oracle.connect(user="your_username", password="your_password", dsn="your_dsn")

# Create a cursor
cursor = connection.cursor()

# List of SQL files
sql_files = ['path_to_your_table_sql_file.sql', 'path_to_your_view_sql_file.sql', 'path_to_your_procedure_sql_file.sql', 
             'path_to_your_sequence_sql_file.sql', 'path_to_your_function_sql_file.sql', 'path_to_your_package_sql_file.sql']

for sql_file in sql_files:
    # Open your SQL file
    with open(sql_file, 'r') as file:
        sql_script = file.read()

    # Execute the SQL script
    cursor.execute(sql_script)

# Commit the transaction
connection.commit()

# Close the connection
connection.close()
