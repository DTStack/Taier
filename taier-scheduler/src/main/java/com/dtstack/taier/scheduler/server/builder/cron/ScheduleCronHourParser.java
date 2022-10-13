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
import com.dtstack.taier.scheduler.server.builder.ScheduleConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 小时解析器
 * Date: 2017/5/4
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ScheduleCronHourParser implements IScheduleConfParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleCronHourParser.class);

    private static final String CRON_FORMAT = "0 ${beginMin} ${beginHour}-${endHour}/${gapNum} * * ?";

    @Override
    public String parse(ScheduleConf scheduleConf) {
        if (scheduleConf.getBeginHour() == null) {
            throw new RdosDefineException(String.format(ScheduleConfManager.CustomError.ERROR_INFO, scheduleConf.getPeriodType(), "beginHour"));
        }

        if (scheduleConf.getBeginMin() == null) {
            throw new RdosDefineException(String.format(ScheduleConfManager.CustomError.ERROR_INFO, scheduleConf.getPeriodType(), "beginMin"));
        }

        if (scheduleConf.getEndHour() == null) {
            throw new RdosDefineException(String.format(ScheduleConfManager.CustomError.ERROR_INFO, scheduleConf.getPeriodType(), "endHour"));
        }

        if (scheduleConf.getGapHour() == null) {
            throw new RdosDefineException(String.format(ScheduleConfManager.CustomError.ERROR_INFO, scheduleConf.getPeriodType(), "gapHour"));
        }

        int beginHour = scheduleConf.getBeginHour();
        int endHour = scheduleConf.getEndHour();
        //间隔数最小为1
        int gapNum = scheduleConf.getGapHour();
        int beginMinute = scheduleConf.getBeginMin();

        if (beginHour < 0 || endHour > 23) {
            LOGGER.error("illegal schedule cron for period hour :{}", JSON.toJSONString(scheduleConf));
            return null;
        }

        return CRON_FORMAT.replace("${beginHour}", beginHour + "").replace("${endHour}", endHour + "")
                .replace("${gapNum}", gapNum + "").replace("${beginMin}", beginMinute + "");
    }
}
