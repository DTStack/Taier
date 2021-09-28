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

package com.dtstack.engine.master.server.scheduler.parser;

import com.dtstack.engine.pluginapi.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

/**
 * @author xinge
 * cron表达式解析
 */
public class ScheduleCronCustomParser extends ScheduleCron{



    @Override
    public String parse(Map<String, Object> param) {
        String cron =(String) param.get("cron");
        if (StringUtils.isNotBlank(cron)){
            setCronStr(cron);
            return null;
        }
        return null;
    }

    @Override
    public List<String> getTriggerTime(String specifyDate) throws ParseException {
        // Spring解析cron
        CronExpression expression = new CronExpression(getCronStr());
        LocalDate startDate = DateUtil.parseDate(specifyDate, DateUtil.DATE_FORMAT).toInstant()
                .atZone(DateUtil.DEFAULT_ZONE).toLocalDate();
        // 开始日期,提前1s可能出现cron需要从一天开始之时执行
        Date startDateTime= new Date(startDate.atStartOfDay().plusSeconds(-1)
                .toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());
        // 结束日期
        Date endDateTime = new Date(startDate.plusDays(1).atStartOfDay()
                .toInstant(DateUtil.DEFAULT_ZONE).toEpochMilli());
        List<String> triggerTimeList = new ArrayList<>();

        Date curDateTime = expression.getNextValidTimeAfter(startDateTime);
        //  构建每个触发时间
        while (curDateTime.before(endDateTime) && curDateTime.after(startDateTime)){
            String curDateStr = DateUtil.getDate(curDateTime, DateUtil.STANDARD_DATETIME_FORMAT);
            triggerTimeList.add(curDateStr);
            curDateTime = expression.getNextValidTimeAfter(curDateTime);
        }
        return triggerTimeList;
    }

    @Override
    public boolean checkSpecifyDayCanExe(String specifyDate) throws ParseException {
        return true;
    }


}
