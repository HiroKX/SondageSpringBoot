package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.commentaire;

public class CommentaireSampleE2E {
    public static String generateCommentairePOSTBody(String message, long participantId) {
        return "{\n" +
                "\"commentaire\" : \"" + message + "\",\n" +
                "\"participant\" : " + participantId + "\n" +
                "}";
    }
}
