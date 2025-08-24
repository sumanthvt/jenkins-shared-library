@Library('jenkins-shared-library') _

pipeline {
    agent any   // Runs on EC2 host
    options {
        timestamps()
    }

    stages {

        stage('Unit Tests') {
            steps {
                // Run Gradle wrapper tests
                sh './gradlew clean test'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Code Coverage') {
            steps {
                sh './gradlew jacocoTestReport'
            }
            post {
                always {
                    publishHTML(target: [
                        reportDir: 'build/reports/jacoco/test/html',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage'
                    ])
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQubeServer') {
                    sh './gradlew sonarqube'
                }
            }
        }
    }
}