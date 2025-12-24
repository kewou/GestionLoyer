# Utiliser une image Maven pour la phase de build
FROM maven:3.8.6-openjdk-11 AS build

COPY . /app

# Aller dans le répertoire de l'application
WORKDIR /app

# Construire le projet et récupérer la version
RUN mvn clean package -DskipTests && \
    export VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout) && \
    cp target/basic-${VERSION}.jar /app/myapp.jar


# Nouvelle étape pour l'image finale
FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /app

# Copier le jar depuis l'image de construction
COPY --from=build /app/myapp.jar /app/myapp.jar

# Exposition
EXPOSE 8090

# Lancer l'application Spring Boot (le profil sera défini par la variable d'environnement)
ENTRYPOINT ["java","-jar","/app/myapp.jar"]
