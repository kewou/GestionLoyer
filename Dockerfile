FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /app

ARG JAR_FILE=target/basic-1.0.0.jar


COPY ${JAR_FILE} /app/app.jar

# Exposition
EXPOSE 8080

# java -jar /usr/local/runme/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
