package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception;

public class DateSondageAlreadyExistsException extends Exception {
    public DateSondageAlreadyExistsException(){

        super("La date de sondage existe déjà.");
    }
}
