package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.date_sondage;

import java.util.Date;

import static fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset.CrudRestAssured.sendDate;

public class DateSondageSampleE2E {
    public static String generateDateSondagePOSTBody(Date date) {
        return "{\n" +
                "\"date\" : \"" + sendDate(date) + "\"\n" +
                "}";
    }
}
