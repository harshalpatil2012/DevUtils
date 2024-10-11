from pyspark.sql import SparkSession
from pyspark.sql.types import *

# Create a SparkSession
spark = SparkSession.builder.appName("LoadExcel").getOrCreate()

# Define the schema of your Excel data (adjust as needed)
schema = StructType([
    StructField("column1", StringType(), True),  # Example column, replace with your actual column names and types
    StructField("column2", IntegerType(), True),
    # ... more columns
])

# Read the Excel file (replace "your_file.xlsx" and "Sheet1" with your actual file and sheet name)
df = spark.read.format("com.crealytics.spark.excel") \
    .option("header", "true") \
    .option("inferSchema", "false") \
    .schema(schema) \
    .load("your_file.xlsx", sheetName="Sheet1")

# Display the DataFrame
df.show()

# Stop the SparkSession
spark.stop()


Question 1: Data Transformation and Aggregation

Problem:

You are given a PySpark DataFrame representing sales data with columns: product_id, category, sales_amount, date.
Calculate the total sales for each category for the most recent date in the dataset.
Expected Solution Outline:

from pyspark.sql.functions import col, max, sum

# Find the most recent date
latest_date = df.agg(max("date")).collect()[0][0]

# Filter data for the latest date and aggregate sales by category
result = df.filter(col("date") == latest_date) \
           .groupBy("category") \
           .agg(sum("sales_amount").alias("total_sales"))

result.show()

Problem:

You have a PySpark DataFrame with customer transactions: customer_id, transaction_amount, transaction_date.
For each customer, calculate the 3-day rolling average of their transaction amounts.
Expected Solution Outline:

from pyspark.sql.window import Window
from pyspark.sql.functions import avg, col

# Define the window specification
window_spec = Window.partitionBy("customer_id") \
                   .orderBy("transaction_date") \
                   .rowsBetween(-2, 0)  # 3-day window including current row

# Calculate the rolling average
result = df.withColumn("rolling_avg", avg("transaction_amount").over(window_spec))

result.show()


Question 3: Data Cleaning and Filtering

Problem:

You are given a PySpark DataFrame containing user data: user_id, email, age, country.
Filter out rows where the email is invalid (doesn't contain '@') or the age is less than 18.

from pyspark.sql.functions import col

# Filter based on email validity and age
result = df.filter((col("email").contains("@")) & (col("age") >= 18))

result.show()


from pyspark.sql import SparkSession
from pyspark.sql.functions import col, max, sum, avg
from pyspark.sql.window import Window
from pyspark.sql.types import *

# Create a SparkSession
spark = SparkSession.builder.appName("KaratPySparkInterview").getOrCreate()

# Sample DataFrames (replace with actual data if available)
sales_data = [
    ("P1", "Electronics", 1000, "2023-09-10"),
    ("P2", "Electronics", 500, "2023-09-09"),
    ("P3", "Clothing", 200, "2023-09-10"),
    ("P4", "Clothing", 300, "2023-09-08"),
]
sales_schema = StructType([
    StructField("product_id", StringType(), True),
    StructField("category", StringType(), True),
    StructField("sales_amount", IntegerType(), True),
    StructField("date", StringType(), True),
])
sales_df = spark.createDataFrame(sales_data, sales_schema)

transaction_data = [
    (1, 100, "2023-09-08"),
    (1, 200, "2023-09-09"),
    (1, 150, "2023-09-10"),
    (2, 50, "2023-09-07"),
    (2, 80, "2023-09-09"),
]
transaction_schema = StructType([
    StructField("customer_id", IntegerType(), True),
    StructField("transaction_amount", IntegerType(), True),
    StructField("transaction_date", StringType(), True),
])
transaction_df = spark.createDataFrame(transaction_data, transaction_schema)

user_data = [
    (1, "user1@example.com", 25, "USA"),
    (2, "invalid_email", 30, "Canada"),
    (3, "user3@example.com", 15, "UK"),
]
user_schema = StructType([
    StructField("user_id", IntegerType(), True),
    StructField("email", StringType(), True),
    StructField("age", IntegerType(), True),
    StructField("country", StringType(), True),
])
user_df = spark.createDataFrame(user_data, user_schema)


# Question 1: Total Sales by Category for Latest Date
latest_date = sales_df.agg(max("date")).collect()[0][0]
result_q1 = sales_df.filter(col("date") == latest_date) \
                    .groupBy("category") \
                    .agg(sum("sales_amount").alias("total_sales"))
print("Question 1: Total Sales by Category for Latest Date")
result_q1.show()


# Question 2: 3-Day Rolling Average of Transaction Amounts
window_spec = Window.partitionBy("customer_id") \
                    .orderBy("transaction_date") \
                    .rowsBetween(-2, 0)
result_q2 = transaction_df.withColumn("rolling_avg", avg("transaction_amount").over(window_spec))
print("\nQuestion 2: 3-Day Rolling Average of Transaction Amounts")
result_q2.show()


# Question 3: Filter Invalid Emails and Underage Users
result_q3 = user_df.filter((col("email").contains("@")) & (col("age") >= 18))
print("\nQuestion 3: Filter Invalid Emails and Underage Users")
result_q3.show()


# Stop the SparkSession
spark.stop()

Question 4: Joins and Data Enrichment

Problem:

You have two DataFrames:
orders_df with columns: order_id, customer_id, order_date, total_amount
customers_df with columns: customer_id, customer_name, city
Enrich the orders_df by joining it with customers_df to include customer_name and city information.
Expected Solution Outline:

 
# Perform an inner join on 'customer_id'
enriched_orders_df = orders_df.join(customers_df, on="customer_id", how="inner")

# Select the desired columns
result_q4 = enriched_orders_df.select("order_id", "customer_name", "city", "order_date", "total_amount")

print("\nQuestion 4: Joins and Data Enrichment")
result_q4.show()

Question 5: Handling Missing Values

Problem:

You have a DataFrame with potentially missing values in the age column.
Fill the missing age values with the average age calculated from the non-missing values.
Expected Solution Outline:
 
from pyspark.sql.functions import avg, when, col, isnull

# Calculate the average age from non-missing values
average_age = df.agg(avg(when(~isnull("age"), col("age")))).collect()[0][0]

# Fill missing 'age' values with the calculated average
result_q5 = df.withColumn("age", when(isnull("age"), average_age).otherwise(col("age")))

print("\nQuestion 5: Handling Missing Values")
result_q5.show()


Question 6: User-Defined Functions (UDFs)

Problem:

You have a DataFrame with a text column containing product descriptions.
Create a UDF to extract the first word from each product description and add it as a new column first_word.
Expected Solution Outline:

Python
from pyspark.sql.functions import udf

# Define the UDF to extract the first word
def extract_first_word(text):
    if text:
        return text.split()[0]
    else:
        return None

extract_first_word_udf = udf(extract_first_word, StringType())

# Apply the UDF to create the new column
result_q6 = df.withColumn("first_word", extract_first_word_udf(col("text")))

print("\nQuestion 6: User-Defined Functions (UDFs)")
result_q6.show()