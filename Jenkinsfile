pipeline{
    environment {
        registry = "http://localhost:8081/repository/DockerNexus/"
        registryCredential = 'DockerNexus'
        MAVEN_VERSION = ''
        DOCKER_IMAGE_NAME: 'gestionloyer-app'
      }
    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
    }

    stages {
        stage("Build & Test") {
            steps {
                sh 'mvn clean install'
            }
        }

        stage("Get Maven Version"){
            steps {
                script {
                    MAVEN_VERSION = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()
                    echo "La version Maven extraite est : ${MAVEN_VERSION}"
                }
            }
        }

        stage("Sonar Analysis") {
            steps {
                script {
                    withSonarQubeEnv('sonar') {

                        sh "mvn sonar:sonar -Dsonar.projectKey=gestionLoyer -Dsonar.projectName=gestionLoyer -Dsonar.sources=src/main -Dsonar.language=java -Dsonar.java.binaries=target/classes -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"

                    }
                }
            }
        }

        stage("Build image"){
            steps {
                sh 'docker build -t $DOCKER_IMAGE_NAME:$VERSION .'
            }
        }

        stage("Run image"){
            steps {
                sh 'docker run -d --name BACKEND -p 8090:8090 --network JavaNetwork $DOCKER_IMAGE_NAME:$VERSION'
            }
        }


              
        stage('Deploy SNAPSHOT to Nexus') {
            steps {             
                sh 'mvn deploy -Dmaven.test.skip=true -P my-nexus --settings /var/jenkins_home/settings.xml'
            }
        }

    }
}
