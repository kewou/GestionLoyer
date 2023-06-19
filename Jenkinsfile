pipeline{

    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
    }

    stages {

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
                sh 'mvn deploy -Dmaven.test.skip=true -P my-nexus --settings /var/jenkins_home/workspace/settings.xml'
            }
        }
    }
}
