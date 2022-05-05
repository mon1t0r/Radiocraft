package com.mon1tor.radiocraft.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String timestampToString(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }
}
