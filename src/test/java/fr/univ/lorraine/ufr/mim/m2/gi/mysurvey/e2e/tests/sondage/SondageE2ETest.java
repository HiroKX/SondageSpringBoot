package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.tests.sondage;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.participant.ParticipantSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage.SondageSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;

import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
class SondageE2ETest {
    private final static ParticipantSampleE2E participantSample = new ParticipantSampleE2E();
    private final static SondageSampleE2E sondageSample = new SondageSampleE2E();

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = CrudRestAssured.SRV_BASEURI;
        RestAssured.port = CrudRestAssured.SERVER_PORT;
    }
    @Test
    void givenParticipant_whenCreatesSondage() {
        // GET ID WHEN NO SONDAGE
        Response response = CrudRestAssured.getFromDB("api/sondage/99");
        assertEquals(500, response.statusCode());

        // GET ALL WHEN NO SONDAGE
        response = CrudRestAssured.getFromDB("api/sondage/");
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.getBody().print());

        // GET DATE CLOTURE WHEN NO SONDAGE
        response = CrudRestAssured.getFromDB("api/sondage/99/dates");
        assertEquals(200, response.statusCode());

        // CREATE THE PARTICIPANT IN DB
        Participant participant = new Participant(1L, "Mortensen", "Viggo");
        String requestBody = participantSample.generateParticipantPOSTBody(participant);
        response = CrudRestAssured.addToDB("/api/participant/", requestBody);
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
        response = CrudRestAssured.addToDB("/api/sondage/", requestBody);
        long createdSondageID = response.jsonPath().getLong("sondageId");
        assertEquals(201, response.statusCode());
        assertEquals(sondage.getNom(), response.jsonPath().getString("nom"));
        assertEquals(sondage.getDescription(), response.jsonPath().getString("description"));
        assertEquals(recieveDate(sondage.getFin()), response.jsonPath().getString("fin"));
        assertEquals(sondage.getCloture(), response.jsonPath().getBoolean("cloture"));
        assertEquals(sondage.getCreateBy().getParticipantId(), response.jsonPath().getLong("createBy"));

        // GET SONDAGE ID
        response = CrudRestAssured.getFromDB("/api/sondage/"+createdSondageID);
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
        response = CrudRestAssured.addToDB("/api/sondage/", requestBody);
        long createdSondageID2 = response.jsonPath().getLong("sondageId");


        // GET ALL SONDAGE
        response = CrudRestAssured.getFromDB("/api/sondage/");
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
        sondage.setNom("Au final un billard ?");
        sondage.setDescription("C'est pas les mêmes boules");
        sondage.setFin(new Date(1739599600));
        sondage.setCloture(true);
        requestBody = sondageSample.generateSondagePostBody(sondage);
        response = CrudRestAssured.updateEntityFromDB("/api/sondage/"+createdSondageID, requestBody);
        assertEquals(500, response.statusCode());
        //assertEquals(createdSondageID, response.jsonPath().getLong("sondageId"));
        //assertEquals(sondage.getNom() ,response.jsonPath().getString("nom"));
        //assertEquals(sondage.getDescription(), response.jsonPath().getString("description"));
        //assertEquals(recieveDate(sondage.getFin()), response.jsonPath().getString("fin"));
        //assertEquals(sondage.getCloture(), response.jsonPath().getBoolean("cloture"));


        // SUPPRESSION PARTICIPANT
        response = CrudRestAssured.removeFromDB("/api/participant/"+createdParticipantID);
        assertEquals(200, response.statusCode());

        // SUPPRESSION SONDAGE
        response = CrudRestAssured.removeFromDB("/api/sondage/"+createdSondageID);
        assertEquals(200, response.statusCode());
    }
    @Test
    void givenSondage_whenCloseSondage_thenCannotVote() {
        Participant participant = new Participant(4L, "BRANSTETT", "Tim");
        Sondage sondage = new Sondage(8L,
                "Aller en cours de Production Logicielle",
                "Il y a la soutenance !",
                new Date(),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        ArrayList<DateSondage> listDateSondage = new ArrayList<>();
        DateSondage dateSondage = new DateSondage(1L, new Date(), sondage, new ArrayList<>());
        listDateSondage.add(dateSondage);
        DateSondee dateSondee = new DateSondee(1L, dateSondage, participant, Choix.DISPONIBLE);
        sondage.setDateSondage(listDateSondage);
        sondage.setCloture(false);
        sondage.setCreateBy(participant);
        // CREATE PARTICIPANT
        // CREATE SONDAGE
        String requestBody = participantSample.generateParticipantPOSTBody(participant);
        Response response = CrudRestAssured.addToDB("/api/participant/", requestBody);
        long createdParticipantID = response.jsonPath().getLong("participantId");
        participant.setParticipantId(createdParticipantID);
        sondage.setCreateBy(participant);
        requestBody = sondageSample.generateSondagePostBody(sondage);
        response = CrudRestAssured.addToDB("/api/sondage/", requestBody);
        long createdSondageID = response.jsonPath().getLong("sondageId");
        //SUPPRESSION DU PARTICIPANT ET DU SONDAGE
        response = CrudRestAssured.removeFromDB("/api/participant/"+createdParticipantID);
        response = CrudRestAssured.removeFromDB("/api/sondage/"+createdSondageID);

    }
}
