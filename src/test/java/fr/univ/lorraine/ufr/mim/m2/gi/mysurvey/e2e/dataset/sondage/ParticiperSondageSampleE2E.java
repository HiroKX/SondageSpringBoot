package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Choix;

public class ParticiperSondageSampleE2E {
    public static String generateParticiper(long participantId, Choix choix) {
        return "{\n" +
                "\"participant\" : " + participantId + ",\n" +
                "\"choix\" : \""+ choix +"\"\n" +
                "}";
    }
}
