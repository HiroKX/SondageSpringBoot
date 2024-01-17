package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.DisplayNameGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public final class RestAssured_conf {
    public static final String sendPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String recievePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().load();

    public static final String SRV_BASEURI = "http://localhost";
    public static final int SERVER_PORT = Integer.parseInt(Objects.requireNonNull(dotenv.get("SERVER_PORT")));

    public static String recieveDate(Date date) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(recievePattern);
        return dateFormatter.format(date)+"+00:00";
    }

    public static String sendDate(Date date) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(sendPattern);
        return dateFormatter.format(date);
    }
}
