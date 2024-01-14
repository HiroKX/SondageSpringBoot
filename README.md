# Setup du projet
Afin de faciliter l'utilisation de l'application, nous avons mis en place un conteneur docker via 
Ce fichier va instancier un conteneur avec une base de données **PostgreSQL**, **adminer**, un gestionnaire de database simple en php et héberger l'application entière.

Pour déployer ce conteneur : 
* Installer docker.
* Créer un fichier .env dans le root du projet et éditer la configuration : 
```
SPRING_PROFILES_ACTIVE= local / test
DB_USERNAME=
DB_PASSWORD=
DB_NAME=
DB_URL="jdbc:postgresql://db:5432/"
ADMINER_PORTS=
DB_PORTS=
SERVER_PORT=
```

* Ouvrer un **CMD** dans le root du projet et effectuer la commande : 
``` docker-compose up -d --build```

## Configuration locale

Editer la configuration du fichier .env (Exemple de configuration) : 

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

* Accès à l'api Swagger : http://localhost:8080/swagger-ui.html (dépend du SERVER_PORT que vous avez set)

* Documentation de l'api : http://localhost:8080/v3/api-docs (dépend du port SERVER_PORT que vous avez set)

* Interface adminer pour consulter la DB : http://localhost:8080/ (dépend du port SERVER_PORT vous avez set)
