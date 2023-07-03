pipeline{

    agent any

    environment {
        GITHUB_TOKEN = credentials('github_token')
    }
    tools {
        maven 'maven'
        jdk 'jdk8'
    }

    stages {
        stage('Checkout') {
            steps {
                withCredentials([string(credentialsId: 'github_token', variable: 'SECRET_VALUE')]) {
                    sh "git config --global user.name 'myusername'"
                    sh "git config --global user.email 'myemail@example.com'"
                    sh "git config --global http.extraheader 'Authorization: Bearer ${SECRET_VALUE}'"
                    sh "git clone https://github.com/kewou/GestionLoyer.git"
                }
            }        
        }
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
                    sh 'mvn release:prepare -DreleaseVersion=0.0.4 -DdevelopmentVersion=0.0.5-SNAPSHOT release:perform -Dtag=false -DbranchName=jenkins'
                }
            }
        }
    }
}
