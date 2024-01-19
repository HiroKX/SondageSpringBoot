[![Test Coverage](https://github.com/HiroKX/SondageSpringBoot/blob/gh-pages/jacoco/jacoco.svg)](https://hirokx.github.io/SondageSpringBoot/jacoco)

# Setup et lancement du projet
Afin de faciliter l'utilisation de l'application, nous avons mis en place un conteneur docker.
Vous devrez créer un fichier ".env" (plus d'informations dans la partie Docker) qui vous permettra d'instancier un conteneur hébergeant une base de données **PostGreSQL**, une interface **Adminer** (qui est un gestionnaire de database simple en php), ainsi que l'application entière.

Une fois le projet démarré sur un environnement non déployé  : 

* Accès à l'api Swagger : http://localhost:{ VOTRE_SERVER_PORT }/swagger-ui.html (dépend du SERVER_PORT que vous avez set)

* Accès à la documentation de l'api : http://localhost:{ VOTRE_SERVER_PORT }/v3/api-docs (dépend du SERVER_PORT que vous avez set)
  
* Accès à l'interface Adminer pour consulter la DB : http://localhost:{ VOTRE_SERVER_PORT }/ (dépend du SERVER_PORT que vous avez set)

## Local
### Prérequis :
* Base de données
Nous vous conseillons d'utiliser une base de données [PostGreSQL](https://www.postgresql.org/)

* Java 19
Téléchargeable [ici](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)

### Installation : 
Dans le fichier [application-local.properties](https://github.com/HiroKX/SondageSpringBoot/blob/56-fix-du-readme/src/test/resources/application.properties), il vous faut renseigner les variables de connexion propres à votre base de donnée.
Il s'agit du port, du nom d'utilisateur, du mot de passe et du nom de la base de donnée.

Une fois que tout ceci est fait, il vous suffit de lancer le programme [MySurveyApplication](https://github.com/HiroKX/SondageSpringBoot/blob/56-fix-du-readme/src/main/java/fr/univ/lorraine/ufr/mim/m2/gi/mysurvey/MySurveyApplication.java).

## Docker
Nous recommandons l'utilisation de Docker car il permet de contenir votre application dans un environnement qui n'atteint pas votre machine.
Il est tout de même recommander d'utiliser JAVA19 si vous voulez modifier le projet.

### Prérequis
Afin de pouvoir lancer correctement ce projet de cette façon, il vous faut :

1. Un environnement Docker sur votre machine

Et c'est tout !

### Installation

Il est possible d'augmenter la sécurité de votre application lors du déploiement du conteneur en refusant les connexions à la base de donnée pour chaque requête provenant de l'exterieur du conteneur.
Pour ce faire, il suffit de retirer ces [ligne](https://github.com/HiroKX/SondageSpringBoot/blob/develop/docker-compose.yml#L25-L26) dans votre docker-compose.ml.

Pour déployer ce conteneur : 
1. Installez docker.
2. Créez un fichier .env dans le root du projet et éditez la configuration : 
```bash
SPRING_PROFILES_ACTIVE=local
DB_USERNAME= # Ex : postgres
DB_PASSWORD= # Ex : admin
DB_NAME= # Ex : MySurvey
DB_URL="jdbc:postgresql://db:5432"
SERVER_PORT= # Ex : 8090
ADMINER_PORTS= # Ex : 8081 Optionnel si vous ne souhaitez pas que votre interface adminer soit accessible de l'exterieur.
DB_PORTS= # Ex : 5432 Optionnel si vous ne souhaitez pas que votre database soit accessible de l'exterieur.
```

Il vous faut également, dans le fichier Dockerfile, modifier la variable d'environnement **EXPOSE** [ici](https://github.com/HiroKX/SondageSpringBoot/blob/develop/Dockerfile#L21) en y affectant votre SERVER_PORT.

3. Ouvrez un **CMD** dans le root du projet et exécutez la commande suivante :
``` docker-compose up --build```

# Tests du projet

## Tests

Pour lancer les tests unitaires du projet, il vous suffit d'exécuter la commande `mvn test`. Vous pouvez également retrouver nos rapports de tests JaCoCo [ici](https://hirokx.github.io/SondageSpringBoot/jacoco) et nos rapports de tests Pitest [ici](https://hirokx.github.io/SondageSpringBoot/pitest). Ces rapports se rapportent à la dernière version de notre application.

# Accès au projet en ligne :

Vous pouvez accéder à notre projet directement en ligne, en cliquant [ici](https://sondage.ronde-lingons.fr/swagger-ui.html) ! 
