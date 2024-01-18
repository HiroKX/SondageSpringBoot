package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.tests.sondage;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.date_sondage.DateSondageSample;
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
import org.springframework.boot.test.json.JacksonTester;

import java.util.ArrayList;
import java.util.Date;

import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured.*;
import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage.SondageSampleE2E.futureDate;
import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage.SondageSampleE2E.pastDate;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
class SondageE2ETest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = CrudRestAssured.SRV_BASEURI;
        RestAssured.port = CrudRestAssured.SERVER_PORT;
    }
    @Test
    void givenParticipant_whenCreatesSondage() {
        // GET ID WHEN NO SONDAGE
        Response response = CrudRestAssured.getFromDB("api/sondage/99");
        assertEquals(404, response.statusCode());

        // GET ALL WHEN NO SONDAGE
        response = CrudRestAssured.getFromDB("api/sondage/");
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.getBody().print());

        // GET DATE CLOTURE WHEN NO SONDAGE
        response = CrudRestAssured.getFromDB("api/sondage/99/dates");
        assertEquals(404, response.statusCode());

        // CREATE THE PARTICIPANT IN DB
        Participant participant = new Participant(1L, "Mortensen", "Viggo");
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        response = CrudRestAssured.addToDB("/api/participant/", requestBody);
        long createdParticipantID = response.jsonPath().getLong("participantId");
        assertEquals(201, response.statusCode());
        assertEquals(createdParticipantID, response.jsonPath().getLong("participantId"));

        // TEST POST SONDAGE
        participant.setParticipantId(createdParticipantID);
        Sondage sondage = new Sondage(8L,
                "Aller voir Star Wars IX au cinéma",
                "Vous êtes pas obligés",
                new Date(futureDate.getTime()),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage);
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
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage2);
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
        sondage2.setSondageId(createdSondageID2);
        sondage2.setNom("Au final un billard ?");
        sondage2.setDescription("C'est pas les mêmes boules");
        sondage2.setFin(new Date(futureDate.getTime()+10000));
        sondage2.setCloture(false); // A passer à true après le prochain refacto
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage2);
        response = CrudRestAssured.updateEntityFromDB("/api/sondage/"+createdSondageID2, requestBody);
        assertEquals(200, response.statusCode());
        assertEquals(createdSondageID2, response.jsonPath().getLong("sondageId"));
        assertEquals(sondage2.getNom() ,response.jsonPath().getString("nom"));
        assertEquals(sondage2.getDescription(), response.jsonPath().getString("description"));
        assertEquals(recieveDate(sondage2.getFin()), response.jsonPath().getString("fin"));
        assertEquals(sondage2.getCloture(), response.jsonPath().getBoolean("cloture"));


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
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        sondage.setCloture(false);
        sondage.setCreateBy(participant);
        // CREATE PARTICIPANT
        // CREATE SONDAGE
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        Response response = CrudRestAssured.addToDB("/api/participant/", requestBody);
        long createdParticipantID = response.jsonPath().getLong("participantId");
        participant.setParticipantId(createdParticipantID);
        sondage.setCreateBy(participant);
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage);
        response = CrudRestAssured.addToDB("/api/sondage/", requestBody);
        long createdSondageID = response.jsonPath().getLong("sondageId");

        // TEST POST DATESONDAGE
        Date fixedDate = new Date();
        fixedDate.setTime(fixedDate.getTime()+300000);
        requestBody = DateSondageSample.generateDateSondagePOSTBody(fixedDate);
        response = CrudRestAssured.addToDB("/api/datesondage/"+createdSondageID, requestBody);
        assertEquals(201, response.statusCode());

        // TEST GET DATESONDAGE
        long createdDateSondageID = response.jsonPath().getLong("dateSondageId");
        response = CrudRestAssured.getFromDB("/api/datesondage/"+createdSondageID);
        assertEquals(200, response.statusCode());
        String expectedString = "[{\"dateSondageId\":"+createdDateSondageID+",\"date\":\""+recieveDate(fixedDate)+"\"}]";
        assertEquals(expectedString, response.getBody().print());

        // TEST POST MULTIPLE DATES
        Date futureFixedDate = new Date(futureDate.getTime());
        requestBody = DateSondageSample.generateDateSondagePOSTBody(futureFixedDate);
        response = CrudRestAssured.addToDB("/api/datesondage/"+createdSondageID, requestBody);
        assertEquals(201, response.statusCode());
        long createdDateSondageID2 = response.jsonPath().getLong("dateSondageId");

        // TEST ADD DATE ALREADY EXISTING
        requestBody = DateSondageSample.generateDateSondagePOSTBody(fixedDate);
        response = CrudRestAssured.addToDB("/api/datesondage/"+createdSondageID, requestBody);
        assertEquals(400, response.statusCode());

        // TEST ADD DATE IN THE PAST
        //requestBody = DateSondageSample.generateDateSondagePOSTBody(pastDate);
        //response = CrudRestAssured.addToDB("/api/datesondage/"+createdSondageID, requestBody);
        //assertEquals(201, response.statusCode()); // TODO : Repassage en 400
        //assertEquals("Vérifier la date", response.getBody().print());

        // TEST POST DATESONDAGE FAKE SONDAGE
        response = CrudRestAssured.addToDB("/api/datesondage/999", requestBody);
        assertEquals(400, response.statusCode());

        // TEST SUPPRESSION DE DATESONDAGE
        response = CrudRestAssured.removeFromDB("/api/datesondage/"+createdDateSondageID);
        assertEquals(200, response.statusCode());

        // TEST GET DES DATES DU SONDAGE
        response = CrudRestAssured.getFromDB("/api/datesondage/"+createdSondageID);
        expectedString = "[{\"dateSondageId\":"+createdDateSondageID2+",\"date\":\""+recieveDate(futureFixedDate)+"\"}]";
        assertEquals(200, response.statusCode());
        assertEquals(expectedString, response.getBody().print());

        // DELETE PARTICIPANT AND SONDAGE
        response = CrudRestAssured.removeFromDB("/api/participant/"+createdParticipantID);
        response = CrudRestAssured.removeFromDB("/api/sondage/"+createdSondageID);

    }
}
