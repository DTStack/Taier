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

package com.dtstack.taiga.develop.parser;


import com.dtstack.taiga.common.util.MathUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 按月周期调度
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleCronMonthParser extends ScheduleCron {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleCronMonthParser.class);

    private static final String cronFormat = "0 ${min} ${hour} ${days} * ?";

    private static final String MINUTE_KEY = "min";

    private static final String HOUR_KEY = "hour";

    private static final String DAY_KEY = "day";

    private int hour = 0;

    private int minute = 0;

    @Override
    public String parse(Map<String, Object> param) {
        Preconditions.checkState(param.containsKey(MINUTE_KEY), MINUTE_KEY + " not be null!");
        Preconditions.checkState(param.containsKey(HOUR_KEY), HOUR_KEY + " not be null!");
        Preconditions.checkState(param.containsKey(DAY_KEY), DAY_KEY + " not be null!");

        minute = MathUtil.getIntegerVal(param.get(MINUTE_KEY));
        hour = MathUtil.getIntegerVal(param.get(HOUR_KEY));
        String days = MapUtils.getString(param, DAY_KEY);

        String cronStr = cronFormat.replace("${min}", minute + "").replace("${hour}", hour + "")
                .replace("${days}", days+"");

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
    public boolean checkSpecifyDayCanExe(String specifyDate) {
        int day = MathUtil.getIntegerVal(specifyDate.substring(8,10));
        String canExeDay = CronStrUtil.getDayStr(getCronStr());
        if(canExeDay == null){
            logger.error("error cronStr:{}", getCronStr());
            return false;
        }

        boolean canExe = false;
        for(String tmpDay : canExeDay.split(",")){
            tmpDay = tmpDay.trim();
            if(tmpDay.startsWith("0")){
                tmpDay = tmpDay.substring(1);
            }

            if(tmpDay.equals(day + "")){
                return true;
            }
        }

        return canExe;
    }

}
