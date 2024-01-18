package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception;

public class DateSondeeAlreadyExistsException extends Exception {

        public DateSondeeAlreadyExistsException() {
            super("La participation a déjà été enregistrée.");
        }
}
