package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.services.participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.participant.DataSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
class ParticipantE2ETest {

    private final static DataSampleE2E dataSample = new DataSampleE2E();
    private Long createdID = 1L;

    @BeforeEach
    void setup() {
        RestAssured.baseURI="http://localhost";
        RestAssured.port=666;
    }
    @Test
    void participantPOST_GET_GETID_PUTID_DELETEID() {
        // TEST DU GET QUAND PAS DE PARTICIPANTS
        Response response = given()
                .header("accept", "*/*")
                .when()
                .get("/api/participant/")
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.getBody().print());

        // TEST DU POST PARTICIPANT
        Participant participant = new Participant(1L,"Reeves","Keanu");
        String requestBody = dataSample.generateParticipantPOSTBody(participant);
        response = given()
                .header("Content-type", "application/json")
                .header("accept", "*/*")
                .body(requestBody)
                .when()
                .post("/api/participant/")
                .then()
                .extract().response();
        createdID = response.jsonPath().getLong("participantId");
        assertEquals(201, response.statusCode());
        assertEquals("Reeves", response.jsonPath().getString("nom"));
        assertEquals("Keanu", response.jsonPath().getString("prenom"));

        // TEST DU GET ALL
        response = given()
                .header("accept", "*/*")
                .when()
                .get("/api/participant/")
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
        String expectedString = "[{\"participantId\":" + createdID + ",\"nom\":\"" + participant.getNom() + "\",\"prenom\":\"" + participant.getPrenom() + "\"}]";
        assertEquals(expectedString, response.getBody().print());

        // TEST DU PUT PARTICIPANT
        participant.setNom("Wick");
        participant.setPrenom("John");
        requestBody = dataSample.generateParticipantPOSTBody(participant);
        response = given()
                .header("accept", "*/*")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("api/participant/"+createdID)
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());
        assertEquals(createdID, response.jsonPath().getLong("participantId"));
        assertEquals("Wick", response.jsonPath().getString("nom"));
        assertEquals("John", response.jsonPath().getString("prenom"));


        // TEST DU GET PARTICIPANT
        response = given()
                .header("accept", "*/*")
                .when()
                .get("/api/participant/"+createdID)
                .then()
                .extract().response();
        assertEquals(createdID, response.jsonPath().getLong("participantId"));
        assertEquals("Wick", response.jsonPath().getString("nom"));
        assertEquals("John", response.jsonPath().getString("prenom"));

        // TEST DU DELETE PARTICIPANT
        response = given()
                .header("accept", "*/*")
                .when()
                .delete("/api/participant/"+createdID)
                .then()
                .extract().response();
        assertEquals(200, response.statusCode());

    }
}