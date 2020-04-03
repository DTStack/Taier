package com.dtstack.engine.master.job.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.BatchTaskShade;
import com.dtstack.engine.api.dto.BatchTaskParamShade;
import com.dtstack.engine.master.job.IJobStartTrigger;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2019-11-05
 */
@Service
public class BatchLibraJobStartTrigger implements IJobStartTrigger {

    @Autowired
    private JobParamReplace jobParamReplace;

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, BatchTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        String sql = (String)actionParam.getOrDefault("sqlText","");
        List<BatchTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String)actionParam.get("taskParamsToReplace"), BatchTaskParamShade.class);
        actionParam.put("sqlText",jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime()));
    }
}
