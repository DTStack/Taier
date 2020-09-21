package com.dtstack.engine.master.scheduler.parser;

import com.dtstack.engine.common.enums.DependencyType;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MathUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.MapUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/5/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleFactory {

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static final String BEGIN_DATE_KEY = "beginDate";

    private static final String END_DATE_KEY = "endDate";

    private static final String PERIOD_TYPE_KEY = "periodType";

    private static final String SELFRELIANCE_KEY = "selfReliance";

    private static final String MAX_RETRY_NUM = "maxRetryNum";

    public static ScheduleCron parseFromJson(String jsonStr) throws IOException, ParseException {
        Map<String, Object> jsonMap = objMapper.readValue(jsonStr, Map.class);
        Preconditions.checkState(jsonMap.containsKey(PERIOD_TYPE_KEY), "schedule param must contain " + PERIOD_TYPE_KEY);
        Preconditions.checkNotNull(jsonMap.containsKey(BEGIN_DATE_KEY), "schedule param must contain " +  BEGIN_DATE_KEY);
        Preconditions.checkNotNull(jsonMap.containsKey(END_DATE_KEY), "schedule param must contain " +  END_DATE_KEY);

        int periodType = MathUtil.getIntegerVal(jsonMap.get(PERIOD_TYPE_KEY));
        ScheduleCron scheduleCron = null;

        if(periodType == ESchedulePeriodType.MONTH.getVal()){
            scheduleCron = new ScheduleCronMonthParser();
        }else if(periodType == ESchedulePeriodType.WEEK.getVal()){
            scheduleCron = new ScheduleCronWeekParser();
        }else if(periodType == ESchedulePeriodType.DAY.getVal()){
            scheduleCron = new ScheduleCronDayParser();
        }else if(periodType == ESchedulePeriodType.HOUR.getVal()){
            scheduleCron = new ScheduleCronHourParser();
        }else if(periodType == ESchedulePeriodType.MIN.getVal()){
            scheduleCron = new ScheduleCronMinParser();
        }else{
            throw new RdosDefineException("not support period type!");
        }

        String beginDateStr = (String) jsonMap.get(BEGIN_DATE_KEY);
        String endDateStr = (String) jsonMap.get(END_DATE_KEY);
        if(jsonMap.containsKey(SELFRELIANCE_KEY) ){
            String obj = jsonMap.get(SELFRELIANCE_KEY).toString();
            Integer type = 0;
            if("true".equals(obj)){
                type = DependencyType.SELF_DEPENDENCY_SUCCESS.getType();
            }else if("false".equals(obj)){
                type = DependencyType.NO_SELF_DEPENDENCY.getType();
            }else {
                type = MathUtil.getIntegerVal(obj);
            }
            scheduleCron.setSelfReliance(type);
        }

        DateTime beginDateTime = TIME_FORMATTER.parseDateTime(beginDateStr + " 00:00:00");
        DateTime endDateTime = TIME_FORMATTER.parseDateTime(endDateStr + " 23:59:59");
        scheduleCron.setBeginDate(beginDateTime.toDate());
        scheduleCron.setEndDate(endDateTime.toDate());

        scheduleCron.setPeriodType(periodType);
        scheduleCron.parse(jsonMap);

        scheduleCron.setMaxRetryNum(MapUtils.getInteger(jsonMap, MAX_RETRY_NUM, 0));

        return scheduleCron;
    }
}
