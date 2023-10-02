# Spring Boot Application Setup and Health Check Script

**Author:** Harshal

This script is designed to simplify the process of setting up and checking the health of a Spring Boot application. It covers the following steps:

1. Cloning a Git repository.
2. Copying a JDK from a Nexus repository.
3. Building and running the Spring Boot application.
4. Continuously checking the application's health status until it's up and running.

## Prerequisites

Before using this script, make sure you have the following:

- Git installed on your system.
- The application's Git repository URL.
- Access to a Nexus repository with the JDK package you need.
- Gradle Wrapper (`gradlew`) or Gradle installed for building the Spring Boot application.
- A running Spring Boot application that exposes a health check endpoint (usually `/actuator/health`).

## Usage

1. **Clone the Repository**: Replace `GIT_REPO_URL` with the URL of your Git repository.

2. **Copy JDK from Nexus**: Replace `NEXUS_JDK_URL` with the URL of the JDK package in your Nexus repository.

3. **Build and Run the Application**: Customize `BUILD_COMMAND` and `RUN_COMMAND` as needed for your project.

4. **Health Check**: The script continuously checks the health of the application using the `HEALTHCHECK_URL`. Customize this URL to match your application's health check endpoint.

5. **Script Configuration**:
   - You can adjust the `MAX_ATTEMPTS` and `SLEEP_INTERVAL` variables to control the number of health check attempts and the time between attempts.
   - Customize the `APP_DIRECTORY` variable to set the directory where the application will be cloned.
   - Set the `APPLICATION_PORT` variable to specify the port for the Spring Boot application.

6. **Running the Script**:

   ### On Windows
   
   - Open a Command Prompt (cmd).
   - Navigate to the directory where the script is located.
   - Run the script using the following command:
     ```batch
     setup-and-health-check.bat
     ```

   ### On Linux/macOS

   - Open a terminal.
   - Navigate to the directory where the script is located.
   - Make the script executable (if not already) using the following command:
     ```bash
     chmod +x setup-and-health-check.sh
     ```
   - Run the script using the following command:
     ```bash
     ./setup-and-health-check.sh
     ```

7. **Monitoring**: The script logs the health check status. If the application is successfully running, it will indicate success. If the health check fails after all attempts, the script logs an error message.

## Note

- This script is intended for development and testing purposes.
- Customize the script as needed to match your project's specific requirements.

Feel free to reach out if you encounter any issues or have questions about using this script.
