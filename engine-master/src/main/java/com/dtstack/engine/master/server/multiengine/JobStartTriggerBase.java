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

package com.dtstack.engine.master.server.multiengine;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.master.server.scheduler.JobParamReplace;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2019-11-05
 */
@Component
public class JobStartTriggerBase {

    @Resource
    private JobParamReplace jobParamReplace;

    public void readyForTaskStartTrigger(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        String sql = (String) actionParam.getOrDefault("sqlText", "");
        //对于DQ的任务采用不同的替换方式
        if (StringUtils.isNotBlank(sql) && sql.contains(TaskConstant.DQ_JOB_ID)) {
            sql = sql.replace(TaskConstant.DQ_JOB_ID, scheduleJob.getJobId());
        }

        if (StringUtils.isNotBlank(sql) && sql.contains(TaskConstant.DQ_FLOW_JOB_ID)) {
            sql = sql.replace(TaskConstant.DQ_FLOW_JOB_ID, scheduleJob.getFlowJobId());
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        actionParam.put("sqlText", jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime()));
    }
}
