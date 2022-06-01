package com.scrap.constants;

import java.text.SimpleDateFormat;
import java.util.Timer;

public class TimeConstants {

    public static final SimpleDateFormat CENSOR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat TSN_DATE_FORMAT = new SimpleDateFormat("hh:mm, dd.MM.yyyy");
    public static final SimpleDateFormat PRAVDA_DATE_FORMAT = new SimpleDateFormat("hh:mm");

    public static final int MINUTE_DELAY = 60 * 1000;
    public static final int DELAY_IN_MS = MINUTE_DELAY;

    public static Timer TIMER = new Timer("Scrap timer");
}
