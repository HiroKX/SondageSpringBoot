package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.commentaire;

import java.util.Date;

import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured.sendDate;

public class CommentaireSampleE2E {
    public static String generateCommentairePOSTBody(String message, long participantId) {
        return "{\n" +
                "\"commentaire\" : \"" + message + "\",\n" +
                "\"participant\" : " + participantId + "\n" +
                "}";
    }
}
