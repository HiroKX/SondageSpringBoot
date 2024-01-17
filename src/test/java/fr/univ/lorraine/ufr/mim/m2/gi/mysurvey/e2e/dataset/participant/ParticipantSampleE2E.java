package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.participant;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;

 public class ParticipantSampleE2E {

    // Participants
    static String[] prenoms = {"Benjamin", "Lara", "Indiana", "Nathan", "Rick"};
    static String[] noms = {"Gates", "Croft", "Jones", "Drake", "O'Connell"};
    static int size = prenoms.length;
    public String generateParticipantPOSTBody (int index) {
        return "{\n" +
                "\"participantId\" : 1,\n" +
                "\"nom\" : \"" + noms[index] + "\",\n" +
                "\"prenom\" : \"" + prenoms[index] + "\"\n" +
                "}";
    }

    public String generateParticipantPOSTBody(Participant participant) {
        return "{\n" +
                "\"participantId\" : " + participant.getParticipantId() + ",\n" +
                "\"nom\" : \"" + participant.getNom() + "\",\n" +
                "\"prenom\" : \"" + participant.getPrenom() + "\"\n" +
                "}";
    }
    public String generateParticipantPUTBody (int index, Long id) {
        return "{\n" +
                "\"participantId\": " + id + ",\n" +
                "\"nom\" : \"" + noms[index] + "\",\n" +
                "\"prenom\" : \"" + prenoms[index] + "\"\n" +
                "}";
    }

    public String generateParticipantPUTBody (Participant participant, Long id) {
        return "{\n" +
                "\"participantId\": " + id + ",\n" +
                "\"nom\" : \"" + participant.getNom() + "\",\n" +
                "\"prenom\" : \"" + participant.getPrenom() + "\"\n" +
                "}";
    }


}