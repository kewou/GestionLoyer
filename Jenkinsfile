pipeline{

    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
    }

    stages {


        stage('Deploy to Nexus') {
            steps {             
                sh 'mvn deploy -Dmaven.test.skip=true'
            }
        }
    }
}
