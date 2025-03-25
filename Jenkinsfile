pipeline {
    agent any
 
    environment {
        SONARQUBE_ENV = 'My SonarQube Server' // Set this name in Jenkins global config
        SONAR_TOKEN = credentials('sonar-token') // Jenkins > Credentials > secret text
    }
 
    tools {
        maven 'Maven 3.9'         // Match this to your Jenkins tool name
        jdk 'Temurin 17'          // Match this to your Jenkins tool name
    }
 
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/DylanBoylan/MSCIndivudalProj.git', branch: 'dev'
            }
        }
 
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
 
        stage('Test') {
            steps {
               sh 'mvn test'
            }
        }
 
        stage('SonarQube Analysis') {
            steps {
                    withSonarQubeEnv("${env.SONARQUBE_ENV}") {
                        sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=indv \
                          -Dsonar.login=${SONAR_TOKEN}
                        """
                    
                }
            }
        }
     }
}
