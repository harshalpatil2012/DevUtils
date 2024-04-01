import cx_Oracle
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def copy_records(source_conn_str, target_conn_str, batch_size=1000):
    try:
        # Connect to the source and target databases
        source_conn = cx_Oracle.connect(source_conn_str)
        target_conn = cx_Oracle.connect(target_conn_str)

        # Create cursors for both connections
        source_cursor = source_conn.cursor()
        target_cursor = target_conn.cursor()

        # Fetch data from the source table
        source_cursor.execute("SELECT * FROM source_table")
        all_records = source_cursor.fetchall()

        # Copy records in batches
        for i in range(0, len(all_records), batch_size):
            batch = all_records[i:i + batch_size]
            target_cursor.executemany("INSERT INTO target_table VALUES (:1, :2)", batch)
            target_conn.commit()

            logger.info(f"Batch {i // batch_size + 1} copied successfully")

    except Exception as e:
        logger.error(f"Error: {str(e)}")
        raise

    finally:
        # Close connections
        source_cursor.close()
        target_cursor.close()
        source_conn.close()
        target_conn.close()

if __name__ == "__main__":
    source_conn_str = "source_user/source_password@source_tns"
    target_conn_str = "target_user/target_password@target_tns"

    try:
        copy_records(source_conn_str, target_conn_str)
        logger.info("Data copy completed successfully!")

    except Exception as e:
        logger.error(f"Data copy failed: {str(e)}")
