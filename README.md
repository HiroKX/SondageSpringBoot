# Setup du projet
## Database
Afin de faciliter l'utilisation de l'application, nous avons mis en place un conteneur docker via un fichier yml contenu dans le dossier db.
Ce fichier va instancier un conteneur avec une base de données **PostgreSQL** ainsi qu'**adminer**, un gestionnaire de database simple en php.

Pour déployer ce conteneur : 
* Installer docker.
* Aller dans le fichier `/db/.env` et éditez la configuration : 
```
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=
DB_PORTS=
ADMINER_PORTS=
```
Configuration des ports par défaut :
* Le serveur **adminer** : ```8080:8080```
* La base **postgreSQL** : ```5432:5432```

**/!\ Les ports sont un couple de deux ports XXXX:YYYY avec XXXX le port de sortie du conteneur et YYYY le port d'entrée du conteneur**

Les champs sont libres mais il faudra faire correspondre les valeurs dans la prochaine étape, les champs à changer seront indiqués avec les noms de variables ci-dessus

* Ouvrez un **CMD** dans le dossier **db/** et effectuez la commande : 
``` docker-compose up -d```

## Configuration locale

Créez un fichier `.env` et éditez sa configuration (Exemple de configuration) : 

```
SPRING_PROFILES_ACTIVE=local
DB_USERNAME=post
DB_PASSWORD=admin
DB_NAME=MySurvey
DB_URL="jdbc:postgresql://db:5432/"
ADMINER_PORTS=8081
DB_PORTS=5432
SERVER_PORT=8090
```

# Lancement du projet

## Tests

Pour lancer les tests d'intégrations du projet : `mvn test`

## Run

Une fois le projet démarré : 

* Accès à l'api Swagger : http://localhost:8080/swagger-ui.html (dépend du port que vous avez set)

* Documentation de l'api : http://localhost:8080/v3/api-docs (dépend du port que vous avez set)

* Interface adminer pour consulter la DB : http://localhost:8080/ (dépend du port que vous avez set)
