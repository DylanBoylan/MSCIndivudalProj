pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'My SonarQube Server' // Set this name in Jenkins global config
        SONAR_TOKEN = credentials('sonar-token') // Jenkins > Credentials > secret text
    }

    tools {
        maven 'Maven 3.9.9'  // Match this to your Jenkins tool name
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/DylanBoylan/MSCIndivudalProj.git', branch: 'dev'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Test with JaCoCo') {
            steps {
                bat 'mvn test'
            }
        }

        stage('JaCoCo Report') {
            steps {
                bat 'mvn jacoco:report'  // Generates the coverage report
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${env.SONARQUBE_ENV}") {
                    bat """
                    mvn sonar:sonar ^
                      -Dsonar.projectKey=indv ^
                      -Dsonar.login=${SONAR_TOKEN} ^
                      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

       
    }

    post {
        success {
            echo "✅ Build, test, and SonarQube analysis completed successfully!"
        }
        failure {
            echo "❌ Build or analysis failed! Check logs."
        }
    }
}
