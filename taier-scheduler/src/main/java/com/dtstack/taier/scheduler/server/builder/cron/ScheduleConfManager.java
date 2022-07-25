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

package com.dtstack.taier.scheduler.server.builder.cron;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.scheduler.enums.ESchedulePeriodType;
import com.dtstack.taier.scheduler.server.builder.ScheduleConf;

import java.io.IOException;
import java.text.ParseException;

/**
 * Reason:
 * Date: 2017/5/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleConfManager {

    public static ScheduleCorn parseFromJson(String scheduleConf) throws IOException, ParseException {
        ScheduleConf scheduleConfBean = JSON.parseObject(scheduleConf, ScheduleConf.class);
        // 校验必要参数
        checkConf(scheduleConf, scheduleConfBean);

        // 获得对应的corn表达式
        int periodType = scheduleConfBean.getPeriodType();
        IScheduleConfParser scheduleCron;
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
        }else if (periodType == ESchedulePeriodType.CRON.getVal()){
            scheduleCron = new ScheduleCronCustomParser();
        } else{
            throw new RdosDefineException("not support period type!");
        }
        String cron = scheduleCron.parse(scheduleConfBean);

        // 返回corn对象
        ScheduleCorn corn = new ScheduleCorn();
        corn.setScheduleConf(scheduleConfBean);
        corn.setCron(cron);
        return corn;
    }

    /**
     * 校验必要的参数
     *
     * @param scheduleConf 调度参数
     * @param scheduleConfBean 序列号后的调度对象
     */
    private static void checkConf(String scheduleConf, ScheduleConf scheduleConfBean) {
        if (scheduleConfBean == null) {
            throw new RdosDefineException("scheduleConf illegal:"+ scheduleConf +",Please check the json pass");
        }

        if (scheduleConfBean.getPeriodType() == null) {
            throw new RdosDefineException("schedule param must contain: periodType");
        }

        if (scheduleConfBean.getBeginDate() == null) {
            throw new RdosDefineException("schedule param must contain: beginDate");
        }

        if (scheduleConfBean.getEndDate() == null) {
            throw new RdosDefineException("schedule param must contain: endDate");
        }
    }

    interface CustomError {
        String ERROR_INFO = "when periodType = %s ,schedule param must contain: %s";
    }
}
