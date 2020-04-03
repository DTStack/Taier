package com.dtstack.engine.master.job.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.dtcenter.common.util.TimeParamOperator;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.BatchTaskShade;
import com.dtstack.engine.api.dto.BatchTaskParamShade;
import com.dtstack.engine.master.job.IJobStartTrigger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yuebai
 * @date 2019-11-05
 */
@Service
public class BatchKylinJobStartTrigger implements IJobStartTrigger {

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, BatchTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        if (taskShade.getEngineType().equals(EngineType.Kylin.getVal())) {
            List<BatchTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String)actionParam.get("taskParamsToReplace"), BatchTaskParamShade.class);
            JSONObject pluginInfo = JSONObject.parseObject((String) actionParam.get("pluginInfo"));
            if (Objects.nonNull(pluginInfo)) {
               String taskExeArgs = taskShade.getExeArgs();
                JSONObject jsonObject = (JSONObject) JSONObject.parse(taskExeArgs);
                if (!jsonObject.getBooleanValue("noPartition")) {
                    long startTime = 0;
                    long endTime = 0;
                    if (!jsonObject.getBooleanValue("isUseSystemVar")) {
                        // 时间转时间戳
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        // 加28800000 是为了加八小时
                        startTime = simpleDateFormat.parse(jsonObject.get("startTime").toString()).getTime() + 28800000;
                        endTime = simpleDateFormat.parse(jsonObject.get("endTime").toString()).getTime() + 28800000;
                    } else if (!jsonObject.getBooleanValue("isUseSystemVar")) {
                        for (BatchTaskParamShade param : taskParamsToReplace) {
                            String paramCommand = param.getParamCommand();
                            String targetVal = TimeParamOperator.transform(paramCommand, scheduleJob.getCycTime());
                            long length = targetVal.length();
                            if (length < 14) {
                                for (int i = 0; i < 14 - length; i++) {
                                    targetVal += "0";
                                }
                            }
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
                            // 加28800000 是为了加八小时
                            startTime = simpleDateFormat1.parse(targetVal).getTime() + 28800000;
                            endTime = simpleDateFormat1.parse(String.valueOf(Long.valueOf(targetVal) + 1)).getTime() + 28800000;
                        }
                    } else {
                        throw new RdosDefineException("任务参数错误");
                    }
                    pluginInfo.put("startTime", startTime);
                    pluginInfo.put("endTime", endTime);
                }
            }

            actionParam.put("pluginInfo", pluginInfo);
        }

    }
}
