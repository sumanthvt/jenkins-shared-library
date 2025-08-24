pipeline {
    agent {
        docker {
            image 'gradle:8.9-jdk17'
            args '-v $HOME/.gradle:/home/gradle/.gradle'
        }
    }
    /*
    tools {
        jdk 'jdk11'
        gradle 'gradle7'
    }
    */
    stages {
        stage('Checkout') {
            //steps { checkout scm }
            steps {
                git branch: 'main', url: 'https://github.com/sumanthvt/jenkins-shared-library.git'
            }
        }
        stage('Unit Tests') {
            steps { sh './gradlew clean test' }
            post {
                always { junit 'build/test-results/test/*.xml' }
            }
        }
        stage('Code Coverage') {
            steps { sh './gradlew jacocoTestReport' }
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