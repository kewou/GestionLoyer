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

        stage('Prepare Release') {
            steps {             
                sh "mvn release:prepare --settings /var/jenkins_home/settings.xml -P my-nexus"
            }
        }

    }
}
