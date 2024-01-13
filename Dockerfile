# Étape de construction: Utiliser l'image de base officielle Maven avec JDK 19 pour construire le projet Spring Boot
FROM maven:3.9.6-amazoncorretto-21 as build

# Copier les fichiers du projet dans l'image
COPY src /home/app/src
COPY pom.xml /home/app

# Définir le répertoire de travail pour les commandes suivantes
WORKDIR /home/app

# Construire le projet et empaqueter l'application
RUN mvn clean package -DskipTests

# Étape d'exécution: Utiliser une image de base avec JDK 19 pour exécuter l'application
FROM openjdk:19-jdk-slim

# Copier le fichier JAR du conteneur de build au conteneur de run
COPY --from=build /home/app/target/MySurvey-0.0.1-SNAPSHOT.jar /usr/local/lib/springboot.jar

# Exposer le port sur lequel l'application s'exécute
EXPOSE 8080

# Définir la commande pour exécuter l'application
ENTRYPOINT ["java","-jar","/usr/local/lib/springboot.jar"]
