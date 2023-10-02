
 // Sample script to skip Sonar scan in case there are no programming changes like java, javascript etc.
 // Many times as a developer when to do change and checkin Jenkins automatically trigger build job.
 // In case of huge codebase it take too much time to complete sonar scan and in case if there are only config changes, 
 // Jenkins build take additional time to do the code scan and validate Qulity gate which may not be needed in such scnerio.
 
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Checkout your code from your version control system (e.g., Git)
                script {
                    checkout scm
                }
            }
        }

        stage('Check for Code Changes') {
            steps {
                script {
                    def branchName = 'my-feature-branch'
                    def allCommits = sh(
                        returnStdout: true,
                        script: "git log --pretty=format:'%h' ${branchName}"
                    ).trim().split('\n')

                    def codeChangesDetected = false

                    for (def commitHash in allCommits) {
                        def codeChanges = sh(
                            returnStatus: true,
                            script: "git diff --name-only ${commitHash}~1..${commitHash} -- '*.java' '*.js' '*.ts' '*.html'"
                        )
                        
                        if (codeChanges) {
                            // Code changes detected for this commit
                            codeChangesDetected = true
                            break
                        }
                    }

                    if (codeChangesDetected) {
                        // Code changes detected, run Sonar scan
                        stage('SonarQube Scan') {
                            steps {
                                withSonarQubeEnv('Your_SonarQube_Server_Name') {
                                    // Configure SonarQube properties as needed
                                    sh "sonar-scanner"
                                }
                            }
                        }
                    } else {
                        // No code changes, skip Sonar scan
                        echo "No code file changes detected. Skipping SonarQube scan."
                    }
                }
            }
        }
    }
}
