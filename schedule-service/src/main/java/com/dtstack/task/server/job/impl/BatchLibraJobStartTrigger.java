package com.dtstack.task.server.job.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.task.domain.BatchJob;
import com.dtstack.task.domain.BatchTaskShade;
import com.dtstack.task.dto.BatchTaskParamShade;
import com.dtstack.task.server.job.IJobStartTrigger;
import com.dtstack.task.server.scheduler.JobParamReplace;
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
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, BatchTaskShade taskShade, BatchJob batchJob) throws Exception {
        String sql = (String)actionParam.getOrDefault("sqlText","");
        List<BatchTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String)actionParam.get("taskParamsToReplace"), BatchTaskParamShade.class);
        actionParam.put("sqlText",jobParamReplace.paramReplace(sql, taskParamsToReplace, batchJob.getCycTime()));
    }
}
