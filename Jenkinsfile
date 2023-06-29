pipeline{

    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
        sonar 'sonar'
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
                def sonarProperties = [
                    "sonar.projectKey": "gestionLoyer",
                    "sonar.projectName": "gestionLoyer",                    
                    "sonar.sources": "src",
                    "sonar.java.binaries": "target/classes",
                    // ... autres propriétés de configuration SonarQube
                ]
                // Configuration étendue (facultatif)
                def additionalProperties = [
                    "sonar.exclusions": "**/*.xml",
                    // ... autres propriétés de configuration supplémentaires
                ]
                
                // Configuration de SonarQube
                withSonarQubeProperties(sonarProperties + additionalProperties) {
                    // Lancer l'analyse SonarQube
                    sh "mvn sonar:sonar"
                }
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
