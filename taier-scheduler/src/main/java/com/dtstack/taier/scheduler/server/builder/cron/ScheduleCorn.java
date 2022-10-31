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

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.dtstack.taier.scheduler.server.builder.ScheduleConf;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.cronutils.model.CronType.QUARTZ;

/**
 * @Auther: dazhi
 * @Date: 2021/12/30 7:05 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleCorn {

    private final CronDefinition CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 调度配置
     */
    private ScheduleConf scheduleConf;

    /**
     * 获得距离date的上一次执行最近的时间
     * @param date 时间
     * @return 上一次执行最近的时间
     */
    public Date last(Date date) {
        if (date == null) {
            return null;
        }

        CronParser parser = new CronParser(CRON_DEFINITION);
        Cron quartzCron = parser.parse(cron);

        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        ZonedDateTime zonedDateTime = executionTime.lastExecution(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())).orElse(null);

        if (zonedDateTime == null) {
            return null;
        }
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 获得距离date的下一次执行最近的时间
     *
     * @param date 时间
     * @return 下一次执行最近的时间
     */
    public Date next(Date date) {
        if (date == null) {
            return null;
        }

        CronParser parser = new CronParser(CRON_DEFINITION);
        Cron quartzCron = parser.parse(cron);

        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        ZonedDateTime zonedDateTime = executionTime.nextExecution(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())).orElse(null);

        if (zonedDateTime == null) {
            return null;
        }
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 判断当前时间是否是执行时间
     * @param date 当前时间
     */
    public Boolean isMatch(Date date) {
        if (date == null) {
            return null;
        }

        CronParser parser = new CronParser(CRON_DEFINITION);
        Cron quartzCron = parser.parse(cron);

        ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);
        return executionTime.isMatch(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public ScheduleConf getScheduleConf() {
        return scheduleConf;
    }

    public void setScheduleConf(ScheduleConf scheduleConf) {
        this.scheduleConf = scheduleConf;
    }
}
