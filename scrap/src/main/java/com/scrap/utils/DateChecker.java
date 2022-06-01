package com.scrap.utils;

import java.util.Date;

public class DateChecker {
    public static boolean checkIfOutdatedPost(Date postDate, Date compareTime) {
        return postDate == null || (postDate.before(compareTime));
    }
}
