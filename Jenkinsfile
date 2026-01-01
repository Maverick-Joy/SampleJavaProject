pipeline {
    agent any

    tools {
        // This must match the name you gave Maven in 'Global Tool Configuration'
        maven 'LocalMaven'
    }

    stages {
        stage('Checkout') {
            steps {
                // This pulls the code from the GitHub repo linked to the job
                checkout scm
            }
        }

        stage('Build & Compile') {
            steps {
                echo 'Compiling the project...'
                bat 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running tests...'
                // Use 'sh' if Jenkins is on Linux, 'bat' if it's on Windows
                bat 'mvn test'
            }
            post {
                always {
                    // Record the test results in Jenkins UI
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
    }
}