package com.dtstack.engine.master.multiengine.engine;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskParamShade;
import com.dtstack.engine.master.multiengine.JobStartTriggerBase;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.schedule.common.util.TimeParamOperator;
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
public class KylinJobStartTrigger extends JobStartTriggerBase {

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        if (taskShade.getEngineType().equals(ScheduleEngineType.Kylin.getVal())) {
            List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String)actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
            Map<String,Object> pluginInfo = (Map<String,Object>) actionParam.get("pluginInfo");
            if (null != pluginInfo) {
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
                    } else if (jsonObject.getBooleanValue("isUseSystemVar")) {
                        for (ScheduleTaskParamShade param : taskParamsToReplace) {
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
                        throw new RdosDefineException("Task parameter error");
                    }
                    pluginInfo.put("startTime", startTime);
                    pluginInfo.put("endTime", endTime);
                }
            }

            actionParam.put("pluginInfo", pluginInfo);
        }

    }
}
