package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.exception;

public class SondageCloturedException extends Exception{

    public SondageCloturedException(){
        super("Le sondage a été cloturé.");
    }
}
