pipeline {
    agent any

    tools {
        maven 'LocalMaven' 
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo 'Running tests and building package...'
                bat 'mvn clean package' // 'package' runs tests AND creates the .jar
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo 'Saving the .jar file...'
                // This looks into the 'target' folder and saves any .jar file found
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
    
    post {
        always {
            // Record the test results in Jenkins UI
            echo 'Collecting test results...'
            junit '**/target/surefire-reports/*.xml'
        }
        success {
            echo 'Build and Testing successful! Artifact is ready.'
        }
        failure {
            echo 'Build failed. Check the logs and test reports.'
        }
    }
}