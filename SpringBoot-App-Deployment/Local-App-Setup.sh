#!/bin/bash

# Define variables
GIT_REPO_URL="https://github.com/your/repo.git"
NEXUS_JDK_URL="https://nexus.example.com/nexus/repository/maven-public/com/example/jdk/11.0.13/jdk-11.0.13.zip"
APP_DIRECTORY="my-spring-boot-app"
BUILD_COMMAND="./gradlew build"
RUN_COMMAND="./gradlew bootRun"
HEALTHCHECK_URL="http://localhost:8080/actuator/health"
MAX_ATTEMPTS=10  # Number of health check attempts
SLEEP_INTERVAL=10  # Number of seconds between health check attempts
APPLICATION_PORT=8080  # Port for the Spring Boot application

# Function to log messages with timestamp
log() {
  echo "$(date +"%Y-%m-%d %H:%M:%S") - $1"
}

# Step 1: Clone the Git repository
log "Step 1: Cloning Git repository..."
git clone "$GIT_REPO_URL" "$APP_DIRECTORY"

# Step 2: Copy the JDK from Nexus (Assuming JDK is packaged as a ZIP file)
log "Step 2: Copying JDK from Nexus..."
wget -O jdk.zip "$NEXUS_JDK_URL"
unzip jdk.zip -d "$APP_DIRECTORY"
rm jdk.zip

# Step 3: Copy Certificates to the Application Directory (if needed)
log "Step 3: Copying Certificates to the Application Directory..."
cd "$APP_DIRECTORY"

# Assuming your certificates are stored in the "certs" folder within your repository
CERTS_DIR="certs"
cp "$CERTS_DIR"/* ./

# Step 4: Import Certificates into JDK's Truststore
log "Step 4: Importing Certificates into JDK's Truststore..."
JAVA_HOME="$APP_DIRECTORY" ./bin/keytool -importcert -trustcacerts -file certificate.crt -alias myapp -keystore ./lib/security/cacerts -storepass changeit -noprompt

# Step 5: Build the Spring Boot application
log "Step 5: Building Spring Boot application..."
$BUILD_COMMAND

# Step 6: Configure Spring Boot Profile, Xmx, and Port
log "Step 6: Configuring Spring Boot Profile, Xmx, and Port..."
echo "spring.profiles.active=localdev" >> application.properties
echo "JAVA_OPTS=\"-Xmx1024m\"" >> application.properties
echo "server.port=$APPLICATION_PORT" >> application.properties
echo "server.http2.enabled=true" >> application.properties

# Step 7: Run the Spring Boot application
log "Step 7: Running Spring Boot application..."
$RUN_COMMAND &

# Wait for the application to start
log "Waiting for the application to start..."
for ((i = 1; i <= MAX_ATTEMPTS; i++)); do
  if curl --output /dev/null --silent --fail "$HEALTHCHECK_URL"; then
    log "Health check passed. Application is running successfully."
    break
  fi
  log "Health check attempt $i failed. Retrying in $SLEEP_INTERVAL seconds..."
  sleep $SLEEP_INTERVAL
done

if [ $i -gt $MAX_ATTEMPTS ]; then
  log "Health check failed after $MAX_ATTEMPTS attempts. Application may not be running."
  # Notify developer or perform further actions on failure
  # Example: send an email to the developer
  # mail -s "Error: Spring Boot App is Not Running" developer@example.com
fi
