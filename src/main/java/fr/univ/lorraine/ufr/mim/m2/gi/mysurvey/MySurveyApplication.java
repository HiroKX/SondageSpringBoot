package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.servers.Server ;
@SpringBootApplication
@OpenAPIDefinition(
        info=@Info(title="Projet de production logicielle", version="1.0", description="API pour le projet de production logicielle"),
        externalDocs = @ExternalDocumentation(description = "Document accessible depuis le repository Github", url = "https://github.com/HiroKX/SondageSpringBoot/tree/master/doc"),
        servers =   {@Server(url = "https://sondage.hirokx.dev/", description = "Serveur sur la branche main"),
                @Server(url = "https://sondage.ronde-lingons.fr/", description = "Serveur sur la branche develop"),
                @Server(url = "http://localhost:{port}", description = "Votre serveur avec le bon port")})
public class MySurveyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MySurveyApplication.class, args);
    }
}


