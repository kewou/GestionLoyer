pipeline{

    agent any

    stages {

        stage("build") {

            steps {
                echo 'building the application'
                mvn clean install
            }
        }

        stage("deploy") {

            steps {
                echo 'deploy the application'
            }
        }
    }
}
