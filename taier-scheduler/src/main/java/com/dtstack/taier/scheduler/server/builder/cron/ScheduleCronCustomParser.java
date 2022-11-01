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

import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.scheduler.server.builder.ScheduleConf;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xinge
 * cron表达式解析
 */
public class ScheduleCronCustomParser implements IScheduleConfParser {

    @Override
    public String parse(ScheduleConf scheduleConf) {
        String cron =scheduleConf.getCorn();
        if (StringUtils.isBlank(cron)) {
            throw new TaierDefineException(String.format(ScheduleConfManager.CustomError.ERROR_INFO,scheduleConf.getPeriodType(),"cron"));
        }
        return cron;
    }

}
