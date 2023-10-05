@echo off
:: Define variables
set GIT_REPO_URL=https://github.com/your/repo.git
set NEXUS_JDK_URL=https://nexus.example.com/nexus/repository/maven-public/com/example/jdk/11.0.13/jdk-11.0.13.zip
set APP_DIRECTORY=my-spring-boot-app
set BUILD_COMMAND=gradlew.bat build
set RUN_COMMAND=gradlew.bat bootRun
set HEALTHCHECK_URL=http://localhost:8080/actuator/health
set MAX_ATTEMPTS=10  :: Number of health check attempts
set SLEEP_INTERVAL=10  :: Number of seconds between health check attempts
set APPLICATION_PORT=8080  :: Port for the Spring Boot application

:: Function to log messages with timestamp
call :log "Step 1: Cloning Git repository..."
git clone %GIT_REPO_URL% %APP_DIRECTORY%

:: Step 2: Copy the JDK from Nexus (Assuming JDK is packaged as a ZIP file)
call :log "Step 2: Copying JDK from Nexus..."
curl -o jdk.zip %NEXUS_JDK_URL%
powershell -command "Expand-Archive -Path .\jdk.zip -DestinationPath %APP_DIRECTORY% -Force"
del jdk.zip

:: Step 3: Copy Certificates to the Application Directory (if needed)
call :log "Step 3: Copying Certificates to the Application Directory..."
cd %APP_DIRECTORY%
:: Assuming your certificates are stored in the "certs" folder within your repository
xcopy /s "certs\*" .\

:: Step 4: Import Certificates into JDK's Truststore
call :log "Step 4: Importing Certificates into JDK's Truststore..."
set JAVA_HOME=%APP_DIRECTORY%
call .\bin\keytool -importcert -trustcacerts -file certificate.crt -alias myapp -keystore .\lib\security\cacerts -storepass changeit -noprompt

:: Step 5: Build the Spring Boot application
call :log "Step 5: Building Spring Boot application..."
call %BUILD_COMMAND%

:: Step 6: Configure Spring Boot Profile, Xmx, and Port
call :log "Step 6: Configuring Spring Boot Profile, Xmx, and Port..."
echo spring.profiles.active=localdev >> application.properties
echo JAVA_OPTS="-Xmx1024m" >> application.properties
echo server.port=%APPLICATION_PORT% >> application.properties
echo server.http2.enabled=true >> application.properties

:: Step 7: Run the Spring Boot application
call :log "Step 7: Running Spring Boot application..."
start /B %RUN_COMMAND%

:: Wait for the application to start
call :log "Waiting for the application to start..."
set i=1
:check_health
if %i% leq %MAX_ATTEMPTS% (
  curl --output nul --silent --fail %HEALTHCHECK_URL%
  if %errorlevel% neq 0 (
    call :log "Health check attempt %i% failed. Retrying in %SLEEP_INTERVAL% seconds..."
    timeout /t %SLEEP_INTERVAL% /nobreak > nul
    set /a i+=1
    goto check_health
  ) else (
    call :log "Health check passed. Application
