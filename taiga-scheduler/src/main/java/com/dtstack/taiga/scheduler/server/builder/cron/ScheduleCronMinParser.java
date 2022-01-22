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

package com.dtstack.taiga.scheduler.server.builder.cron;

import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.scheduler.server.builder.ScheduleConf;

/**
 * 分钟时间解析,默认开始分钟是0, 不允许修改
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ScheduleCronMinParser implements IScheduleConfParser {

    private static final String cronFormat = "0 0/${gapMin} ${beginHour}-${endHour} * * ?";

    @Override
    public String parse(ScheduleConf scheduleConf) {
        if (scheduleConf.getBeginHour() == null) {
            throw new RdosDefineException(String.format(ScheduleConfManager.CustomError.ERROR_INFO,scheduleConf.getPeriodType(),"beginHour"));
        }

        if (scheduleConf.getEndHour() == null) {
            throw new RdosDefineException(String.format(ScheduleConfManager.CustomError.ERROR_INFO,scheduleConf.getPeriodType(),"endHour"));
        }

        if (scheduleConf.getGapMin() == null) {
            throw new RdosDefineException(String.format(ScheduleConfManager.CustomError.ERROR_INFO,scheduleConf.getPeriodType(),"gapMin"));
        }

        int beginHour = scheduleConf.getBeginHour();
        int endHour = scheduleConf.getEndHour();
        int gapNum = scheduleConf.getGapMin();

        return cronFormat.replace("${gapMin}", gapNum + "")
                .replace("${beginHour}", beginHour + "").replace("${endHour}", endHour + "");
    }
}
