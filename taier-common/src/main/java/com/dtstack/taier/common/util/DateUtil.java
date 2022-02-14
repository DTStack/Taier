package com.dtstack.taier.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.ref.SoftReference;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/5
 */
public class DateUtil {

    private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>
            THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>();
    private static final Object object = new Object();

    static final String timeZone = "GMT+8";

    static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";

    static final String dateFormat = "yyyy-MM-dd";

    static final String timeFormat = "HH:mm:ss";

    static final String yearFormat = "yyyy";

    static TimeZone timeZoner;

    static FastDateFormat datetimeFormatter;

    static FastDateFormat dateFormatter;

    static FastDateFormat timeFormatter;

    static FastDateFormat yearFormatter;

    /**
     * 获得当前时间戳
     *
     * @return milliseconds
     * @author toutian
     */
    public static long getCurrentSeconds() {
        return System.currentTimeMillis();
    }


    /**
     * 获取SimpleDateFormat
     *
     * @param pattern 日期格式
     * @return SimpleDateFormat对象
     */
    private static SimpleDateFormat getDateFormat(String pattern) {
        final SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
        Map<String, SimpleDateFormat> formats = ref == null ? null : ref.get();
        if (formats == null) {
            formats = new HashMap<>(16);
            THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
        }
        SimpleDateFormat format = formats.computeIfAbsent(pattern, k -> new SimpleDateFormat(pattern));
        return format;
    }

    /**
     * 根据时间戳，格式化为日期时间字符串
     *
     * @return
     * @author toutian
     */
    public static String getFormattedDate(long timestamp) {
        return getFormattedDate(timestamp, "yyyy-MM-dd");//yyyy-MM-dd HH:mm:ss
    }

    /**
     * 根据时间戳与格式类型，格式化为日期时间字符串
     *
     * @return
     * @author toutian
     */
    public static String getFormattedDate(long timestamp, String format) {
        SimpleDateFormat simpleDateFormat = getDateFormat(format);
        return simpleDateFormat.format(new Date(timestamp));
    }

    /**
     * 根据日期，获取时间戳
     *
     * @return milliseconds
     * @author toutian
     */
    public static long getTimestamp(String formattedDate) {
        return getTimestamp(formattedDate, "yyyy-MM-dd");//yyyy-MM-dd HH:mm:ss
    }

    /**
     * 根据日期与格式类型，获取时间戳
     *
     * @return milliseconds
     * @author toutian
     */
    public static long getTimestamp(String formattedDate, String format) {
        SimpleDateFormat simpleDateFormat = getDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(formattedDate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 根据时间戳，获得当天的开始时间戳
     *
     * @param startTimeSecond milliseconds
     * @return milliseconds
     * @author toutian
     */
    public static long getTimestamp(Long startTimeSecond) {
        SimpleDateFormat simpleDateFormat = getDateFormat("yyyyMMdd");
        try {
            Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date(startTimeSecond)));
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 前*天的时间戳
     *
     * @param day
     * @return milliseconds
     * @author toutian
     */
    public static long TODAY_LESS(int day) {
        return (calTodayMills() - TimeUnit.DAYS.toMillis(day));
    }

    /**
     * 明天0点的时间戳
     *
     * @return milliseconds
     */
    public static long TOMORROW_ZERO() {
        return (calTodayMills() + TimeUnit.DAYS.toMillis(1));
    }

    /**
     * 获得当天0点的时间戳
     *
     * @return milliseconds
     */
    public static long calTodayMills() {
        SimpleDateFormat simpleDateFormat = getDateFormat("yyyyMMdd");
        try {
            Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static String getTimeStrWithoutSymbol(String timeStr) {
        return timeStr.replace(" ", "").replace("-", "").replace(":", "");
    }

    /**
     * 将yyyyMMddHHmmss ---> yyyy-MM-dd HH:mm:ss
     *
     * @param str
     * @return
     */
    public static String addTimeSplit(String str) {

        if (str.length() != 14) {
            return str;
        }

        StringBuffer sb = new StringBuffer("");
        sb.append(str.substring(0, 4))
                .append("-")
                .append(str.substring(4, 6))
                .append("-")
                .append(str.substring(6, 8))
                .append(" ")
                .append(str.substring(8, 10))
                .append(":")
                .append(str.substring(10, 12))
                .append(":")
                .append(str.substring(12, 14));
        return sb.toString();
    }

    public static String getTimeDifference(Timestamp start, Timestamp end) {
        if (start == null || end == null) {
            return StringUtils.EMPTY;
        }
        long time1 = start.getTime();
        long time2 = end.getTime();

        long dif = time2 - time1;

        return getTimeDifference(dif);
    }

    public static String getTimeDifference(Long mill) {
        if (mill == null || mill == 0L) {
            return "0秒";
        } else if(mill < 1000){
            return mill + "毫秒";
        }

        int dif = (int) (mill / 1000);

        StringBuilder time = new StringBuilder();
        int hours = dif / 3600;
        if (hours > 0) {
            time.append(hours).append("小时");
        }
        int minutes = (dif - hours * 3600) / 60;
        if (minutes > 0) {
            time.append(minutes).append("分钟");
        }
        int seconds = (dif - hours * 3600 - minutes * 60);
        if (seconds > 0) {
            time.append(seconds).append("秒");
        }
        return time.toString();
    }

    public static int getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(1);
    }

    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    public static long getLastDay(int num) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - num);
        return cal.getTimeInMillis();
    }

    public static Long getMillisecond(long time){
        if(String.valueOf(time).matches("\\d{10}")){
            return time * 1000;
        } else {
            return time;
        }
    }

    public static Timestamp getSqlTimeStampVal(Object val) {
        if (val == null) {
            return null;
        } else if(val instanceof String) {
            return new Timestamp(stringToDate((String)val).getTime());
        } else if (val instanceof Integer) {
            Integer rawData = (Integer) val;
            return new Timestamp(rawData.longValue());
        } else if (val instanceof Long) {
            Long rawData = (Long) val;
            return new Timestamp(rawData);
        } else if (val instanceof java.sql.Date) {
            return (Timestamp) val;
        } else if(val instanceof Timestamp) {
            return (Timestamp) val;
        } else if(val instanceof Date) {
            Date d = (Date)val;
            return new Timestamp(d.getTime());
        }

        throw new IllegalArgumentException("Can't convert " + val.getClass().getName() + " to Date");
    }

    public static Date stringToDate(String strDate)  {
        assert strDate == null || strDate.trim().length() == 0;

        try {
            return datetimeFormatter.parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return dateFormatter.parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return timeFormatter.parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return yearFormatter.parse(strDate);
        } catch (ParseException ignored) {
        }

        throw new RuntimeException("can't parse date");
    }

    public static java.sql.Date getSqlDate(Object val) {
        if(val == null) {
            return null;
        } else if(val instanceof String) {
            return new java.sql.Date(stringToDate((String)val).getTime());
        } else if (val instanceof Integer) {
            Integer rawData = (Integer) val;
            return new java.sql.Date(rawData.longValue());
        } else if (val instanceof Long) {
            Long rawData = (Long) val;
            return new java.sql.Date(rawData.longValue());
        } else if (val instanceof java.sql.Date) {
            return (java.sql.Date) val;
        } else if(val instanceof Timestamp) {
            Timestamp ts = (Timestamp) val;
            return new java.sql.Date(ts.getTime());
        } else if(val instanceof Date) {
            Date d = (Date)val;
            return new java.sql.Date(d.getTime());
        }

        throw new IllegalArgumentException("Can't convert " + val.getClass().getName() + " to Date");
    }

    static {
        timeZoner = TimeZone.getTimeZone(timeZone);
        datetimeFormatter = FastDateFormat.getInstance(datetimeFormat, timeZoner);
        dateFormatter = FastDateFormat.getInstance(dateFormat, timeZoner);
        timeFormatter =  FastDateFormat.getInstance(timeFormat, timeZoner);
        yearFormatter = FastDateFormat.getInstance(yearFormat, timeZoner);
    }
}
