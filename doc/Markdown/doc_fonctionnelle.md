# Setup du projet

## Local
### Prérequis 
* **Base de données**


Nous vous conseillons d'utiliser une base de données [PostGreSQL](https://www.postgresql.org/).

* **Java 19**


Téléchargeable [ici](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html).

### Installation 
Dans le fichier [application-local.properties](https://github.com/HiroKX/SondageSpringBoot/blob/56-fix-du-readme/src/test/resources/application.properties), il vous faut renseigner les variables de connexion propres à votre base de données.
Il s'agit du port, du nom d'utilisateur, du mot de passe et du nom de la base de données.

Une fois que tout ceci est fait, il vous suffit de lancer le programme [MySurveyApplication](https://github.com/HiroKX/SondageSpringBoot/blob/56-fix-du-readme/src/main/java/fr/univ/lorraine/ufr/mim/m2/gi/mysurvey/MySurveyApplication.java).

## Docker
Nous recommandons l'utilisation de Docker car il permet de contenir votre application dans un environnement qui n'atteint pas votre machine.
Il est tout de même recommandé d'utiliser Java 19 si vous voulez modifier le projet.

### Prérequis
Afin de pouvoir lancer correctement ce projet de cette façon, il vous faut :

1. Un environnement Docker sur votre machine

Et c'est tout !

### Installation

Nous avons mis en place un conteneur docker déployant : 
* Une base de donnée PostgreSQL
* Une interface d'administration de la base de donnée Adminer
* L'application dans un environnement Ubuntu, afin qu'elle soit build et exécutée

Il est possible d'augmenter la sécurité de votre application lors du déploiement du conteneur en refusant les connexions à la base de données pour chaque requête provenant de l'extérieur du conteneur.
Pour cela, il vous suffit de retirer [ces lignes](https://github.com/HiroKX/SondageSpringBoot/blob/develop/docker-compose.yml#L25-L26) de votre docker-compose.ml.

Pour déployer le conteneur : 
1. Installez docker.
2. Créez un fichier .env dans le root du projet et éditez le fichier de configuration suivant : 
```bash
SPRING_PROFILES_ACTIVE=local
DB_USERNAME= # Ex : postgres
DB_PASSWORD= # Ex : admin
DB_NAME= # Ex : MySurvey
DB_URL="jdbc:postgresql://db:5432"
SERVER_PORT= # Ex : 8090
ADMINER_PORTS= # Ex : 8081 Optionnel si vous ne souhaitez pas que votre interface Adminer soit accessible de l'extérieur.
DB_PORTS= # Ex : 5432 Optionnel si vous ne souhaitez pas que votre base de données soit accessible de l'extérieur.
```
> - DB_USERNAME : Le login avec lequel vous accèderez à la base.
>  - DB_PASSWORD : Le mot de passe avec lequel vous accèderez à la base.
> - DB_NAME : Le nom de la base.
> - ADMINER_PORTS : Le port d'entrée de l'interface Adminer.
> - DB_PORTS : Le port d'entrée de la base PostGreSQL.
> - SERVER_PORT : Le port d'entrée du serveur.

3. Il vous faut également modifier la variable d'environnement **EXPOSE** (Ligne 21) dans le fichier Dockerfile en y affectant votre **SERVER_PORT**.

4. Ouvrez un **CMD** dans le root du projet et exécutez la commande suivante :
```docker-compose up --build```.


# Tests du projet

Pour lancer les tests du projet, utilisez la commande : `mvn test`.

# Accès au projet

Pour lancer le projet, il faut exécuter la classe compilée ``MySurveyApplication`` après avoir installé les dépendances Maven.

- Accès à l'api Swagger : **http://localhost/:{SERVER_PORT}/swagger-ui.html**    _(SERVER_PORT que vous avez set dans .env)_
- Documentation de l'api : **http://localhost/:{SERVER_PORT}/v3/api-docs**    _(SERVER_PORT que vous avez set dans .env)_
- Interface Adminer pour consulter la DB : **http://localhost/:{ADMINER_PORTS}**    _(ADMINER_PORTS que vous avez set dans .env)_

# Accès en ligne

Voici un lien pour y accéder : 
* Swagger : https://sondage.ronde-lingons.fr/swagger-ui.html
* Documentation : https://sondage.ronde-lingons.fr/v3/api-docs