package com.umnagendra.filter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Basic utility methods
 *
 * @author Nagendra Mahesh
 */
public class DumperUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy (z) HH:mm:ss.SSS");

    public static String getCurrentTimestamp() {
        return DATE_FORMAT.format(new Date());
    }
}
