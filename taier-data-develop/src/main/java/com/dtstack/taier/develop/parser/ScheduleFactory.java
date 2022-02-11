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

package com.dtstack.taier.develop.parser;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.MathUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.MapUtils;
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

    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static final String BEGIN_DATE_KEY = "beginDate";

    private static final String END_DATE_KEY = "endDate";

    private static final String PERIOD_TYPE_KEY = "periodType";

    private static final String SELFRELIANCE_KEY = "selfReliance";

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
        }else if(periodType == ESchedulePeriodType.CRON.getVal()) {
            scheduleCron = new ScheduleCronParser();
        }else{
            throw new RdosDefineException("not support period type!");
        }

        String beginDateStr = (String) jsonMap.get(BEGIN_DATE_KEY);
        String endDateStr = (String) jsonMap.get(END_DATE_KEY);
        if(jsonMap.containsKey(SELFRELIANCE_KEY) ){
            String obj = MapUtils.getString(jsonMap, SELFRELIANCE_KEY);
            Integer type  = MathUtil.getIntegerVal(obj);
            scheduleCron.setSelfReliance(type);
        }

        DateTime beginDateTime = timeFormatter.parseDateTime(beginDateStr + " 00:00:00");
        DateTime endDateTime = timeFormatter.parseDateTime(endDateStr + " 23:59:59");
        scheduleCron.setBeginDate(beginDateTime.toDate());
        scheduleCron.setEndDate(endDateTime.toDate());

        scheduleCron.setPeriodType(periodType);
        scheduleCron.parse(jsonMap);
        return scheduleCron;
    }
}
