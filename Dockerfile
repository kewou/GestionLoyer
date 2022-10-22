FROM adoptopenjdk/openjdk11:alpine-jre

ARG JAR_FILE=target/basic-0.0.1-SNAPSHOT.jar

# cd /usr/local/runme
WORKDIR /usr/local/springboot

# copy target/find-links.jar /usr/local/runme/app.jar
COPY ${JAR_FILE} app.jar

# Exposition
EXPOSE 8090

# java -jar /usr/local/runme/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
