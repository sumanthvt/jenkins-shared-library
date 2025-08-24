@Library('jenkins-shared-library') _

pipeline {
    agent any   // Use host node to avoid Docker-in-Docker issues
    options {
        timestamps()
    }

    stages {

        stage('Unit Tests') {
            steps {
                // Run Gradle tests inside a temporary Docker container
                sh '''
                docker run --rm \
                    -v $PWD:/app \
                    -w /app \
                    gradle:8.9-jdk21 \
                    ./gradlew clean test
                '''
            }
            post {
                always {
                    // Publish JUnit test results
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Code Coverage') {
            steps {
                sh '''
                docker run --rm \
                    -v $PWD:/app \
                    -w /app \
                    gradle:8.9-jdk21 \
                    ./gradlew jacocoTestReport
                '''
            }
            post {
                always {
                    // Publish JaCoCo HTML report
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
                    sh '''
                    docker run --rm \
                        -v $PWD:/app \
                        -w /app \
                        gradle:8.9-jdk21 \
                        ./gradlew sonarqube
                    '''
                }
            }
        }
    }
}