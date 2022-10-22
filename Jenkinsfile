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

        stage("build") {

            steps {
                sh 'mvn clean install -Dmaven.test.skip=true '
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
