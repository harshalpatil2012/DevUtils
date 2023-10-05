# Local Database Configuration for [Your Spring Boot Project]

This README provides instructions on configuring the local database for [Your Spring Boot Project] using an embedded H2 database. This setup is intended for local development purposes.

## Prerequisites

- [Java JDK](https://www.oracle.com/java/technologies/javase-downloads.html) installed on your machine.
- [Maven](https://maven.apache.org/download.cgi) (if using Maven) or [Gradle](https://gradle.org/install/) (if using Gradle) installed on your machine.

## Getting Started

1. Clone the [Your Spring Boot Project] repository to your local machine.

2. Open a terminal or command prompt and navigate to the project directory.

3. **Ensure that your SQL script is named `your-script.sql`** and placed in the project root directory under the `database/localDB` folder.

4. Update your application configuration:

   - **Using `application.properties` (Maven):**
     - Open the `src/main/resources/application.properties` file.
     - Set the script location property to point to your SQL script:
       ```properties
       your-script-location=file:./database/localDB/your-script.sql
       ```

   - **Using `application.yml` (Gradle):**
     - Open the `src/main/resources/application.yml` file.
     - Set the script location property to point to your SQL script:
       ```yaml
       your-script-location: file:./database/localDB/your-script.sql
       ```

5. Run your Spring Boot application with the local-dev profile:

   - **Maven:**
     ```
     mvn spring-boot:run -Dspring.profiles.active=local-dev
     ```

   - **Gradle:**
     ```
     gradle bootRun -Dspring.profiles.active=local-dev
     ```

6. Your Spring Boot application will start, and the H2 database will be initialized with the data from your SQL script.

7. You can access the H2 database console at (http://localhost:8080/h2-console) to interact with the database if needed. Use the JDBC URL `jdbc:h2:mem:testdb`, username `sa`, and no password to connect to the H2 database.

## Additional Notes

- This setup is intended for local development only. Ensure that you have the necessary configurations for other environments, such as staging and production.

- Make sure to keep your SQL script (`your-script.sql`) up to date with your database schema changes during development.

- Remember that the H2 database is an in-memory database, and its data will be lost when the application stops. It is suitable for local development and testing.

- If you encounter any issues or have questions, please refer to the project documentation or contact [Your Contact Information].

Happy coding!
