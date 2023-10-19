pipeline{
    environment {
        registry = "http://localhost:8081/repository/DockerNexus/"
        registryCredential = 'DockerNexus'
      }
    agent any

    tools {
        maven 'maven'
        jdk 'jdk8'
    }

    stages {
        stage("Test") {

            steps {
                sh 'mvn clean install'
            }
        }

        stage ("Clean"){
            steps {
                sh 'mvn release:clean'
            }
        }

        stage ("Release") {
            steps{
                withCredentials([
                string(
                    credentialsId: 'github_token',
                    variable: 'TOKEN'
                )
                ]){
                    sh 'git tag'
                }
            }
        }

    }
}
