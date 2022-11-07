pipeline{

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
                sh 'mvn test'
            }
        }

        stage("deploy") {

            steps {
                echo 'deploy the application'
            }
        }
    }
}
