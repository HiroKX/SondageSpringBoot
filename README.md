[![Test Coverage](https://github.com/HiroKX/SondageSpringBoot/blob/gh-pages/jacoco/jacoco.svg)](https://hirokx.github.io/SondageSpringBoot/jacoco)

[Pitest Mutation Coverage](https://hirokx.github.io/SondageSpringBoot/pitest)

# Setup et lancement du projet
Afin de faciliter l'utilisation de l'application, nous avons mis en place un conteneur docker.
Un fichier ".env" que vous créerez va instancier un conteneur avec une base de données **PostgreSQL**, **adminer**, un gestionnaire de database simple en php et héberger l'application entière.

Une fois le projet démarré sur un environnement non déployé  : 

* Accès à l'api Swagger : http://localhost:{ VOTRE_SERVER_PORT }/swagger-ui.html (dépend du SERVER_PORT que vous avez set)

* Documentation de l'api : http://localhost:{ VOTRE_SERVER_PORT }/v3/api-docs (dépend du SERVER_PORT que vous avez set)
  
* Interface adminer pour consulter la DB : http://localhost:{ VOTRE_SERVER_PORT }/ (dépend du SERVER_PORT que vous avez set)

## Local
### Prérequis :
* Base de donnée
Il faut posséder une Base de donnée, ici nous vous conseillons d'utiliser Postgres : https://www.postgresql.org/

* Java 19
Pour installer Java 19 : https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html

### Installation : 
Dans le fichier [application-local.properties](https://github.com/HiroKX/SondageSpringBoot/blob/56-fix-du-readme/src/test/resources/application.properties), il faut changer les variables de connexion à votre base de donnée.
Il s'agit du port, du nom d'utilisateur, du mot de passe et du nom de la base de donnée.

Une fois que tout ceci est fait, il vous suffit de lancer le programme [MySurveyApplication](https://github.com/HiroKX/SondageSpringBoot/blob/56-fix-du-readme/src/main/java/fr/univ/lorraine/ufr/mim/m2/gi/mysurvey/MySurveyApplication.java).

## Docker
Nous recommandons l'utilisation de Docker car il permet de contenir votre application dans un environnement qui n'atteint pas votre machine.
Il est tout de même recommander d'utiliser JAVA19 si vous voulez modifier le projet.

### Prérequis
Afin de pouvoir lancer correctement ce projet de cette façon il vous faut :

1. Un environnement Docker sur votre machine

Et c'est tout !

### Installation

Il est possible d'augmenter la sécurité de votre application lors du déploiement du conteneur en refusant les connexions à la base de donnée pour chaque requête provenant de l'exterieur du conteneur.
Pour ce faire, il suffit de retirer ces [ligne](https://github.com/HiroKX/SondageSpringBoot/blob/develop/docker-compose.yml#L25-L26) dans votre docker-compose.ml.

Pour déployer ce conteneur : 
1. Installer docker.
2. Créer un fichier .env dans le root du projet et éditer la configuration : 
```bash
SPRING_PROFILES_ACTIVE=local
DB_USERNAME= # Ex : postgres
DB_PASSWORD= # Ex : admin
DB_NAME= # Ex : MySurvey
DB_URL="jdbc:postgresql://db:5432"
SERVER_PORT= # Ex : 8090
ADMINER_PORTS= # Ex : 8081 Optionnel si vous ne voulez pas de adminer soit accessible de l'exterieur.
DB_PORTS= # Ex : 5432 Optionnel si vous ne voulez pas que votre database soit accessible de l'exterieur.
```

Il vous faut également dans le fichier Dockerfile, changer la variable d'environnement **EXPOSE** (Ligne 21) par votre port.

3. Ouvrer un **CMD** dans le root du projet et effectuer la commande :
``` docker-compose up --build```

# Tests du projet

## Tests

Pour lancer les tests d'intégrations du projet : `mvn test`

# Accès au projet en ligne :

Vous pouvez accéder à notre projet directement en ligne ! 

Voici un lien pour y accéder : https://sondage.ronde-lingons.fr/swagger-ui.html
