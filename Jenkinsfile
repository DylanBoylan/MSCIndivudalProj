pipeline {
    agent any

    environment {
        SONARQUBE_ENV = 'My SonarQube Server' // Set this name in Jenkins global config
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
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    bat """
                    mvn sonar:sonar ^
                      -Dsonar.projectKey=indv ^
                      -Dsonar.host.url=http://your-sonarqube-server-url ^
                      -Dsonar.login=${credentials('sonar-token')} ^
                      -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    timeout(time: 5, unit: 'MINUTES') {  // Increased timeout
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "❌ Quality Gate failed: ${qg.status}"
                        }
                    }
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
