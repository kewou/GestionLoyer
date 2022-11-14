pipeline{
    environment {
        registry = "http://localhost:8081/repository/DockerNexus/"
        registryCredential = 'DockerNexus'
      }
    agent any
    tools {
        maven 'maven339'
        jdk 'jdk8'
    }

    stages {

        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage("Build") {

            steps {
                sh 'mvn clean install'
            }
        }

        stage("Sonar Analysis") {
            steps {
                echo 'Analyse sonar'
            }
        }

        stage("Build Image") {
            steps{
                script {
                    dockerImageName=docker.build Gestionloyer:latest
                }
            }
        }

        stage("Deploy"){
            steps{
                script {
                  docker.withRegistry( '', registryCredential ) {
                    dockerImage.push()
                  }
                }
              }
        }

    }
}
