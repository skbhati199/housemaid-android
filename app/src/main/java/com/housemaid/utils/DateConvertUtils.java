package com.housemaid.utils;

public class DateConvertUtils {

    public static String daysToYear(int days) {

        int year;
        int months = 0;
        String experience;


        if (days > 365) {
            year = days / 365;

            if (days % 365 > 30) months = (days % 365) / 30;
            else if (days % 365 == 30) months = 1;
            else months = 0;

        } else if (days == 365) {
            year = 1;
        } else {
            year = 0;
            months = days / 30;
        }

        if (days == 0) experience = "No experience";
        else {
            if (year == 0) experience = String.valueOf(months) + " months";
            else if (months == 0) experience = String.valueOf(year) + " years";
            else experience = String.valueOf(year) + " years " + String.valueOf(months) + " months";
        }
        return experience;
    }
}
