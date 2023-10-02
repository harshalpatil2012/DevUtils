# DevUtils

# Jenkins Pipeline for SonarQube Integration

## Overview

This Jenkins pipeline script automates the integration of SonarQube code analysis into your software development workflow. It checks for changes in specified code file types (Java, JavaScript, TypeScript, HTML) on a branch and triggers a SonarQube scan if changes are detected.

## Prerequisites

Before using this Jenkins pipeline script, ensure you have the following prerequisites set up:

1. **Jenkins**: You need a Jenkins instance installed and configured.
2. **SonarQube**: Set up a SonarQube server and configure the necessary projects.
3. **Git**: Your project should be version-controlled using Git.

## Usage

1. **Create a Jenkinsfile**: Copy the provided Jenkins pipeline script into a file named `Jenkinsfile`.

2. **Customize the Script**:
   - Replace `"my-feature-branch"` with the name of your target branch.
   - Configure the SonarQube server details in the "SonarQube Scan" stage.
   - Customize any other parameters and settings according to your project's requirements.

3. **Store the Jenkinsfile**:
   - Option 1: Store the Jenkinsfile in your project's root directory if you want to keep it version-controlled along with your code.
   - Option 2: Store the Jenkinsfile in a shared location accessible by your Jenkins instance.

4. **Configure Jenkins Job**:
   - Set up a Jenkins job or pipeline.
   - In the job configuration, specify where Jenkins can find the Jenkinsfile.
   - Ensure the job is configured to run automatically or on trigger events.

## Execution

- When the Jenkins job is triggered, the pipeline script performs the following steps:
   1. Checks out your project's code from the version control system.
   2. Determines if there are changes in Java, JavaScript, TypeScript, or HTML files between the latest commit and the previous one.
   3. If code changes are detected, it triggers a SonarQube scan to analyze the code quality.
   4. If no code changes are detected, it skips the SonarQube scan.

- The script can be used to continuously monitor code quality for the specified file types in your project's branch.

## Feedback and Contributions

Feel free to provide feedback or contribute to the script to enhance its functionality. If you encounter issues or have suggestions, please create a GitHub issue or submit a pull request.
