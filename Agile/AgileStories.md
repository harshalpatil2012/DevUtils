Description:
As a QA Engineer, I want to create a JMeter test plan to measure the performance of the GET API, so that we can ensure the API meets our performance criteria under different load conditions.

Task:
Test Plan Creation:
Create a JMeter test plan to simulate GET API requests.
Configure the test plan to include necessary components (Thread Group, HTTP Request, Listeners).
Ensure the test plan can be easily modified for different environments (e.g., dev, stage).

Load Simulation: 
Simulate various load conditions (e.g., 10, 100, 500 concurrent users).
Define ramp-up periods and loop counts for realistic load testing scenarios.

Assertions:
Add assertions to verify the response code (e.g., 200 OK).
Ensure the response time is within acceptable limits (e.g., < 500 ms).

Result Analysis:
Configure listeners to collect performance metrics (e.g., Response Time, Throughput, Error Rate).
Generate and save test results in a structured format (e.g., CSV, HTML).

Documentation:
Document the test plan setup and execution steps.
Provide guidelines on how to interpret the test results.
Include recommendations for performance improvement based on test outcomes.




Benchmark Application Performance Post Spring Boot and Java  Upgrade
As a developer
I want to: Benchmark the application's performance after the Spring Boot and Java upgrade. So that: We can quantify any performance improvements gained from the upgrade.

Acceptance Criteria:

Identify key performance metrics relevant to the application (e.g., response times, throughput, resource utilization).
Establish a baseline performance profile using the current Java and Spring Boot versions.
Compare the pre- and post-upgrade performance metrics to identify any improvements.
Document the benchmarking results and performance changes observed.


User Story: Upgrade Spring Boot and Java Version
As a Developer
I want to: Upgrade the application to leverage the latest features of Java 17 and potentially the newest Spring Boot version.
So that: We can benefit from performance improvements, new language features, and potential bug fixes in both Java and Spring Boot.

Acceptance Criteria:

Update the project's Java version to Java 17.
Update the Spring Boot dependencies to a compatible version (consider the latest stable version or a well-tested LTS release).
Identify and address any compatibility issues arising from the upgrade.
Update unit and integration tests to ensure continued functionality after the upgrade.
Build and deploy the upgraded application to a staging environment for thorough testing.
Perform manual and automated testing to verify the application's functionality after the upgrade.
Update documentation to reflect the new Java and Spring Boot versions used.
