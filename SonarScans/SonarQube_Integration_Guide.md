
# SonarQube Integration Guide

This document outlines the steps required to integrate SonarQube analysis into a Maven project. This setup allows for running SonarQube scans locally, which is ideal for catching issues early in the development cycle.

## Prerequisites

- **Java JDK**: Ensure Java JDK 11 or higher is installed.
- **Maven**: Maven 3.6 or higher must be installed.
- **SonarQube Server**: A local SonarQube server is required. Download and install from [SonarQube Downloads](https://www.sonarqube.org/downloads/).

## Setup SonarQube Server

1. **Install SonarQube**: Follow the instructions on the SonarQube website to install and run the SonarQube server on your local machine.
2. **Access SonarQube**: Once the server is running, you can access the SonarQube dashboard at `http://localhost:9000`. Default login is `admin` with password `admin`.

## Configure Maven Project

### Add SonarQube Plugin

Insert the following into the `pom.xml` within the `<build>` section:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.sonarsource.scanner.maven</groupId>
      <artifactId>sonar-maven-plugin</artifactId>
      <version>3.9.1.2184</version>
    </plugin>
  </plugins>
</build>
```

### Create Properties File

Create a `sonar.properties` file in your project's root directory with the following configuration:

```properties
# SonarQube server
sonar.host.url=http://localhost:9000
sonar.login=your_token_here

# Project identification
sonar.projectKey=myproject_key
sonar.projectName=My Project Name
sonar.projectVersion=1.0

# Branch information
sonar.branch.name=feature/my_feature
sonar.newCode.referenceBranch=main

# Source directories
sonar.sources=src/main/java
sonar.tests=src/test/java

# Exclusions and inclusions
sonar.exclusions=**/vendor/**,**/example/**,**/*.gen.java
sonar.test.exclusions=**/test/vendor/**
sonar.inclusions=**/*.java

# Additional configurations
sonar.java.binaries=target/classes
sonar.language=java
sonar.sourceEncoding=UTF-8

```

### Maven Properties Plugin

Add the `properties-maven-plugin` to load the `sonar.properties` file at build initialization:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>properties-maven-plugin</artifactId>
      <version>1.0.0</version>
      <executions>
        <execution>
          <phase>initialize</phase>
          <goals>
            <goal>read-project-properties</goal>
          </goals>
          <configuration>
            <files>
              <file>${project.basedir}/sonar.properties</file>
            </files>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

## Running SonarQube Analysis

To perform a SonarQube scan, execute the following Maven command from the root of your project:

```bash
mvn clean verify sonar:sonar
```

This command will clean the project, run verification (including tests), and perform the SonarQube analysis, uploading the results to your local SonarQube server.

## Viewing the Results

After the scan completes, you can view the results by accessing the SonarQube dashboard at `http://localhost:9000` and navigating to your project's report.

## Conclusion

Integrating SonarQube with Maven allows for continuous inspection of code quality, helping to maintain and improve the health of your project's codebase over time.
