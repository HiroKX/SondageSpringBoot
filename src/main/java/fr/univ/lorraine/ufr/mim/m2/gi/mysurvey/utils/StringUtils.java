package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.utils;

public class StringUtils {
    private StringUtils() { }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
