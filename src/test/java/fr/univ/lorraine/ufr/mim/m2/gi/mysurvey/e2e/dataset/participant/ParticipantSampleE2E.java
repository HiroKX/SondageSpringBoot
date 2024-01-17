package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.participant;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ParticipantSampleE2E {

    public String generateParticipantPOSTBody(Participant participant) {
        return "{\n" +
                "\"participantId\" : " + participant.getParticipantId() + ",\n" +
                "\"nom\" : \"" + participant.getNom() + "\",\n" +
                "\"prenom\" : \"" + participant.getPrenom() + "\"\n" +
                "}";
    }
}
