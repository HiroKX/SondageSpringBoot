# Setup du projet
# Prérequis
## Database
Afin de faciliter l'utilisation de l'application, nous avons mis en place un conteneur docker via un fichier yml contenu dans le dossier db.
Ce fichier va instancier un conteneur avec une base de données **PostgreSQL** ainsi qu'**adminer**, un gestionnaire de database simple en php.

Pour déployer ce conteneur : 
* Installer docker.
* Ouvrez un **CMD** dans le dossier **db/** . Effectuez la commande : 
``` docker-compose up -d```

Vous pouvez, si vous le souhaitez, changer les ports d'accès des différentes applications du conteneur.

Configuration des ports par défaut :  
* Le serveur **adminer** : ```8080```
* La base **postgreSQL** : ```5432```

## Configuration locale

Créez un fichier `application-local.properties`

```
server.port = 8081
spring.datasource.url = jdbc:postgresql://localhost:5432/MySurvey
spring.datasource.username = username
spring.datasource.password = password
spring.datasource.driver-class-name = org.postgresql.Driver
spring.jpa.show-sql = true
spring.jpa.hibernate.ddl-auto = update
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
spring.main.banner-mode = off
```

Les champs `server.port`, `spring.datasource.url`, `spring.datasource-username` et `spring.datasource.password` sont modifiables, mais les trois 
dernières sont liés à la configuration postgres du conteneur donc pensez bien à les modifier dedans avant de compose à nouveau.

# Lancement du projet

## Tests

Pour lancer les tests d'intégrations du projet : `mvn test`

## Run

Une fois le projet démarré : 

* Accès à l'api Swagger : http://localhost:8081/swagger-ui.html (dépend du port que vous avez set)

* Documentation de l'api : http://localhost:8080/v3/api-docs (dépend du port que vous avez set)

* Interface adminer pour consulter la DB : http://localhost:8080/ (dépend du port que vous avez set)
