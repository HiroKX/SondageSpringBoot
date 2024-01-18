package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.dataset;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static io.restassured.RestAssured.given;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class CrudRestAssured {
    public static final String sendPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String recievePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().load();

    public static final String SRV_BASEURI = "http://localhost";
    public static final int SERVER_PORT = Integer.parseInt(Objects.requireNonNull(dotenv.get("SERVER_PORT")));

    public CrudRestAssured() {
        RestAssured.baseURI= CrudRestAssured.SRV_BASEURI;
        RestAssured.port= CrudRestAssured.SERVER_PORT;
    }

    public static String recieveDate(Date date) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(recievePattern);
        return dateFormatter.format(date)+"+00:00";
    }

    public static String sendDate(Date date) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(sendPattern);
        return dateFormatter.format(date);
    }

    public static Response dbPOST(String path, String requestBody ) {
        return given()
                .header("accept", "*/*")
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post(path)
                .then()
                .extract().response();
    }

    public static Response dbDELETE(String path) {
        return given()
                .header("accept", "*/*")
                .when()
                .delete(path)
                .then()
                .extract().response();
    }

    public static Response dbPUT(String path, String requestBody) {
        return given()
                .header("accept", "*/*")
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .put(path)
                .then()
                .extract().response();
    }
    public static Response dbGET(String path) {
        return given()
                .header("accept", "*/*")
                .when()
                .get(path)
                .then()
                .extract().response();
    }
}
