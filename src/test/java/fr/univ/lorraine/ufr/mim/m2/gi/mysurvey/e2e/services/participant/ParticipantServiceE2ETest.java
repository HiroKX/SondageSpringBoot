package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.services.participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.servlet.http.Part;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfiguration
class ParticipantServiceE2ETest {

    @Autowired
    private ParticipantService service;

    @BeforeEach
    void setup() {
        RestAssured.baseURI="http://localhost";
        RestAssured.port=666;
    }

    @Autowired
    private ParticipantService service;

    @BeforeEach
    void setup() {
        RestAssured.baseURI="http://localhost";
        RestAssured.port=666;
    }

    @Test
    void givenLocalParticipant_whenPutParticipant_thenResponse201andCorrectName() {
        String requestBody = "{\n" +
                "  \"participantId\": \"1\",\n" +
                "  \"nom\": \"Reeves\",\n" +
                "  \"prenom\": \"Keanu\" \n}";
        Response response = given()
                .header("Content-type", "application/json")
                .header("accept", "*/*")
                .body(requestBody)
                .when()
                .post("/api/participant/")
                .then()
                .extract().response();

        assertEquals(201, response.statusCode());
        assertEquals("Reeves", response.jsonPath().getString("nom"));
        assertEquals("Keanu", response.jsonPath().getString("prenom"));
        cleanParticipantsDB();
    }
    @Test
    void whenGetParticipant_thenResponse200andCorrectObject() {
        Response response = given()
                .header("accept", "*/*")
                .when()
                .get("/api/participant/")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());
    }

    @Test
    void givenLocalParticipant_whenDeleteParticipant_thenResponse200() {
        Participant participant = new Participant(1L, "Tom", "Hanks");
        service.create(participant);
        Optional<Participant> getCreatedParticipant = service.getAll().stream().filter(findPrenom -> findPrenom.getPrenom().equals(participant.getPrenom())).findFirst();
        assertTrue(getCreatedParticipant.isPresent());
        Response response = given()
                .header("accept", "*/*")
                .pathParams("id", getCreatedParticipant.get().getParticipantId())
                .when()
                .delete("/api/participant/{id}")
                .then()
                .extract().response();

        assertEquals(200, response.statusCode());
    }
    void cleanParticipantsDB() {
        List<Participant> participants = service.getAll();
        participants.forEach(participant -> service.delete(participant.getParticipantId()));
        participants.clear();
    }

}
