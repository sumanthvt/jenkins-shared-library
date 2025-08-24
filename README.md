jenkins-shared-lib/
├── vars/
│   └── deployApp.groovy             # Shared library step
│
├── src/
│   └── org/
│       └── example/
│           └── utils/
│               └── DeployUtils.groovy   # Utility class used in the var
│
├── test/
│   └── org/
│       └── example/
│           └── DeployAppTest.groovy     # Unit tests using JenkinsPipelineUnit
│
├── build.gradle                    # Gradle build config
├── settings.gradle                 # Gradle settings
├── gradlew / gradlew.bat           # Gradle wrapper scripts
├── gradle/wrapper/                 # Gradle wrapper JAR + properties
├── Jenkinsfile                     # Jenkins pipeline to run tests + sonar
└── README.md


🛠️ Requirements
    Java 11+
    Gradle 7+ (wrapper included)
    Docker (for running SonarQube locally, optional)
    Jenkins with required plugins:
    Pipeline
    JUnit
    SonarQube Scanner
    JaCoCo
    HTML Publisher

▶️ Usage in a Jenkins Pipeline

In your Jenkins pipeline (Jenkinsfile in a consuming project):

@Library('jenkins-shared-lib') _
pipeline {
    agent any
    stages {
        stage('Deploy') {
            steps {
                script {
                    deployApp("myApp", "1.0.0", "dev")
                }
            }
        }
    }
}
