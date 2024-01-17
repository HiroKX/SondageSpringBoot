package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;

import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured.*;

public class SondageSampleE2E {

    public String generateSondagePostBody(Sondage sondage) {
        return "{\n" +
                "\"sondageId\" : 1,\n" +
                "\"nom\" : \"" + sondage.getNom() + "\",\n" +
                "\"description\" : \"" + sondage.getDescription() + "\",\n" +
                "\"fin\" : \"" + sendDate(sondage.getFin()) + "\",\n" +
                "\"cloture\" : " + sondage.getCloture() + ",\n" +
                "\"createBy\" : "+ sondage.getCreateBy().getParticipantId() +"\n" +
                "}";
    }
}
