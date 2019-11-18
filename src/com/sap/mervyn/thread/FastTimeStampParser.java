package com.sap.mervyn.thread;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

public class FastTimeStampParser {
    private final SimpleDateFormat sdf;
    private final Map<String, Long> cache = new HashMap<>();

    public FastTimeStampParser() {
        this("yyy-MM-dd HH:mm:ss");
    }

    public FastTimeStampParser(String timeStampFormat) {
        SimpleTimeZone stz = new SimpleTimeZone(0, "UTC");
        sdf = new SimpleDateFormat(timeStampFormat);
        sdf.setTimeZone(stz);
    }

    public long parseTimeStamp(String timeStamp) {
        Long cacheValue = cache.get(timeStamp);
        if (cacheValue != null) {
            return cacheValue.longValue();
        }

        long result = 0;
        Date date = null;

        try {
            date = sdf.parse(timeStamp);
            result = date.getTime();
            cache.put(timeStamp, Long.valueOf(result));
        } catch (ParseException e) {
            throw new RuntimeException();
        }

        return result;
    }
}
