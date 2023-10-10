Servlet Performance Metrics Readme
Welcome to the Servlet Performance Metrics Readme! This document provides valuable insights into the performance metrics of different Servlet API versions, including latency, average throughput, and the minimum requirements for Tomcat and JDK versions.

Table of Contents
Introduction
Servlet Performance Metrics
Latency Improvement
Throughput Improvement
Tomcat and JDK Requirements
Conclusion
Additional Notes
Introduction
This readme file offers a comprehensive overview of the performance metrics for various Servlet API versions. It outlines the key parameters like latency, average throughput, and the minimum requirements for Tomcat and JDK versions for each Servlet version.

Servlet Performance Metrics
Here is a summary of the performance metrics for different Servlet versions:

Servlet Version	Tomcat Version	Min JDK Version	Latency (ms)	Average Throughput (requests/second)
3.1	8	8	8.5	1000
4	9	11	7.5	1200
5	10	11	7	1400
6	11	17	6.5	1600
Latency Improvement
Latency improvement represents the percentage decrease in latency from one Servlet version to the next. For instance, Servlet 4 has a latency improvement of -11.76% compared to Servlet 3.1, indicating that Servlet 4 is 11.76% faster than Servlet 3.1.

Throughput Improvement
Throughput improvement reflects the percentage increase in throughput from one Servlet version to the next. For example, Servlet 4 has a throughput improvement of 20.00% over Servlet 3.1, signifying that Servlet 4 can handle 20.00% more requests per second than Servlet 3.1.

Tomcat and JDK Requirements
Here are the minimum Tomcat and JDK requirements for each Servlet version:

Servlet Version	Minimum Tomcat Version	Minimum JDK Version
4	9	11
5	10	11
6	11	17
Conclusion
In conclusion, Servlet 4, 5, and 6 offer substantial improvements in latency and throughput compared to Servlet 3.1. Whether you are developing a new Servlet application or upgrading an existing one, it is strongly recommended to use Servlet 4 or higher for enhanced performance.

Additional Notes
Latency is measured in milliseconds (ms), with lower values indicating a faster server.
Average throughput is measured in requests/second, with higher values indicating a more scalable server.
Latency improvement and throughput improvement are presented as percentages. Negative latency improvement values signify a decrease in latency, while positive values indicate an increase. Similarly, negative throughput improvement values indicate a decrease in throughput, while positive values indicate an increase.