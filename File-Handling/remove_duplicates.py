import os
import time
from multiprocessing import Pool

def remove_duplicates(input_file):
    start_time = time.time()  # Record the start time
    output_folder = "C:/updates"
    output_file = os.path.join(output_folder, os.path.splitext(os.path.basename(input_file))[0] + "_unique.txt")
    total_records = 0
    total_unique_records = 0
    total_duplicate_records = 0
    seen = set()
    with open(input_file, 'r') as infile, open(output_file, 'w') as outfile:
        for line in infile:
            total_records += 1  # Increment total records count
            if line not in seen:
                seen.add(line)
                outfile.write(line)
                total_unique_records += 1  # Increment unique records count
            else:
                total_duplicate_records += 1  # Increment duplicate records count
    end_time = time.time()  # Record the end time
    execution_time = end_time - start_time  # Calculate the total execution time
    
    # Print counts
    print(f"Total records: {total_records}")
    print(f"Total unique records: {total_unique_records}")
    print(f"Total duplicate records: {total_duplicate_records}")
    print(f"Total execution time: {execution_time} seconds")
    print(f"Unique records file generated: {output_file}")

if __name__ == "__main__":
    input_files = ["C:/Logs/20millionloc.csv"]  # List of input files
   # input_files = ["C:/Logs/5m-records.csv"]  # List of input files
   # input_files = ["C:/Logs/500000-Sales-Records.csv"]  # List of input files
    num_processes = os.cpu_count()  # Number of CPU cores for parallel processing

    with Pool(num_processes) as pool:
        pool.map(remove_duplicates, input_files)
