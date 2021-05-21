package com.jaej.demo.util;

import android.icu.util.Calendar;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class Constants {

    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final int CHANNEL = 44;

    public static final String COUNTER = "COUNTER";
    public static final String COUNTER_SERVICE_NOTIFICATION = "COUNTER_SERVICE_NOTIFICATION";

    public static final String TIMER_TIME_VALUE = "TIMER_TIME_VALUE";
    public static final String TIMER_TASK_TIMER = "TIMER_TASK_TIMER";
    public static final String TIMER_TASK_NAME = "TIMER_TASK_NAME";

    public static final String TIMER_TIME_REMAINING = "TIMER_TIME_REMAINING";
    public static final String TIMER_FINISHED = "TIMER_FINISHED";
    public static final String TIMER_BROADCAST = "TIMER_BROADCAST";
    public static final String TIMER_TASK = "TIMER_TASK";
    public static final String TIMER_RECORD = "TIMER_RECORD";


    public static final String TASK_NOTIFICATION = "TASK_NOTIFICATION";
    public static final String REST_NOTIFICATION = "REST_NOTIFICATION";
    public static final String ACTIVATE_INTENT_FROM_FRAGMENT = "ACTIVATE_INTENT_FROM_FRAGMENT";

    public static final String TASK_FROM_HOME = "TASK_FROM_HOME";
    public static final String RECORD_FROM_HOME = "RECORD_FROM_HOME";
    public static final String TASK_FROM_SERVICE = "TASK_FROM_SERVICE";
    public static final String RECORD_FROM_SERVICE = "RECORD_FROM_SERVICE";
    public static final String TASK_FROM_FRAGMENT = "TASK_FROM_FRAGMENT";
    public static final String RECORD_FROM_FRAGMENT = "RECORD_FROM_FRAGMENT";
    public static final String TASK_FROM_ACTIVITY = "TASK_FROM_ACTIVITY";
    public static final String RECORD_FROM_ACTIVITY = "RECORD_FROM_ACTIVITY";
    public static final String IS_TIMER_RUNNING = "IS_TIMER_RUNNING";


    public static final String FIRST_DAY = "FIRST_DAY";
    public static final String USER_BIRTHDAY = "USER_BIRTHDAY";
    public static final String USER_VIEW_RANGE_START = "USER_VIEW_RANGE_START";
    public static final String USER_VIEW_RANGE_END = "USER_VIEW_RANGE_END";
    public static final String USER_VIEW_GROUP_BY = "USER_VIEW_GROUP_BY";
    public static final String USER_PARTITION_SELECTION = "USER_PARTITION_SELECTION";
    public static final String USER_PARTITION_CHAR = "USER_PARTITION_CHAR";

    public static final String STATE_TASK = "STATE_TASK";
    public static final String STATE_RECORD = "STATE_RECORD";


    public static final String START_TASK_FOREGROUND = "START_TASK_FOREGROUND";
    public static final String START_REST_FOREGROUND = "START_REST_FOREGROUND";
    public static final String STOP_FOREGROUND = "STOP_FOREGROUND";

    public static final int DB_VERSION = 1;
    public static final String TODAY_DAY_OF_WEEK = todayDayOfWeek();

    public static Date TODAY = getTODAY();

    private static Date getTODAY() {
        LocalDateTime now = LocalDateTime.now(); // current date and time
        LocalDateTime midnight = now.toLocalDate().atStartOfDay();


        return Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());
    }


    private static String todayDayOfWeek() {
        Date curTime = Calendar.getInstance().getTime();

        return new SimpleDateFormat("u", Locale.getDefault()).format(curTime).equals("7") ?
                "0" : new SimpleDateFormat("u", Locale.getDefault()).format(curTime);
    }



}
