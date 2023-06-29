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
                        // Configuration properties
                        /*
                        def sonarProperties = [
                            "sonar.projectKey": "gestionLoyer",
                            "sonar.projectName": "gestionLoyer",                    
                            "sonar.sources": "src",
                            "sonar.java.binaries": "target/classes",
                            // ... autres propriétés de configuration SonarQube
                        ]

                        */
                        // Configuration de SonarQube

                        sh "mvn sonar:sonar -Dsonar.projectKey=gestionLoyer -Dsonar.projectName=gestionLoyer -Dsonar.sources=src -Dsonar.language=java -Dsonar.java.binaries=target/classes"

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
