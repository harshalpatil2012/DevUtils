# Generate the content for the markdown (.md) file for SonarQube integration documentation.
markdown_content = """
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
## Create Properties File
Create a sonar.properties file in your project's root directory:
Refer sonar.properties

##  Maven Properties Plugin
Add the properties-maven-plugin to load the sonar.properties file at build initialization:
sonar-pom.xml

##   Running SonarQube Analysis
To perform a SonarQube scan, execute the following Maven command from the root of your project:
```
mvn clean verify sonar:sonar