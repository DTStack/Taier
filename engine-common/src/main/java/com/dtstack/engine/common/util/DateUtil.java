package com.dtstack.engine.common.util;

import java.lang.ref.SoftReference;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 
 *
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class DateUtil {

    private static final String TIME_ZONE = "GMT+8";
    private static final String STANDARD_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String UN_STANDARD_DATETIME_FORMAT = "yyyyMMddHHmmss";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String YEAR_FORMAT = "yyyy";

    private static final String STANDARD_DATETIME_FORMAT_KEY = "standardDatetimeFormatter";
    private static final String UN_STANDARD_DATETIME_FORMAT_KEY = "unStandardDatetimeFormatter";
    private static final String DATE_FORMAT_KEY = "dateFormatter";
    private static final String TIME_FORMAT_KEY = "timeFormatter";
    private static final String YEAR_FORMAT_KEY = "yearFormatter";
    private static final String START_TIME = "1970-01-01";

    private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>
            THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>();


    public static ThreadLocal<Map<String,SimpleDateFormat>> datetimeFormatter = ThreadLocal.withInitial(() -> {
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);

        Map<String, SimpleDateFormat> formatterMap = new HashMap<>();

        SimpleDateFormat standardDatetimeFormatter = new SimpleDateFormat(STANDARD_DATETIME_FORMAT);
        standardDatetimeFormatter.setTimeZone(timeZone);
        formatterMap.put(STANDARD_DATETIME_FORMAT_KEY,standardDatetimeFormatter);

        SimpleDateFormat unStandardDatetimeFormatter = new SimpleDateFormat(UN_STANDARD_DATETIME_FORMAT);
        unStandardDatetimeFormatter.setTimeZone(timeZone);
        formatterMap.put(UN_STANDARD_DATETIME_FORMAT_KEY,unStandardDatetimeFormatter);

        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        dateFormatter.setTimeZone(timeZone);
        formatterMap.put(DATE_FORMAT_KEY,dateFormatter);

        SimpleDateFormat timeFormatter = new SimpleDateFormat(TIME_FORMAT);
        timeFormatter.setTimeZone(timeZone);
        formatterMap.put(TIME_FORMAT_KEY,timeFormatter);

        SimpleDateFormat yearFormatter = new SimpleDateFormat(YEAR_FORMAT);
        yearFormatter.setTimeZone(timeZone);
        formatterMap.put(YEAR_FORMAT_KEY,yearFormatter);

        return formatterMap;
    });

    public static java.sql.Date columnToDate(Object column) {
        if(column instanceof String) {
            Date date = stringToDate((String) column);
            if (date == null) {
                throw new IllegalArgumentException("Can't convert " + column.getClass().getName() + " to Date");
            }
            return new java.sql.Date(date.getTime());
        } else if (column instanceof Integer) {
            Integer rawData = (Integer) column;
            return new java.sql.Date(rawData.longValue());
        } else if (column instanceof Long) {
            Long rawData = (Long) column;
            return new java.sql.Date(rawData.longValue());
        } else if (column instanceof java.sql.Date) {
            return (java.sql.Date) column;
        } else if(column instanceof java.sql.Timestamp) {
            Timestamp ts = (Timestamp) column;
            return new java.sql.Date(ts.getTime());
        }
        throw new IllegalArgumentException("Can't convert " + column.getClass().getName() + " to Date");
    }

    public static Date stringToDate(String strDate)  {
        if(strDate == null || strDate.trim().length() == 0) {
            return null;
        }
        try {
            return datetimeFormatter.get().get(STANDARD_DATETIME_FORMAT_KEY).parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return datetimeFormatter.get().get(UN_STANDARD_DATETIME_FORMAT_KEY).parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return datetimeFormatter.get().get(DATE_FORMAT_KEY).parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return datetimeFormatter.get().get(TIME_FORMAT_KEY).parse(strDate);
        } catch (ParseException ignored) {
        }

        try {
            return datetimeFormatter.get().get(YEAR_FORMAT_KEY).parse(strDate);
        } catch (ParseException ignored) {
        }

        throw new RuntimeException("can't parse date");
    }

    public static String dateToString(Date date) {
        return datetimeFormatter.get().get(DATE_FORMAT_KEY).format(date);
    }

    public static String timestampToString(Date date) {
        return datetimeFormatter.get().get(STANDARD_DATETIME_FORMAT_KEY).format(date);
    }

    public static String dateToYearString(Date date) {
        return datetimeFormatter.get().get(YEAR_FORMAT_KEY).format(date);
    }

    public static SimpleDateFormat getDateTimeFormatter(){
        return datetimeFormatter.get().get(STANDARD_DATETIME_FORMAT_KEY);
    }

    public static SimpleDateFormat getDateFormatter(){
        return datetimeFormatter.get().get(DATE_FORMAT_KEY);
    }

    public static SimpleDateFormat getTimeFormatter(){
        return datetimeFormatter.get().get(TIME_FORMAT_KEY);
    }

    public static SimpleDateFormat getYearFormatter(){
        return datetimeFormatter.get().get(YEAR_FORMAT_KEY);
    }

    /**
     * 获取所在日的0点的时间戳（秒值）
     * 
     * @param day Long 时间
     * @return long
     */
    public static long getTodayStart(long day) {
        long firstDay = 0L;
        Calendar cal = Calendar.getInstance();
        if (("" + day).length() > 10) {
            cal.setTime(new Date(day));
        } else {
            cal.setTime(new Date(day * 1000));
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        firstDay = cal.getTimeInMillis() / 1000;
        return firstDay;
    }

    /**
     * 获取所在日的0点的时间戳
     * @param day Long 时间
     * @param scope String 级别<br>"MS"：毫秒级<br>"S":秒级
     * @return
     */
    public static long getTodayStart(long day,String scope) {
    	if("MS".equals(scope)){
    		return getTodayStart(day)*1000;
    	}else if("S".equals(scope)){
    		return getTodayStart(day);
    	}else{
    		return getTodayStart(day);
    	}
    }

    /**
     * 获取下一天时间的0点的时间戳（秒值）
     * @param day Long 时间
     * @return long
     */
    public static long getNextDayStart(long day) {
        long daySpanMill = 86400000L;
        long nextDay = 0L;
        Calendar cal = Calendar.getInstance();
        if (("" + day).length() > 10) {
            cal.setTime(new Date(day));
        } else {
            cal.setTime(new Date(day * 1000));
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        nextDay = (cal.getTimeInMillis() + daySpanMill) / 1000;
        return nextDay;
    }
    
    /**
     * 获取下一天时间的0点的时间戳
     * @param day Long 时间
     * @param scope String 级别<br>"MS"：毫秒级<br>"S":秒级
     * @return
     */
    public static long getNextDayStart(long day,String scope) {
    	if("MS".equals(scope)){
    		return getNextDayStart(day)*1000;
    	}else if("S".equals(scope)){
    		return getNextDayStart(day);
    	}else{
    		return getNextDayStart(day);
    	}
    }
    

    /**
     * 根据某个日期时间戳秒值，获取所在月的第一天的0点的时间戳秒值.
     * 
     * @param day
     * @return
     */
    public static long getMonthFirst(long day) {
        long firstDay = 0L;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(day * 1000));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        firstDay = cal.getTimeInMillis() / 1000;
        return firstDay;
    }

    /**
     * 根据某个日期时间戳秒值，获取所在月，月份值按实际理解，1代表一月，依此类推.
     * 
     * @param day
     * @return
     */
    public static int getMonth(long day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(day * 1000));
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据某个日期时间戳秒值，获取所在年。
     * 
     * @author yumo.lck
     */
    public static int getYear(long day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(day * 1000));
        return cal.get(Calendar.YEAR);
    }

    /**
     * 根据某个日期时间戳秒值，获取所在周的周一的0点的时间戳秒值.
     * 
     * @param day
     * @return
     */
    public static long getWeekFirst(long day) {
        long firstDay = 0L;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(day * 1000));
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        firstDay = cal.getTimeInMillis() / 1000;
        return firstDay;
    }

    /**
     * 根据某个日期时间戳秒值，获取所在周在一年中是第几周.
     * 
     * @param day
     * @return
     */
    public static int getWeekOfYear(long day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(day * 1000));
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 根据字符串取得昨天日期返回String
     * 
     * @param day 时间字符串
     * @param inFormat 传入正则
     * @param outFormat 传出正则
     * @return String
     * @throws ParseException
     */
    public static String getYesterdayByString(String day, String inFormat, String outFormat){
        try {
			SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
			Date date = sdf.parse(day);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int calendarDay = calendar.get(Calendar.DATE);
			calendar.set(Calendar.DATE, calendarDay - 1);
			String dayBefore = new SimpleDateFormat(outFormat).format(calendar.getTime());
			return dayBefore;
		} catch (ParseException e) {
			return null;
		}
    }

    /**
     * 根据字符串取得明天日期返回String
     * 
     * @param day 时间字符串
     * @param inFormat 传入正则
     * @param outFormat 传出正则
     * @return String
     * @throws ParseException
     */
    public static String getTomorrowByString(String day, String inFormat, String outFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
        Date date = sdf.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int calendarDay = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, calendarDay + 1);
        String dayBefore = new SimpleDateFormat(outFormat).format(calendar.getTime());
        return dayBefore;
    }
    
    /**
     * 根据字符串取得明天日期
     *
     * @return Date
     * @throws ParseException
     */
    public static Date getTomorrowByDate(Date date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int calendarDay = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, calendarDay + 1);
        return calendar.getTime();
    }

    /**
     * 根据字符串取得30天前的日期返回String
     *
     * @param day 时间字符串
     * @param inFormat 传入正则
     * @param outFormat 传出正则
     * @return String
     * @throws ParseException
     */
    public static String get30DaysBeforeByString(String day, String inFormat, String outFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
        Date date = sdf.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int calendarDay = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, calendarDay - 30);
        String dayBefore = new SimpleDateFormat(outFormat).format(calendar.getTime());
        return dayBefore;
    }
    
    /**
     * 根据字符串取得30天后的日期返回String
     *
     * @param day 时间字符串
     * @param inFormat 传入正则
     * @param outFormat 传出正则
     * @return String
     * @throws ParseException
     */
    public static String get30DaysLaterByString(String day, String inFormat, String outFormat) throws ParseException {
    	SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
    	Date date = sdf.parse(day);
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(date);
    	int calendarDay = calendar.get(Calendar.DATE);
    	calendar.set(Calendar.DATE, calendarDay + 30);
    	String dayBefore = new SimpleDateFormat(outFormat).format(calendar.getTime());
    	return dayBefore;
    }


    /**
     * 字符串日期格式转换
     * 
     * @param day 时间字符串
     * @param inFormat 传入正则
     * @param outFormat 传出正则
     * @return String
     * @throws ParseException
     */
    public static String getDateStrTOFormat(String day, String inFormat, String outFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
        Date date = sdf.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String dayBefore = new SimpleDateFormat(outFormat).format(calendar.getTime());
        return dayBefore;
    }
    
    public static long getDateMillTOFormat(String day, String inFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
        Date date = sdf.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis()/1000;
    }

    /**
     * 根据年和月获取这个月的第一天的秒值
     * 
     * @author yumo.lck
     * @param year
     * @param month
     * @return
     */
    public static long getFirstDay4Month(int year, int month) {
        long firstDay = 0L;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        // 上一个月的最后一天的下一天：这个月的第一天
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        firstDay = cal.getTimeInMillis() / 1000;
        return firstDay;
    }

    /**
     * 根据年和月获取这个月的最后一天的秒值
     * 
     * @author yumo.lck
     * @param year
     * @param month
     * @return
     */
    public static long getLastDay4Month(int year, int month) {
        long lastDay = 0L;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        // 1表示下个月第一天的零点，可以看做这个月的最有一天结束,但是数据表就最后一天零点就行
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        lastDay = cal.getTimeInMillis() / 1000;
        return lastDay;
    }

    /**
     * 根据某个日期时间戳秒值，获取上个月第一天零点的值的秒值
     * 
     * @author yumo.lck
     */

    public static long getBeforeMonthDay(long day, boolean chooseFirstDay) {
        long chooseDay = 0L;
        int currentMonth = getMonth(day);
        int currentYear = getYear(day);
        if (currentMonth > 1) {
            currentMonth--;
        } else {
            currentYear--;
            currentMonth = 12;
        }
        if (chooseFirstDay) {
            chooseDay = getFirstDay4Month(currentYear, currentMonth);
            return chooseDay;
        } else {
            chooseDay = getLastDay4Month(currentYear, currentMonth);
            return chooseDay;
        }

    }

    /**
     * 今日
     * 
     * @return long
     */
    public static long getMillByOneDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }

    /**
     * 昨日
     * 
     * @return long
     */
    public static long getMillByYesDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }

    /**
     * 一周前
     * 
     * @return
     */
    public static long getMillByLastWeekDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 7);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis() / 1000;
    }
    
   /**
    * 取得秒级Long型时间
    * @param severalDays 需要计算的天数
    * @param condition  条件 
    * <br> "-":当前时间点以前的时间(当前时间-需要计算的天数) 
    * <br> "+":当前时间点以后的时间(当前时间+需要计算的天数)
    * <br> null/其他值：为当天
    * @return long
    */
    public static long getMillByDay(int severalDays,String condition) {
    	int dateT=0;
        Calendar cal = Calendar.getInstance();
    	if(condition==null){
    		return getMillToDay(cal,dateT);
    	}
        if("-".equals(condition)){
        	dateT = (cal.get(Calendar.DATE) - severalDays);
        	return getMillToDay(cal,dateT);
        }
        if("+".equals(condition)){
        	dateT = (cal.get(Calendar.DATE) + severalDays);
        	return getMillToDay(cal,dateT);
        }
		return getMillToDay(cal,dateT);
  }
    
    /**
     * 取得毫秒级Long型时间
     * @param severalDays 需要计算的天数
     * @param condition  条件 
     * <br> "-":当前时间点以前的时间(当前时间-需要计算的天数) 
     * <br> "+":当前时间点以后的时间(当前时间+需要计算的天数)
     * <br> null/其他值：为当天
     * @return long
     */
    public static long getStampByDay(int severalDays,String condition) {
    	int dateT=0;
    	Calendar cal = Calendar.getInstance();
    	if(condition==null){
    		return getStampToDay(cal,dateT);
    	}
    	if("-".equals(condition)){
    		dateT = (cal.get(Calendar.DATE) - severalDays);
    		return getStampToDay(cal,dateT);
    	}
    	if("+".equals(condition)){
    		dateT = (cal.get(Calendar.DATE) + severalDays);
    		return getStampToDay(cal,dateT);
    	}
    	return getStampToDay(cal,dateT);
    }
    /**
     * 取得当天秒级Long型数据
     * @return long
     */
    public static long getMillByDay(){
		return getMillByDay(0,null);
    }
    
    /**
     * Calendar 型对日期进行计算
     * @param cal  Calendar
     * @param dateT Integer 
     * @return  long
     */
    public static long getMillToDay(Calendar cal,int dateT){
		   if(dateT!=0){
			   cal.set(Calendar.DATE, dateT);
		   }
	       cal.set(Calendar.HOUR_OF_DAY, 0);
	       cal.set(Calendar.MINUTE, 0);
	       cal.set(Calendar.SECOND, 0);
	       cal.set(Calendar.MILLISECOND, 0);
	       return cal.getTimeInMillis()/1000;
	}
    
    /**
     * Calendar 型对日期进行计算
     * @param cal  Calendar
     * @param dateT Integer 
     * @return  long
     */
    public static long getStampToDay(Calendar cal,int dateT){
    	if(dateT!=0){
    		cal.set(Calendar.DATE, dateT);
    	}
    	return cal.getTimeInMillis();
    }

    public static String getToday() {
        Calendar cal = Calendar.getInstance();
        return cal.get(1) + "年" + cal.get(2) + "月" + cal.get(3) + "日";
    }

    /**
     * 根据秒或者毫秒获取当天的日期
     * 
     * @param day 秒
     * @return 格式化后的日期
     */
    public static String getDate(long day, String format) {
        Calendar cal = Calendar.getInstance();
        if (("" + day).length() > 10) {
            cal.setTime(new Date(day));
        } else {
            cal.setTime(new Date(day * 1000));
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(cal.getTime());
    }
    
    /**
     * 获得指定格式的日期字符串
     * 
     * @return 格式化后的日期
     */
    public static String getDate(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }
    
    
    /**
     * String型时间戳转为long型
     * 
     * @param day 时间
     * @param format 格式化
     * @return long
     * @throws ParseException 
     */
    public static long stringToLong(String day, String format) throws ParseException {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        long date = dateFormat.parse(day).getTime();
    	return date;
    }
    
    /**
     * String型转Date型
     * @param day 时间
     * @param format 格式化
     * @return Date
     * @throws ParseException
     */
    public static Date stringToDate(String day, String format)  {
    	try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			 Date date = dateFormat.parse(day);
			return date;
		} catch (ParseException e) {
			return new Date();
		}
    }
    
    
    /**
     * long型时间戳转为String型
     * 
     * @param day 秒
     * @return 格式化后的日期
     * @throws ParseException 
     */
    public static String longToString(long day, String format) throws ParseException {
    	if (("" + day).length() <= 10){
            day=day*1000;
        }
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
	    String date = dateFormat.format(day);
    	return date;
    }

    /**
     * 根据秒获取给定日期的前xx天的秒值
     * 
     * @param day 秒
     * @param minusDay 需要减掉的天数
     * @return 秒
     */
    public static int getMinusDate(int day, int minusDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(day * 1000L));
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - minusDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) cal.getTimeInMillis() / 1000;
    }

    /**
     * 取得当前系统时间long型
     * 
     * @return long
     */
    public static long getMillByNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.getTimeInMillis();
    }

	public static int getWeeksBetweenTwoDates(long startDay, long endDay) {
		int week = getWeekOfYear(endDay) - getWeekOfYear(startDay) + 1;
		if(week<1){
			week = getWeekOfYear(endDay) + getMaxWeekOfYear(startDay) - getWeekOfYear(startDay) + 1;
		}
		return week;
	}

	public static int getMaxWeekOfYear(long startDay) {
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(startDay * 1000));
        return cal.getMaximum(Calendar.WEEK_OF_YEAR);
	}
	
	public static int getMonthsBetweenTwoDates(long startDay, long endDay) {
		int month = DateUtil.getMonth(endDay) - DateUtil.getMonth(startDay) + 1;
		if(month<1){
			month = getMonth(endDay) + 12 - getMonth(startDay) +1;
		}
		return month;
	}
	
	public static Date parseDate(String dateStr, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(pattern);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
     * 获取传入时间分钟的时间戳（秒值）
     * 
     * @return long
     */
    public static long getMinuteStart(long time) {
        long firstDay = 0L;
        Calendar cal = Calendar.getInstance();
        if (("" + time).length() > 10) {
            cal.setTime(new Date(time));
        } else {
            cal.setTime(new Date(time * 1000));
        }
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        firstDay = cal.getTimeInMillis() / 1000;
        return firstDay;
    }
    
    /**
     * 获取传入时间小时的时间戳（秒值）
     * 
     * @return long
     */
    public static long getHourStart(long time) {
        long firstDay = 0L;
        Calendar cal = Calendar.getInstance();
        if (("" + time).length() > 10) {
            cal.setTime(new Date(time));
        } else {
            cal.setTime(new Date(time * 1000));
        }
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        firstDay = cal.getTimeInMillis() / 1000;
        return firstDay;
    }

    /**
     * long 型时间转换成 date
     * @param time 秒值
     * @return Date
     */
    public static Date getDateByLong(long time){
        Date date = new Date();
        date.setTime(time);
        return date;
    }
    

    public static Date parseDate(String dateStr, String pattern, Locale locale){
    	SimpleDateFormat df = new SimpleDateFormat(
				pattern, locale);

		df.setTimeZone(new SimpleTimeZone(0, "GMT"));
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}
    
    public static String getDate(Date date, String format, Locale locale) {
    	SimpleDateFormat df = new SimpleDateFormat(
    			format, locale);
    	df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }

    public static String getTimeDifference(Long mill) {
        if(mill != null && mill.longValue() != 0L) {
            if(mill.longValue() < 1000L) {
                return mill + "毫秒";
            } else {
                int dif = (int)(mill.longValue() / 1000L);
                StringBuilder time = new StringBuilder();
                int hours = dif / 3600;
                if(hours > 0) {
                    time.append(hours).append("小时");
                }

                int minutes = (dif - hours * 3600) / 60;
                if(minutes > 0) {
                    time.append(minutes).append("分钟");
                }

                int seconds = dif - hours * 3600 - minutes * 60;
                if(seconds > 0) {
                    time.append(seconds).append("秒");
                }

                return time.toString();
            }
        } else {
            return "0秒";
        }
    }

    /**
     * 根据时间戳，格式化为日期时间字符串
     *
     * @return
     * @author toutian
     */
    public static String getDateFormattedDate(long timestamp) {
        SimpleDateFormat simpleDateFormat = datetimeFormatter.get().get(DATE_FORMAT_KEY);
        return simpleDateFormat.format(new Date(timestamp));
    }

    /**
     * 根据时间戳与格式类型，格式化为日期时间字符串
     *
     * @return
     * @author toutian
     */
    public static String getUnStandardFormattedDate(long timestamp) {
        SimpleDateFormat simpleDateFormat = datetimeFormatter.get().get(UN_STANDARD_DATETIME_FORMAT_KEY);
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String getStandardFormattedDate(long timestamp) {
        SimpleDateFormat simpleDateFormat = datetimeFormatter.get().get(STANDARD_DATETIME_FORMAT_KEY);
        return simpleDateFormat.format(new Date(timestamp));
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
        SimpleDateFormat simpleDateFormat = datetimeFormatter.get().get(DATE_FORMAT_KEY);
        try {
            Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
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

    public static long getLastDay(int num) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(5, cal.get(5) - num);
        return cal.getTimeInMillis();


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



}
