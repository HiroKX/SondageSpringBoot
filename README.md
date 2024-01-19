[![Test Coverage](https://github.com/HiroKX/SondageSpringBoot/blob/gh-pages/jacoco/jacoco.svg)](https://hirokx.github.io/SondageSpringBoot/jacoco)

[Pitest Mutation Coverage](https://hirokx.github.io/SondageSpringBoot/pitest)
# Setup du projet
Afin de faciliter l'utilisation de l'application, nous avons mis en place un conteneur docker.
Un fichier ".env" que vous créerez va instancier un conteneur avec une base de données **PostgreSQL**, **adminer**, un gestionnaire de database simple en php et héberger l'application entière.

##Local
###Prérequis :
1. Base de donnée
Il faut posséder une Base de donnée, ici nous vous conseillons d'utiliser Postgres : https://www.postgresql.org/

2. Java 19
Pour installer Java 19 : https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html

###Installation : 
Dans le fichier [application-local.properties](https://github.com/HiroKX/SondageSpringBoot/blob/56-fix-du-readme/src/test/resources/application.properties), il faut changer les variables de connexion à votre base de donnée.
Il s'agit du port, du nom d'utilisateur, du mot de passe et du nom de la base de donnée.

Une fois que tout ceci est fait, il vous suffit de lancer le programme [MySurveyApplication](https://github.com/HiroKX/SondageSpringBoot/blob/56-fix-du-readme/src/main/java/fr/univ/lorraine/ufr/mim/m2/gi/mysurvey/MySurveyApplication.java).

##Docker
Pour déployer ce conteneur : 
* Installer docker.
* Créer un fichier .env dans le root du projet et éditer la configuration : 
```bash
SPRING_PROFILES_ACTIVE=local
DB_USERNAME= # Ex : postgres
DB_PASSWORD= # Ex : admin
DB_NAME= # Ex : MySurvey
DB_URL="jdbc:postgresql://db:5432"
ADMINER_PORTS= # Ex : 8081
DB_PORTS= # Ex : 5432
SERVER_PORT= # Ex : 8090
```

Il vous faut également dans le fichier Dockerfile, changer la variable d'environnement **EXPOSE** (Ligne 21) par votre port.

* Ouvrer un **CMD** dans le root du projet et effectuer la commande : 
``` docker-compose up --build```

# Lancement du projet

## Tests

Pour lancer les tests d'intégrations du projet : `mvn test`

## Run

Une fois le projet démarré : 

* Accès à l'api Swagger : http://localhost:{ VOTRE_SERVER_PORT }/swagger-ui.html (dépend du SERVER_PORT que vous avez set)

* Documentation de l'api : http://localhost:{ VOTRE_SERVER_PORT }/v3/api-docs (dépend du SERVER_PORT que vous avez set)
  
* Interface adminer pour consulter la DB : http://localhost:{ VOTRE_SERVER_PORT }/ (dépend du SERVER_PORT que vous avez set)

## Accès au projet en ligne :

Vous pouvez accéder à notre projet directement en ligne ! 

Voici un lien pour y accéder : https://sondage.ronde-lingons.fr/swagger-ui.html
