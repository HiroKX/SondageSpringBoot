package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.sondage;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Sondage;

import java.util.Date;

import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured.*;

public class SondageSampleE2E {

    public static String generateSondagePostBody(Sondage sondage) {
        return "{\n" +
                "\"sondageId\" : "+sondage.getSondageId()+",\n" +
                "\"nom\" : \"" + sondage.getNom() + "\",\n" +
                "\"description\" : \"" + sondage.getDescription() + "\",\n" +
                "\"fin\" : \"" + sendDate(sondage.getFin()) + "\",\n" +
                "\"cloture\" : " + sondage.getCloture() + ",\n" +
                "\"createBy\" : "+ sondage.getCreateBy().getParticipantId() +"\n" +
                "}";
    }

    public final static Date todayDate = new Date();
    public final static Date futureDate = new Date(todayDate.getTime()+864000);

    public final static Date pastDate = new Date(todayDate.getTime()-864000);
}
