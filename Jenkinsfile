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
                echo 'Analyse sonar'
            }
        }

        stage("Build Image") {
            agent { dockerfile true }
            steps {
                sh 'docker build -t springboot .'
            }
        }

    }
}
