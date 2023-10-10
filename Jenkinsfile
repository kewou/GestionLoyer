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
                    sh 'mvn release:prepare -DreleaseVersion=0.0.6 -DdevelopmentVersion=0.0.7-SNAPSHOT release:perform -Dtag=0.0.6 -DbranchName=main -P my-nexus --settings /var/jenkins_home/settings.xml'
                }
            }
        }

    }
}
