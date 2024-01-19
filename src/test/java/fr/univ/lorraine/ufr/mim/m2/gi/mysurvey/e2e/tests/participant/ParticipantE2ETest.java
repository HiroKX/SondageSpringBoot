package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.tests.participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.participant.ParticipantSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
class ParticipantE2ETest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = CrudRestAssured.SRV_BASEURI;
        RestAssured.port = CrudRestAssured.SERVER_PORT;
    }
    @Test
    void participantPOST_GET_GETID_PUTID_DELETEID() {
        // GET WHEN NO DATA IN DB
        Response response = CrudRestAssured.dbGET("/api/participant/");
        assertEquals(404, response.statusCode()); // DEBAT POUR 204 PLUTOT QUE 404

        // TEST POST PARTICIPANT
        Participant participant = new Participant(1L,"Reeves","Keanu");
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        response = CrudRestAssured.dbPOST("/api/participant/", requestBody);
        long createdID = response.jsonPath().getLong("participantId");
        assertEquals(201, response.statusCode());
        assertEquals("Reeves", response.jsonPath().getString("nom"));
        assertEquals("Keanu", response.jsonPath().getString("prenom"));

        // TEST GET ALL
        response = CrudRestAssured.dbGET("/api/participant/");
        assertEquals(200, response.statusCode());
        String expectedString = "[{\"participantId\":" + createdID + ",\"nom\":\"" + participant.getNom() + "\",\"prenom\":\"" + participant.getPrenom() + "\"}]";
        assertEquals(expectedString, response.getBody().print());

        // TEST PUT PARTICIPANT
        participant.setNom("Wick");
        participant.setPrenom("John");
        requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        response = CrudRestAssured.dbPUT("api/participant/"+createdID, requestBody);
        assertEquals(200, response.statusCode());
        assertEquals(createdID, response.jsonPath().getLong("participantId"));
        assertEquals("Wick", response.jsonPath().getString("nom"));
        assertEquals("John", response.jsonPath().getString("prenom"));

        // TEST GET PARTICIPANT ID
        response = CrudRestAssured.dbGET("/api/participant/"+createdID);
        assertEquals(200, response.statusCode());
        assertEquals(createdID, response.jsonPath().getLong("participantId"));
        assertEquals("Wick", response.jsonPath().getString("nom"));
        assertEquals("John", response.jsonPath().getString("prenom"));

        // TEST DELETE PARTICIPANT
        response = CrudRestAssured.dbDELETE("/api/participant/"+createdID);
        assertEquals(204, response.statusCode());

        // TEST GET PARTICIPANT WHEN NO ID MATCH
        response = CrudRestAssured.dbGET("/api/participant/99");
        assertEquals(404, response.statusCode());

        // TEST DELETE PARTICIPANT WHEN NO ID MATCH
        response = CrudRestAssured.dbDELETE("/api/participant/99");
        assertEquals(400, response.statusCode());

        // TEST PUT PARTICIPANT WHEN NO ID MATCH
        response = CrudRestAssured.dbPUT("/api/participant/99", requestBody);
        assertEquals(404, response.statusCode());

        // TEST INCOMPLETE POST PARTICIPANT - no name
        requestBody = "[{\"participantId\":" + ",\"nom\":" + ",\"prenom\":\"" + participant.getPrenom() + "\"}]";
        response = CrudRestAssured.dbPOST("/api/participant/", requestBody);
        assertEquals(400, response.statusCode());
    }
}
