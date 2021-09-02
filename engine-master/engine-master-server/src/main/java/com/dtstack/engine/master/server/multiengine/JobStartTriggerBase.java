package com.dtstack.engine.master.server.multiengine;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
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
