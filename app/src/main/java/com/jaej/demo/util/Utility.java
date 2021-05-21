package com.jaej.demo.util;

import android.app.NotificationManager;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.service.notification.StatusBarNotification;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.room.TypeConverter;

import java.util.Date;

public class Utility {

    //TypeConverter for Room operation
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    //when long value needs to be localized
    public static long localizedLong(long dateMillis) {
        TimeZone timeZoneUTC = TimeZone.getDefault();
        long offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
        return dateMillis + offsetFromUTC;
    }

    public static long unlocalizedLong(long dateMillis) {
        TimeZone timeZoneUTC = TimeZone.getDefault();
        long offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
        return dateMillis - offsetFromUTC;
    }

    public static String minuteStringFormatHelper(String string) {
        if (Integer.valueOf(string) < 60) {
            SimpleDateFormat dateFormatTwoDigit = new SimpleDateFormat("mm:ss");
            dateFormatTwoDigit.setTimeZone(TimeZone.getTimeZone("GMT"));
            long taskMillis = Long.parseLong(string) * 60 * 1000;

            Date date = new Date(taskMillis);

            return dateFormatTwoDigit.format(date);
        }
        else {
            SimpleDateFormat dateFormatThreeDigit = new SimpleDateFormat("h:mm:ss");
            dateFormatThreeDigit.setTimeZone(TimeZone.getTimeZone("GMT"));
            long taskMillis = Long.parseLong(string) * 60 * 1000;
            Date date = new Date(taskMillis);
            return dateFormatThreeDigit.format(date);
        }
    }

    public static String secondStringFormatHelper(Integer seconds) {
        if (seconds <= 3600) {
            SimpleDateFormat dateFormatTwoDigit = new SimpleDateFormat("mm:ss");
            dateFormatTwoDigit.setTimeZone(TimeZone.getTimeZone("GMT"));
            long taskMillis = seconds * 1000;
            Date date = new Date(taskMillis);
            return dateFormatTwoDigit.format(date);
        }
        else {
            SimpleDateFormat dateFormatThreeDigit = new SimpleDateFormat("h:mm:ss");
            dateFormatThreeDigit.setTimeZone(TimeZone.getTimeZone("GMT"));
            long taskMillis = seconds * 1000;
            Date date = new Date(taskMillis);
            return dateFormatThreeDigit.format(date);
        }
    }

    //returning Color as string depending on score input parameters used for HomeFragment
    //color ranging from 0 (red) - 50 (yellow) - 100 (green)
    public static String doubleToColorString(double score) {
        int R = 252;
        int G = 3;
        int B = 3;
        double scoreDouble = score * 498;
        int scoreInt = (int) scoreDouble;


        if (score >= 1)
            return "#FF03FC03";
        else {

            if (scoreInt >= 249) {
                G = G + 249;
                R = R - (scoreInt - 249);
            } else {
                G = G + scoreInt;
            }
            String RString, GString, BString;
            RString = Integer.toHexString(R);
            if (RString.length() < 2)
                RString = "0" + RString;
            GString = Integer.toHexString(G);
            if (GString.length() < 2) {
                GString = "0" + GString;
            }

            BString = "0" + B;

            return "#FF" + RString.toUpperCase() + GString.toUpperCase() + BString.toUpperCase();
        }
    }

    //returning Color as string depending on score input parameters used for TaskFragment
    //color ranging from 0 (red) - 50 (yellow) - 100 (green)
    public static String doubleToColorString(double score, double scoreMax) {
        int R = 252;
        int G = 3;
        int B = 3;

        double scoreRatio = score / scoreMax;

        double scoreDouble = scoreRatio * 498;
        int scoreInt = (int) scoreDouble;


        if (scoreRatio >= 1)
            return "#FF03FC03";
        else {
            if (scoreInt >= 249) {
                G = G + 249;
                R = R - (scoreInt - 249);
            } else {
                G = G + scoreInt;
            }
            String RString, GString, BString;
            RString = Integer.toHexString(R);
            if (RString.length() < 2)
                RString = "0" + RString;
            GString = Integer.toHexString(G);
            if (GString.length() < 2) {
                GString = "0" + GString;
            }

            BString = "0" + B;

            return "#FF" + RString.toUpperCase() + GString.toUpperCase() + BString.toUpperCase();
        }
    }
}
