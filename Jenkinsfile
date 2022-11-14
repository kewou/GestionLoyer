pipeline{
    environment {
        registry = "http://localhost:8081/repository/DockerNexus/"
        registryCredential = 'DockerNexus'
      }
    agent any
    tools {
        maven 'maven339'
        jdk 'jdk8'
        docker 'docker'
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

        stage("Sonar Analysis") {
            steps {
                echo 'Analyse sonar'
            }
        }

        stage("Build Image") {
            steps{
                script {
                    dockerImageName=docker.build ('DockerNexus/gestionloyer')
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
