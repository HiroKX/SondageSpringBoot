package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.tests.sondage;

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
                "Aller voir Star Wars IX au cinéma",
                "Vous êtes pas obligés",
                new Date(1735599600),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = sondageSample.generateSondagePostBody(sondage);
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

        // GET SONDAGE ID
        response = given()
                .header("accept", "*/*")
                .when()
                .get("/api/sondage/"+ createdSondageID)
                .then()
                .extract().response();
        assertEquals(createdSondageID, response.jsonPath().getLong("sondageId"));
        assertEquals(sondage.getNom(), response.jsonPath().getString("nom"));
        assertEquals(sondage.getDescription(), response.jsonPath().getString("description"));
        assertEquals(recieveDate(sondage.getFin()), response.jsonPath().getString("fin"));
        assertEquals(sondage.getCloture(), response.jsonPath().getBoolean("cloture"));
        assertEquals(sondage.getCreateBy().getParticipantId(), response.jsonPath().getLong("createBy"));

        // ADD ANOTHER SONDAGE
        Sondage sondage2 = new Sondage(4L,
                "P'tit bowling",
                "Allez venez les copains",
                new Date(),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = sondageSample.generateSondagePostBody(sondage2);
        response = given()
                .header("Content-type", "application/json")
                .header("accept", "*/*")
                .body(requestBody)
                .when()
                .post("/api/sondage/")
                .then()
                .extract().response();
        long createdSondageID2 = response.jsonPath().getLong("sondageId");


        // GET ALL SONDAGE
        response = given()
                    .header("accept", "*/*")
                    .when()
                    .get("/api/sondage/")
                    .then()
                    .extract().response();
        String expectedString = "[{\"sondageId\":" + createdSondageID + "," +
                "\"nom\":\"" + sondage.getNom() + "\"," +
                "\"description\":\"" + sondage.getDescription() + "\"," +
                "\"fin\":\"" + recieveDate(sondage.getFin()) + "\"," +
                "\"cloture\":" + sondage.getCloture() + "," +
                "\"createBy\":" + sondage.getCreateBy().getParticipantId() + "}," +
                "{\"sondageId\":" + createdSondageID2 + "," +
                "\"nom\":\"" + sondage2.getNom() + "\"," +
                "\"description\":\"" + sondage2.getDescription() + "\"," +
                "\"fin\":\"" + recieveDate(sondage2.getFin()) + "\"," +
                "\"cloture\":" + sondage2.getCloture() + "," +
                "\"createBy\":" + sondage2.getCreateBy().getParticipantId() + "}]";
        assertEquals(200, response.statusCode());
        assertEquals(expectedString, response.getBody().print());




        // PUT SONDAGE ID
        sondage.setNom("Au final elle est nulle non ?");
        sondage.setDescription("Le personnage est quand même bien mal fait");
        sondage.setFin(new Date(1739599600));
        sondage.setCloture(true);
        requestBody = sondageSample.generateSondagePostBody(sondage);
        response = given()
                .header("accept", "*/*")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("api/sondage/"+ createdSondageID)
                .then()
                .extract().response();
        assertEquals(500, response.statusCode());
        //assertEquals(createdSondageID, response.jsonPath().getLong("sondageId"));
        //assertEquals(sondage.getNom() ,response.jsonPath().getString("nom"));
        //assertEquals(sondage.getDescription(), response.jsonPath().getString("description"));
        //assertEquals(recieveDate(sondage.getFin()), response.jsonPath().getString("fin"));
        //assertEquals(sondage.getCloture(), response.jsonPath().getBoolean("cloture"));


        // SUPPRESSION PARTICIPANT
        response = given()
                .header("accept", "*/*")
                .when()
                .delete("/api/participant/"+ createdParticipantID)
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());

        // SUPPRESSION SONDAGE
        response = given()
                .header("accept", "*/*")
                .when()
                .delete("/api/sondage/" + createdSondageID)
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
    }
}
