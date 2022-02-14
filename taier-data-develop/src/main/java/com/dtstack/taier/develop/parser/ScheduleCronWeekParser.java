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

import com.dtstack.taier.common.util.MathUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 按周调度解析
 * 1-7 ==> sun-sat
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleCronWeekParser extends ScheduleCron {

    private static final String cronFormat = "0 ${min} ${hour} ? * ${weekday}";

    private static final String MINUTE_KEY = "min";

    private static final String HOUR_KEY = "hour";

    private static final String WEEKDAY_KEY = "weekDay";

    private int hour = 0;

    private int minute = 0;

    @Override
    public String parse(Map<String, Object> param) {

        Preconditions.checkState(param.containsKey(MINUTE_KEY), MINUTE_KEY + " not be null!");
        Preconditions.checkState(param.containsKey(HOUR_KEY), HOUR_KEY + " not be null!");
        Preconditions.checkState(param.containsKey(WEEKDAY_KEY), WEEKDAY_KEY + " not be null!");

        minute = MathUtil.getIntegerVal(param.get(MINUTE_KEY));
        hour = MathUtil.getIntegerVal(param.get(HOUR_KEY));
        String weekDay = MathUtil.getString(param.get(WEEKDAY_KEY));

        String cronStr = cronFormat.replace("${min}", minute + "").replace("${hour}", hour + "")
                .replace("${weekday}", weekDay);

        setCronStr(cronStr);
        return cronStr;
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {

        if(!checkSpecifyDayCanExe(specifyDate)){
            return Lists.newArrayList();
        }

        List<String> resultList = Lists.newArrayList();
        String triggerTime = specifyDate + " " + getTimeStr(hour) + ":" + getTimeStr(minute) + ":00";
        resultList.add(triggerTime);
        return resultList;
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) throws ParseException {
        Date date = dayFormatter.parseDateTime(specifyDate).toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int targetDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(targetDayOfWeek == 1){
            targetDayOfWeek = 7;
        }else{
            targetDayOfWeek = targetDayOfWeek - 1;
        }

        String canExeDay = CronStrUtil.getDayOfWeekStr(getCronStr());
        boolean canExe = false;
        for(String tmpDay : canExeDay.split(",")){
            tmpDay = tmpDay.trim();
            if(tmpDay.startsWith("0")){
                tmpDay = tmpDay.substring(1);
            }
            if(tmpDay.trim().equals(targetDayOfWeek + "")){
                return true;
            }
        }

        return canExe;
    }
}
