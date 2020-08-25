package com.dtstack.engine.master.multiengine;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.dtstack.schedule.common.enums.AppType;
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
        if (taskShade.getAppType() == AppType.DQ.getType()) {
            actionParam.put("sqlText", sql.replace(TaskConstant.DQ_JOB_ID, scheduleJob.getJobId()));
            return;
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        actionParam.put("sqlText", jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime()));
    }
}
