package net.yury757.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtil {
    public static SimpleDateFormat GMTDateFormatter;

    static {
        GMTDateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.CHINA);
    }
}
