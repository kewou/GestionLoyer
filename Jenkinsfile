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
        /*
        stage("Test") {

            steps {
                sh 'mvn clean install'
            }
        }*/

        stage("Sonar Analysis") {
            steps {
                script {
                    withSonarQubeEnv('sonar') {

                        sh "mvn sonar:sonar -Dsonar.projectKey=gestionLoyer -Dsonar.projectName=gestionLoyer -Dsonar.sources=src/main -Dsonar.language=java -Dsonar.java.binaries=target/classes -Dsonar.jacoco.reportPaths=target/site/jacoco/jacoco.xml"

                    }
                }
            }
        }
        /*
        stage('Deploy to Nexus') {
            steps {             
                sh 'mvn deploy -Dmaven.test.skip=true -P my-nexus --settings /var/jenkins_home/settings.xml'
            }
        }*/
    }
}
