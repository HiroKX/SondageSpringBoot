package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.controller.sondage;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.RestAssured_conf;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.participant.ParticipantSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage.SondageSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;

import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.RestAssured_conf.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
class SondageE2ETest {
    private final static ParticipantSampleE2E participantSample = new ParticipantSampleE2E();
    private final static SondageSampleE2E sondageSample = new SondageSampleE2E();

    @BeforeEach
    void setup() {
        RestAssured.baseURI = RestAssured_conf.SRV_BASEURI;
        RestAssured.port = RestAssured_conf.SERVER_PORT;
    }
    @Test
    void givenParticipant_whenCreatesSondage() {
        // GET ID WHEN NO SONDAGE
        Response response = given()
                .header("accept", "*/*")
                .when()
                .get("api/sondage/99")
                .then()
                .extract().response();
        assertEquals(500, response.statusCode());

        // GET ALL WHEN NO SONDAGE
        response = given()
                .header("accept", "*/*")
                .when()
                .get("/api/sondage/")
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.getBody().print());

        // GET DATE CLOTURE WHEN NO SONDAGE
        response = given()
                .header("accept", "*/*")
                .when()
                .get("api/sondage/99/dates")
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());


        // CREATE THE PARTICIPANT IN DB
        Participant participant = new Participant(1L, "Mortensen", "Viggo");
        String requestBody = participantSample.generateParticipantPOSTBody(participant);
        response = given()
                .header("accept", "*/*")
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/participant/")
                .then()
                .extract().response();
        long createdParticipantID = response.jsonPath().getLong("participantId");
        assertEquals(201, response.statusCode());
        assertEquals(createdParticipantID, response.jsonPath().getLong("participantId"));

        // TEST POST SONDAGE
        participant.setParticipantId(createdParticipantID);
        Sondage sondage = new Sondage(8L,
                "Rey Skywalker est le meilleur personnage de Star Wars",
                "Vous ne pouvez qu être d accord",
                new Date(1735599600),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = sondageSample.generateSondagePostBody(sondage);
        System.out.println(requestBody);
        response = given()
                .header("Content-type", "application/json")
                .header("accept", "*/*")
                .body(requestBody)
                .when()
                .post("/api/sondage/")
                .then()
                .extract().response();
        long createdSondageID = response.jsonPath().getLong("sondageId");
        assertEquals(201, response.statusCode());
        assertEquals(sondage.getNom(), response.jsonPath().getString("nom"));
        assertEquals(sondage.getDescription(), response.jsonPath().getString("description"));
        assertEquals(recieveDate(sondage.getFin()), response.jsonPath().getString("fin"));
        assertEquals(sondage.getCloture(), response.jsonPath().getBoolean("cloture"));
        assertEquals(sondage.getCreateBy().getParticipantId(), response.jsonPath().getLong("createBy"));

        // SUPPRESSION DU PARTICIPANT
        response = given()
                .header("accept", "*/*")
                .when()
                .delete("/api/participant/"+ createdParticipantID)
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());

        // SUPPRESSION DU SONDAGE
        response = given()
                .header("accept", "*/*")
                .when()
                .delete("/api/sondage/" + createdSondageID)
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
    }
}
