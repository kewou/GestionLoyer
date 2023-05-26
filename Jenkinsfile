pipeline{

    agent any
    tools {
        maven 'maven'
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
                sh 'mvn compile'
            }
        }

        stage("Test") {

            steps {
                sh 'mvn clean install'
            }
        }

        stage("Sonar Analysis") {
            steps {
                echo 'Sonar : en cours de mise en place'
            }
        }

        stage('Deploy to Nexus') {
            steps {             
                sh 'mvn deploy -Dmaven.test.skip=true -Dmaven.deploy.skip=true'
            }
        }
    }
}
