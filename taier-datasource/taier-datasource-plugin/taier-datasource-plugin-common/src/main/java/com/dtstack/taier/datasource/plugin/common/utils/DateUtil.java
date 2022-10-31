/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 20:13 2020/9/1
 * @Description：时间工具
 */
@Slf4j
public class DateUtil {
    static final String TIME_ZONE = "GMT+8";

    static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static final String DATE_FORMAT = "yyyy-MM-dd";

    static final String TIME_FORMAT = "HH:mm:ss";

    static final String YEAR_FORMAT = "yyyy";

    static final String STRING_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    static TimeZone timeZoner;

    static FastDateFormat datetimeFormatter;

    static FastDateFormat dateFormatter;

    static FastDateFormat timeFormatter;

    static FastDateFormat yearFormatter;

    static SimpleDateFormat simpleDateTimeFormatter;

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
    public static SimpleDateFormat getDateFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format;
    }

    /**
     * 根据时间戳，格式化为日期时间字符串 yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @author toutian
     */
    public static String getFormattedDate(long timestamp) {
        return getFormattedDate(timestamp, "yyyy-MM-dd");
    }

    /**
     * 根据时间戳，格式化为日期时间字符串 yyyy-MM-dd HH:mm:ss
     */
    public static String getFormattedDateTime(long timestamp) {
        return simpleDateTimeFormatter.format(new Date(timestamp));
    }

    /**
     * 根据时间戳与格式类型，格式化为日期时间字符串
     *
     * @param timestamp
     * @param format
     * @return
     * @author toutian
     */
    public static String getFormattedDate(long timestamp, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(timestamp));
    }

    /**
     * 根据日期，获取时间戳 yyyy-MM-dd HH:mm:ss
     *
     * @return milliseconds
     * @author toutian
     */
    public static long getTimestamp(String formattedDate) {
        return getTimestamp(formattedDate, "yyyy-MM-dd");
    }

    /**
     * 根据日期与格式类型，获取时间戳
     *
     * @return milliseconds
     * @author toutian
     */
    public static long getTimestamp(String formattedDate, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(formattedDate);
            return date.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date(startTimeSecond)));
            return date.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
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
    public static long todayLess(int day) {
        return (calTodayMills() - TimeUnit.DAYS.toMillis(day));
    }

    /**
     * 明天0点的时间戳
     *
     * @return milliseconds
     */
    public static long tomorrowZero() {
        return (calTodayMills() + TimeUnit.DAYS.toMillis(1));
    }

    /**
     * 获得当天0点的时间戳
     *
     * @return milliseconds
     */
    public static long calTodayMills() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            return date.getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
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
        } else if (mill < 1000) {
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

    public static Long getMillisecond(long time) {
        if (String.valueOf(time).matches("\\d{10}")) {
            return time * 1000;
        } else {
            return time;
        }
    }

    public static java.sql.Timestamp getSqlTimeStampVal(Object val) {
        if (val == null) {
            return null;
        } else if (val instanceof String) {
            return new java.sql.Timestamp(stringToDate((String) val).getTime());
        } else if (val instanceof Integer) {
            Integer rawData = (Integer) val;
            return new java.sql.Timestamp(rawData.longValue());
        } else if (val instanceof Long) {
            Long rawData = (Long) val;
            return new java.sql.Timestamp(rawData);
        } else if (val instanceof java.sql.Date) {
            return (java.sql.Timestamp) val;
        } else if (val instanceof Timestamp) {
            return (Timestamp) val;
        } else if (val instanceof Date) {
            Date d = (Date) val;
            return new java.sql.Timestamp(d.getTime());
        }

        throw new IllegalArgumentException("Can't convert " + val.getClass().getName() + " to Date");
    }

    public static Date stringToDate(String strDate) {
        AssertUtils.notBlank(strDate, "strDate not null");

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

        throw new SourceException("can't parse date");
    }

    public static java.sql.Date getSqlDate(Object val) {
        if (val == null) {
            return null;
        } else if (val instanceof String) {
            return new java.sql.Date(stringToDate((String) val).getTime());
        } else if (val instanceof Integer) {
            Integer rawData = (Integer) val;
            return new java.sql.Date(rawData.longValue());
        } else if (val instanceof Long) {
            Long rawData = (Long) val;
            return new java.sql.Date(rawData.longValue());
        } else if (val instanceof java.sql.Date) {
            return (java.sql.Date) val;
        } else if (val instanceof Timestamp) {
            Timestamp ts = (Timestamp) val;
            return new java.sql.Date(ts.getTime());
        } else if (val instanceof Date) {
            Date d = (Date) val;
            return new java.sql.Date(d.getTime());
        }

        throw new IllegalArgumentException("Can't convert " + val.getClass().getName() + " to Date");
    }

    /**
     * 强转 timestamp
     *
     * @param timestampObj timestamp obj
     * @return 强转后的 timestamp
     */
    public static Timestamp getTimestamp(Object timestampObj) {
        if (Objects.isNull(timestampObj)) {
            return null;
        } else if (timestampObj instanceof Timestamp) {
            return (Timestamp) timestampObj;
        } else if (timestampObj instanceof Long) {
            return new Timestamp((Long) timestampObj);
        } else {
            return null;
        }
    }

    /**
     * 将date.toString()后的格式日期转换为毫秒时间戳
     *
     * @param dateStr string格式的日期
     * @return 时间戳
     */
    public static Long stringToLong(String dateStr) {
        if (StringUtils.isBlank(dateStr) || dateStr.contains("UNKNOWN")){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(STRING_FORMAT, Locale.ENGLISH);
        try {
            return sdf.parse(dateStr).getTime();
        } catch (ParseException e) {
            throw new SourceException("parse date error." + e.getMessage(), e);
        }
    }

    static {
        timeZoner = TimeZone.getTimeZone(TIME_ZONE);
        datetimeFormatter = FastDateFormat.getInstance(DATETIME_FORMAT, timeZoner);
        dateFormatter = FastDateFormat.getInstance(DATE_FORMAT, timeZoner);
        timeFormatter = FastDateFormat.getInstance(TIME_FORMAT, timeZoner);
        yearFormatter = FastDateFormat.getInstance(YEAR_FORMAT, timeZoner);
        simpleDateTimeFormatter = new SimpleDateFormat(DATETIME_FORMAT);
    }
}
