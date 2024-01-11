# Setup du projet
## Database
Afin de faciliter l'utilisation de l'application, nous avons mis en place un conteneur docker via un fichier yml contenu dans le dossier db.
Ce fichier va instancier un conteneur avec une base de données **PostgreSQL** ainsi qu'**adminer**, un gestionnaire de database simple en php.

Pour déployer ce conteneur : 
* Installer docker.
* Allez dans le fichier `/db/.env` et éditez la configuration : 
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

Créez un fichier `application-local.properties` et éditez sa configuration : 

```
server.port = [Au choix]
spring.datasource.url = jdbc:postgresql://localhost:[DB_PORTS]/[POSTGRES_DB]
spring.datasource.username = [POSTGRES_USER]
spring.datasource.password = [POSTGRES_PASSWORD]
spring.datasource.driver-class-name = org.postgresql.Driver
spring.jpa.show-sql = true
spring.jpa.hibernate.ddl-auto = update
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
spring.main.banner-mode = off
```

**/!\ Pour l'url, seul le port de sortie doit être indiqué et non le couple**

# Lancement du projet

## Tests

Pour lancer les tests d'intégrations du projet : `mvn test`

## Run

Une fois le projet démarré : 

* Accès à l'api Swagger : http://localhost:8080/swagger-ui.html (dépend du port que vous avez set)

* Documentation de l'api : http://localhost:8080/v3/api-docs (dépend du port que vous avez set)

* Interface adminer pour consulter la DB : http://localhost:8080/ (dépend du port que vous avez set)
