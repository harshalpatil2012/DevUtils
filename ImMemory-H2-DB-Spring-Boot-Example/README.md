# Local Database Configuration for [Spring Boot Project]

This README provides instructions on configuring the local database for [Spring Boot Project] using an embedded H2 database. This setup is intended for local development purposes.

## Prerequisites

- [Java JDK](https://www.oracle.com/java/technologies/javase-downloads.html) installed on machine.
- [Maven](https://maven.apache.org/download.cgi) (if using Maven) or [Gradle](https://gradle.org/install/) (if using Gradle) installed on machine.

## Getting Started

1. Clone the [Spring Boot Project] repository to local machine.

2. Open a terminal or command prompt and navigate to the project directory.

3. **Ensure that SQL script is named `script.sql`** and placed in the project root directory under the `database/localDB` folder.

4. Update application configuration:

   - **Using `application.properties` (Maven):**
     - Open the `src/main/resources/application.properties` file.
     - Set the script location property to point to SQL script:
       ```properties
       your-script-location=file:./database/localDB/your-script.sql
       ```

   - **Using `application.yml` (Gradle):**
     - Open the `src/main/resources/application.yml` file.
     - Set the script location property to point to SQL script:
       ```yaml
       your-script-location: file:./database/localDB/your-script.sql
       ```

5. Run Spring Boot application with the local-dev profile:

   - **Maven:**
     ```
     mvn spring-boot:run -Dspring.profiles.active=local-dev
     ```

   - **Gradle:**
     ```
     gradle bootRun -Dspring.profiles.active=local-dev
     ```

6. Spring Boot application will start, and the H2 database will be initialized with the data from SQL script.

7. You can access the H2 database console at (http://localhost:8080/h2-console) to interact with the database if needed. Use the JDBC URL `jdbc:h2:mem:testdb`, username `sa`, and no password to connect to the H2 database.

## Additional Notes

- This setup is intended for local development only. Ensure that you have the necessary configurations for other environments, such as staging and production.

- Make sure to keep SQL script (`your-script.sql`) up to date with database schema changes during development.

- Remember that the H2 database is an in-memory database, and its data will be lost when the application stops. It is suitable for local development and testing.


Happy coding!
