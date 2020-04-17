package com.dtstack.schedule.common.util;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基准时间是yyyyMMddHHmmss
 * 基准天是yyyyMMdd
 * 所有自定义操作必须是基于这两个
 * Date: 2017/6/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class TimeParamOperator {

    private static final String STD_FMT = "yyyyMMddHHmmss";

    private static final String DAY_FMT = "yyyyMMdd";
    private static final String MONTH_FMT = "yyyyMM";
    private static final String YEAR_FMT = "yyyy";

    private static final DateTimeFormatter cycTimeFormat = DateTimeFormat.forPattern(STD_FMT);

    private static final DateTimeFormatter dayDtFmt = DateTimeFormat.forPattern(DAY_FMT);

    private static final Pattern pattern = Pattern.compile("([a-zA-Z]{4,14})\\s*([\\-\\+])\\s*(\\d+)");

    private static final Pattern customizePattern = Pattern.compile("^(yyyyMMdd|hh24miss)\\s*([\\-\\+])\\s*(.*)");

    private static final String FORMATTED_TIME = "^yyyy|MM|dd|HH|hh|hh24|mm|ss|yyyyMM|yyyyMMdd|yyyyMMddHH|yyyyMMddHHmm|yyyyMMddHHmmss|yyyyMMddhh24|yyyyMMddhh24mm|yyyyMMddhh24mmss" +
            "|HH:mm:ss|yyyy-MM-dd HH:mm:ss|yyyy-MM-dd?";

    private static final Pattern formattedPattern = Pattern.compile(FORMATTED_TIME);

    /**
     * 根据基准的时间转换出目标时间
     *
     * @param command
     * @param cycTime 必须满足STD_FMT
     * @return
     */
    public static String transform(String command, String cycTime) {
        command = command.trim();
        if (StringUtils.isBlank(command)) {
            return "";
        } else if (command.equals(STD_FMT)) {
            return cycTime;
        } else if (command.equals(DAY_FMT)) {
            return StringUtils.substring(cycTime, 0, 8);
        } else {
            return dealTimeOperator(command, cycTime);
        }
    }

    public static String dealTimeOperator(String command, String cycTime) {
        Matcher matcher = pattern.matcher(command);
        if (matcher.find() && matcher.groupCount() == 3) {
            String timeFmtStr = matcher.group(1).trim();
            String operatorStr = matcher.group(2).trim();
            String operatorNumStr = matcher.group(3).trim();
            int operatorNum = MathUtil.getIntegerVal(operatorNumStr);

            if ("-".equals(operatorStr)) {
                if (timeFmtStr.length() == 8) {
                    return minusDay(operatorNum, cycTime, timeFmtStr);
                } else if (timeFmtStr.length() == 6) {
                    return minusMonth(operatorNum, cycTime, timeFmtStr);
                } else if (timeFmtStr.length() == 4) {
                    return minusYear(operatorNum, cycTime, timeFmtStr);
                } else {
                    throw new RdosDefineException("illegal command " + command);
                }
            } else if ("+".equals(operatorStr)) {
                if (timeFmtStr.length() == 8) {
                    return plusDay(operatorNum, cycTime, timeFmtStr);
                } else if (timeFmtStr.length() == 6) {
                    return plusMonth(operatorNum, cycTime, timeFmtStr);
                } else if (timeFmtStr.length() == 4) {
                    return plusYear(operatorNum, cycTime, timeFmtStr);
                } else {
                    throw new RdosDefineException("illegal command " + command);
                }
            } else {
                throw new RdosDefineException("illegal command " + command);
            }

        } else {
            throw new RdosDefineException("illegal command " + command);
        }
    }

    public static String dealCustomizeTimeOperator(String command, String cycTime) {
        String result = "";
        String split = null;
        String timeFmtStr = "";
        if (command.startsWith("$[") && command.endsWith("]")) {  //需要计算的变量
            String line = command.substring(2, command.indexOf("]")).trim();
            if (line.startsWith("add_months")) {
                String params = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                String[] paramsArrays = params.split(",");
                timeFmtStr = paramsArrays[0].trim();
                if (paramsArrays.length > 2) {
                    //第三个参数为连接符
                    split = paramsArrays[2].replaceAll("'","");
                }
                if (YEAR_FMT.equals(timeFmtStr)) {
                    String year = StringUtils.deleteWhitespace(paramsArrays[1]);
                    if (!year.contains("*")) {
                        result = plusYear(MathUtil.getIntegerVal(year), cycTime, timeFmtStr);
                    } else {
                        int m = MathUtil.getIntegerVal(year.split("\\*")[0]);
                        result = plusYear(m, cycTime, timeFmtStr);
                    }
                } else if (DAY_FMT.equals(timeFmtStr) || MONTH_FMT.equals(timeFmtStr)) {
                    String months = StringUtils.deleteWhitespace(paramsArrays[1]);
                    if (!months.contains("*")) {
                        result = plusMonth(MathUtil.getIntegerVal(months), cycTime, timeFmtStr);
                    } else {
                        int m = MathUtil.getIntegerVal(months.split("\\*")[0]);
                        int n = MathUtil.getIntegerVal(months.split("\\*")[1]);
                        if (Math.abs(m) != 12 && Math.abs(n) != 12) {
                            throw new RdosDefineException("illegal command " + command);
                        }
                        result = plusMonth(m * n, cycTime, timeFmtStr);
                    }
                }
            } else {  //没有函数
                if (line.contains(",")) {
                    String[] paramsArrays = line.split(",");
                    line = paramsArrays[0];
                    split = String.valueOf(paramsArrays[1]).replaceAll("'", "");
                }
                Matcher matcher = customizePattern.matcher(line);
                if (matcher.find() && matcher.groupCount() == 3) {
                    timeFmtStr = matcher.group(1).trim();
                    String operatorStr = matcher.group(2).trim();
                    String operatorNumStr = StringUtils.deleteWhitespace(matcher.group(3));
                    if (DAY_FMT.equals(timeFmtStr)) {
                        int days = 0;
                        if (!operatorNumStr.contains("*")) {
                            days = MathUtil.getIntegerVal(operatorNumStr);
                        } else if (operatorNumStr.split("\\*").length == 2) {
                            int m = Integer.parseInt(operatorNumStr.split("\\*")[0]);
                            int n = Integer.parseInt(operatorNumStr.split("\\*")[1]);
                            if (m != 7 && n != 7) {
                                throw new RdosDefineException("illegal command " + command);
                            }
                            days = m * n;
                        }
                        if ("-".equals(operatorStr)) {
                            result = minusDay(days, cycTime, timeFmtStr);
                        } else if ("+".equals(operatorStr)) {
                            result = plusDay(days, cycTime, timeFmtStr);
                        }
                    } else if ("hh24miss".equals(timeFmtStr)) {
                        int time;
                        if (operatorNumStr.split("/").length == 2) {  //小时
                            time = MathUtil.getIntegerVal(operatorNumStr.split("/")[0]);
                            if ("-".equals(operatorStr)) {
                                result = minusHour(time, cycTime, STD_FMT);
                            }
                            if ("+".equals(operatorStr)) {
                                result = plusHour(time, cycTime, STD_FMT);
                            }
                        } else if (operatorNumStr.split("/").length == 3) {  //分钟
                            time = MathUtil.getIntegerVal(operatorNumStr.split("/")[0]);
                            if ("-".equals(operatorStr)) {
                                result = minusMinute(time, cycTime, STD_FMT);
                            }
                            if ("+".equals(operatorStr)) {
                                result = plusMinute(time, cycTime, STD_FMT);
                            }
                        }
                    }
                } else if (formattedPattern.matcher(line).matches()) {
                    //时间格式化
                    try {
                        FastDateFormat cycDate = FastDateFormat.getInstance("yyyyMMddHHmmss", TimeZone.getTimeZone("GMT+8"));
                        Date cycd = cycDate.parse(cycTime);
                        if (line.contains("hh24")) {
                            line = line.replaceAll("hh24", "HH");
                        }
                        result = FastDateFormat.getInstance(line, TimeZone.getTimeZone("GMT+8")).format(cycd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (StringUtils.isBlank(result)) {
                throw new RdosDefineException("illegal command " + command);
            }
            if(StringUtils.isNotEmpty(split)&& StringUtils.isNotEmpty(result)){
                if(StringUtils.isBlank(timeFmtStr)){
                    timeFmtStr = line;
                }
                return convertResultWithSplit(result,timeFmtStr,split.trim());
            }
            return result;

        } else if (command.startsWith("${") && command.endsWith("}")) {
            // 支持基于业务日期作为基准取值的格式 时间减一天，其余照原逻辑处理 不多做任何校验
            String yesterdayCycTime = minusDay(1, cycTime, "yyyyMMddHHmmss");
            String normalCommand = command.replaceFirst("\\{", "[").replaceFirst("}", "]");
            return dealCustomizeTimeOperator(normalCommand, yesterdayCycTime);
        } else {
            return command;  //直接返回
        }
    }

    private static String convertResultWithSplit(String result, String timeFmtStr, String split) {
        String fmt = "";
        StringBuilder convertResult = new StringBuilder();
        if ("hh24miss".equals(timeFmtStr)) {
            timeFmtStr = "yyyyMMddHHmmss";
        }
        for (int i = 0; i < timeFmtStr.length(); i++) {
            if (StringUtils.isBlank(fmt)) {
                fmt = String.valueOf(timeFmtStr.charAt(i));
            }
            if (!fmt.equals(String.valueOf(timeFmtStr.charAt(i)))) {
                convertResult.append(split);
                fmt = String.valueOf(timeFmtStr.charAt(i));
            }
            convertResult.append(result.charAt(i));
        }
        return convertResult.toString();
    }

    public static String plusHour(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusHours(n);
        return dateTime.toString(format);
    }

    public static String minusHour(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusHours(n);
        return dateTime.toString(format);
    }

    public static String plusMinute(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusMinutes(n);
        return dateTime.toString(format);
    }

    public static String minusMinute(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusMinutes(n);
        return dateTime.toString(format);
    }

    public static String plusDay(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusDays(n);
        return dateTime.toString(format);
    }

    public static String minusDay(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusDays(n);
        return dateTime.toString(format);
    }

    public static String plusMonth(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusMonths(n);
        return dateTime.toString(format);
    }

    public static String minusMonth(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusMonths(n);
        return dateTime.toString(format);
    }

    public static String plusYear(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.plusYears(n);
        return dateTime.toString(format);
    }

    public static String minusYear(int n, String cycTime, String format) {
        DateTime dateTime = cycTimeFormat.parseDateTime(cycTime);
        dateTime = dateTime.minusYears(n);
        return dateTime.toString(format);
    }


    public static void main(String[] args) throws ParseException {
        String result = TimeParamOperator.transform("yyyyMM - 1", "20171212010101");
        System.out.println(result);
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,10,'-')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-6,'/')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-6,':')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyy,-10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyy,10)]", "20180607010101"));

        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+7*1,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-7*1,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+10,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-10,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-4/24,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24/60,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-3/24/60,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMM,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHH,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmm,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmmss,':']", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24,':']", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24mm,':']", "20180607172233"));



        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,12*2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,-12*1)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,-2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+7*1]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-7*1]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+10]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-10]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-4/24]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24/60]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-3/24/60]", "20180607030000"));

        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[mm]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[dd]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[HH]", "20180607031700"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[MM]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[ss]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMM]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHH]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmm]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmmss]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24mm]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy-MM-dd]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy-MM-dd HH:mm:ss]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[HH:mm:ss]", "20180607172233"));

    }

}
