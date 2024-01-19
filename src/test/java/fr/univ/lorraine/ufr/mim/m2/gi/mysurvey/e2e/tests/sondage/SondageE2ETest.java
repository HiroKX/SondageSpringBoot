package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.tests.sondage;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.commentaire.CommentaireSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.date_sondage.DateSondageSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.participant.ParticipantSampleE2E;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage.ParticiperSondageSampleE2E;
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
import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage.SondageSampleE2E.futureDate;
import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage.SondageSampleE2E.pastDate;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
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
        Response response = CrudRestAssured.dbGET("api/sondage/99");
        assertEquals(404, response.statusCode());

        // GET ALL WHEN NO SONDAGE
        response = CrudRestAssured.dbGET("api/sondage/");
        assertEquals(404, response.statusCode());

        // GET DATE CLOTURE WHEN NO SONDAGE
        response = CrudRestAssured.dbGET("api/sondage/99/dates");
        assertEquals(404, response.statusCode());

        // CREATE THE PARTICIPANT IN DB
        Participant participant = new Participant(1L, "Mortensen", "Viggo");
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        response = CrudRestAssured.dbPOST("/api/participant/", requestBody);
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
        response = CrudRestAssured.dbPOST("/api/sondage/", requestBody);
        long createdSondageID = response.jsonPath().getLong("sondageId");
        assertEquals(201, response.statusCode());
        assertEquals(sondage.getNom(), response.jsonPath().getString("nom"));
        assertEquals(sondage.getDescription(), response.jsonPath().getString("description"));
        assertEquals(recieveDate(sondage.getFin()), response.jsonPath().getString("fin"));
        assertEquals(sondage.getCloture(), response.jsonPath().getBoolean("cloture"));
        assertEquals(sondage.getCreateBy().getParticipantId(), response.jsonPath().getLong("createBy"));

        // GET SONDAGE ID
        response = CrudRestAssured.dbGET("/api/sondage/"+createdSondageID);
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
                new Date(futureDate.getTime()),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage2);
        response = CrudRestAssured.dbPOST("/api/sondage/", requestBody);
        assertEquals(201, response.statusCode());
        long createdSondageID2 = response.jsonPath().getLong("sondageId");


        // GET ALL SONDAGE
        response = CrudRestAssured.dbGET("/api/sondage/");
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
        response = CrudRestAssured.dbPUT("/api/sondage/"+createdSondageID2, requestBody);
        assertEquals(200, response.statusCode());
        assertEquals(createdSondageID2, response.jsonPath().getLong("sondageId"));
        assertEquals(sondage2.getNom() ,response.jsonPath().getString("nom"));
        assertEquals(sondage2.getDescription(), response.jsonPath().getString("description"));
        assertEquals(recieveDate(sondage2.getFin()), response.jsonPath().getString("fin"));
        assertEquals(sondage2.getCloture(), response.jsonPath().getBoolean("cloture"));


        // SUPPRESSION PARTICIPANT
        response = CrudRestAssured.dbDELETE("/api/participant/"+createdParticipantID);
        assertEquals(204, response.statusCode());

        // SUPPRESSION SONDAGE
        response = CrudRestAssured.dbDELETE("/api/sondage/"+createdSondageID);
        assertEquals(404, response.statusCode());
    }
    @Test
    void testDateSondageOnSondage() {
        Participant participant = new Participant(4L, "BRANSTETT", "Tim");
        Sondage sondage = new Sondage(8L,
                "Aller en cours de Production Logicielle",
                "Il y a la soutenance !",
                new Date(futureDate.getTime()),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        // CREATE PARTICIPANT
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        Response response = CrudRestAssured.dbPOST("/api/participant/", requestBody);
        long createdParticipantID = response.jsonPath().getLong("participantId");
        participant.setParticipantId(createdParticipantID);
        // CREATE SONDAGE
        sondage.setCreateBy(participant);
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage);
        response = CrudRestAssured.dbPOST("/api/sondage/", requestBody);
        long createdSondageID = response.jsonPath().getLong("sondageId");

        // TEST POST DATESONDAGE
        Date fixedDate = new Date(futureDate.getTime()+300000);
        requestBody = DateSondageSampleE2E.generateDateSondagePOSTBody(fixedDate);
        response = CrudRestAssured.dbPOST("/api/datesondage/"+createdSondageID, requestBody);
        assertEquals(201, response.statusCode());

        // TEST GET DATESONDAGE
        long createdDateSondageID = response.jsonPath().getLong("dateSondageId");
        response = CrudRestAssured.dbGET("/api/datesondage/"+createdSondageID);
        assertEquals(200, response.statusCode());
        String expectedString = "[{\"dateSondageId\":"+createdDateSondageID+",\"date\":\""+recieveDate(fixedDate)+"\"}]";
        assertEquals(expectedString, response.getBody().print());

        // TEST POST MULTIPLE DATES
        Date futureFixedDate = new Date(futureDate.getTime());
        requestBody = DateSondageSampleE2E.generateDateSondagePOSTBody(futureFixedDate);
        response = CrudRestAssured.dbPOST("/api/datesondage/"+createdSondageID, requestBody);
        assertEquals(201, response.statusCode());
        long createdDateSondageID2 = response.jsonPath().getLong("dateSondageId");

        // TEST ADD DATE ALREADY EXISTING
        requestBody = DateSondageSampleE2E.generateDateSondagePOSTBody(fixedDate);
        response = CrudRestAssured.dbPOST("/api/datesondage/"+createdSondageID, requestBody);
        assertEquals(400, response.statusCode());

        // TEST ADD DATE IN THE PAST
        requestBody = DateSondageSampleE2E.generateDateSondagePOSTBody(pastDate);
        response = CrudRestAssured.dbPOST("/api/datesondage/"+createdSondageID, requestBody);
        assertEquals(400, response.statusCode());

        // TEST POST DATESONDAGE FAKE SONDAGE
        response = CrudRestAssured.dbPOST("/api/datesondage/999", requestBody);
        assertEquals(400, response.statusCode());

        // TEST SUPPRESSION DE DATESONDAGE
        response = CrudRestAssured.dbDELETE("/api/datesondage/"+createdDateSondageID);
        assertEquals(204, response.statusCode());

        // TEST GET DES DATES DU SONDAGE
        response = CrudRestAssured.dbGET("/api/datesondage/"+createdSondageID);
        expectedString = "[{\"dateSondageId\":"+createdDateSondageID2+",\"date\":\""+recieveDate(futureFixedDate)+"\"}]";
        assertEquals(200, response.statusCode());
        assertEquals(expectedString, response.getBody().print());

        // DELETE PARTICIPANT AND SONDAGE
        response = CrudRestAssured.dbDELETE("/api/participant/"+createdParticipantID);
        assertEquals(204, response.statusCode());
    }
    @Test
    void testVoteSondagePastFinDate() {
        // CREATE PARTICIPANT
        Participant participant = new Participant(1L, "Sarah", "Connor");
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        Response response = CrudRestAssured.dbPOST("/api/participant/", requestBody);
        long createdParticipantID = response.jsonPath().getLong("participantId");
        participant.setParticipantId(createdParticipantID);
        // CREATE SONDAGE BUT WITH PAST DATE
        Sondage sondage = new Sondage(1L,
                "Aller manger au KFC",
                "Ça fait longtemps que j'ai pas mangé un Bucket",
                new Date(pastDate.getTime()),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage);
        response = CrudRestAssured.dbPOST("/api/sondage/", requestBody);
        assertEquals(400, response.statusCode()); //TODO : Devrait être 400
    }

    @Test
    void testNewSondage(){
        // CREATE PARTICIPANT
        Participant participant = new Participant(1L, "Sarah", "Connor");
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        Response response = CrudRestAssured.dbPOST("/api/participant/", requestBody);
        long createdParticipantID = response.jsonPath().getLong("participantId");
        participant.setParticipantId(createdParticipantID);
        // CREATE SONDAGE BUT WITH PAST DATE
        Sondage sondage = new Sondage(1L,
                "Aller manger au KFC",
                "Ça fait longtemps que j'ai pas mangé un Bucket",
                new Date(futureDate.getTime()),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage);
        response = CrudRestAssured.dbPOST("/api/sondage/", requestBody);
        assertEquals(201, response.statusCode());
        long createdSondageID = response.jsonPath().getLong("sondageId");

        // CREATE DATESONDAGE AND ADD IT TO THE SONDAGE
        Date fixedDate = new Date(futureDate.getTime()+1000000);
        requestBody = DateSondageSampleE2E.generateDateSondagePOSTBody(fixedDate);
        response = CrudRestAssured.dbPOST("/api/datesondage/"+createdSondageID, requestBody);
        long createdDateSondage = response.jsonPath().getLong("dateSondageId");
        requestBody = DateSondageSampleE2E.generateDateSondagePOSTBody(fixedDate);
        response = CrudRestAssured.dbPOST("/api/datesondage/"+createdSondageID, requestBody);

        // THE PARTICIPANT TRIES TO PARTICIPATE TO A SONDAGE WITH PAST FIN DATE
        requestBody = ParticiperSondageSampleE2E.generateParticiper(createdParticipantID, Choix.INDISPONIBLE);
        response = CrudRestAssured.dbPOST("/api/participer/"+createdDateSondage, requestBody);
        assertEquals(201, response.statusCode()); //TODO : Devrait être 400

        // DELETE PARTICIPANT AND SONDAGE
        response = CrudRestAssured.dbDELETE("/api/participant/"+createdParticipantID);
    }
    @Test
    void testVoteSondageShouldWork() {
        // CREATE PARTICIPANT
        Participant participant = new Participant(1L, "Lara", "Croft");
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        Response response = CrudRestAssured.dbPOST("/api/participant/", requestBody);
        long createdParticipantID = response.jsonPath().getLong("participantId");
        participant.setParticipantId(createdParticipantID);
        // CREATE SONDAGE BUT WITH PAST DATE
        Sondage sondage = new Sondage(1L,
                "Aller manger au McDonalds",
                "Ça fait longtemps que j'ai pas mangé un Big Mac",
                new Date(futureDate.getTime()+2000000),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage);
        response = CrudRestAssured.dbPOST("/api/sondage/", requestBody);
        long createdSondageID = response.jsonPath().getLong("sondageId");
        sondage.setSondageId(createdSondageID);
        // CREATE DATESONDAGE AND ADD IT TO THE SONDAGE
        Date fixedDate = new Date(futureDate.getTime()+1000000);
        requestBody = DateSondageSampleE2E.generateDateSondagePOSTBody(fixedDate);
        response = CrudRestAssured.dbPOST("/api/datesondage/"+createdSondageID, requestBody);
        long createdDateSondage = response.jsonPath().getLong("dateSondageId");
        requestBody = DateSondageSampleE2E.generateDateSondagePOSTBody(fixedDate);
        response = CrudRestAssured.dbPOST("/api/datesondage/"+createdSondageID, requestBody);
        // THE PARTICIPANT TRIES TO PARTICIPATE TO A SONDAGE
        requestBody = ParticiperSondageSampleE2E.generateParticiper(createdParticipantID, Choix.valueOf(Choix.INDISPONIBLE.name()));
        response = CrudRestAssured.dbPOST("/api/participer/"+createdDateSondage, requestBody);
        assertEquals(201, response.statusCode());
        assertEquals(createdParticipantID, response.jsonPath().getLong("participant"));
        assertEquals(Choix.INDISPONIBLE.name(), response.jsonPath().getString("choix"));
        // DELETE PARTICIPANT AND SONDAGE
        response = CrudRestAssured.dbDELETE("/api/participant/"+createdParticipantID);
    }
    @Test
    void crudCommentaireOnSondage() {
        // CREATE PARTICIPANT
        Participant participant = new Participant(1L, "Ellen", "Ripley");
        String requestBody = ParticipantSampleE2E.generateParticipantPOSTBody(participant);
        Response response = CrudRestAssured.dbPOST("/api/participant/", requestBody);
        long createdParticipantID = response.jsonPath().getLong("participantId");
        participant.setParticipantId(createdParticipantID);
        // CREATE SONDAGE BUT WITH PAST DATE
        Sondage sondage = new Sondage(1L,
                "Aller manger au Burger King",
                "Ça fait longtemps que j'ai pas mangé un Double Steakhouse",
                new Date(futureDate.getTime()+2000000),
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                participant);
        requestBody = SondageSampleE2E.generateSondagePostBody(sondage);
        response = CrudRestAssured.dbPOST("/api/sondage/", requestBody);
        long createdSondageID = response.jsonPath().getLong("sondageId");
        sondage.setSondageId(createdSondageID);
        // CREATE COMMENTAIRE AND ADD ON SONDAGE
        requestBody = CommentaireSampleE2E.generateCommentairePOSTBody("Ah mince je me suis trompé dans la date", createdParticipantID);
        response = CrudRestAssured.dbPOST("/api/commentaire/"+createdSondageID, requestBody);
        assertEquals(201, response.statusCode());
        assertEquals("Ah mince je me suis trompé dans la date", response.jsonPath().getString("commentaire"));
        assertEquals(createdParticipantID, response.jsonPath().getLong("participant"));
        long createdCommentaireID = response.jsonPath().getLong("commentaireId");
        // UPDATE COMMENTAIRE
        requestBody = CommentaireSampleE2E.generateCommentairePOSTBody("Au final c'est la bonne date", createdParticipantID);
        response = CrudRestAssured.dbPUT("/api/commentaire/"+createdCommentaireID, requestBody);
        assertEquals(200, response.statusCode());
        assertEquals("Au final c'est la bonne date", response.jsonPath().getString("commentaire"));
        assertEquals(createdParticipantID, response.jsonPath().getLong("participant"));
        // GET COMMENTAIRE
        response = CrudRestAssured.dbGET("/api/commentaire/"+createdSondageID);
        assertEquals(200, response.statusCode());
        String expectedString = "[{\"commentaireId\":"+createdCommentaireID+",\"commentaire\":\"Au final c'est la bonne date\",\"participant\":"+ createdParticipantID+ "}]";
        assertEquals(expectedString, response.getBody().print());
        // DELETE COMMENTAIRE
        response = CrudRestAssured.dbDELETE("/api/commentaire/"+createdCommentaireID);
        assertEquals(204, response.statusCode());
        // DELETE PARTICIPANT AND SONDAGE
        response = CrudRestAssured.dbDELETE("/api/participant/"+createdParticipantID);
        response = CrudRestAssured.dbDELETE("/api/sondage/"+createdSondageID);
    }
}
