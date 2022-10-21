pipeline{

    agent any
    tools {
        maven 'Maven 3.3.9'
        jdk 'jdk11'
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

        stage("build") {

            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true install'
            }
        }

        stage("test") {
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
