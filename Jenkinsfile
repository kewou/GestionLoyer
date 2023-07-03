pipeline{

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
                    sh 'mvn release:prepare -DreleaseVersion=0.0.4 -DdevelopmentVersion=0.0.6-SNAPSHOT release:perform -Dtag=Canada -DbranchName=jenkins -P my-nexus --settings /var/jenkins_home/settings.xml'
                }
            }
        }
    }
}
