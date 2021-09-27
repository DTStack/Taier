package com.dtstack.engine.master.server.multiengine.engine;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.ScheduleEngineType;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.dtstack.engine.master.server.multiengine.JobStartTriggerBase;
import com.dtstack.engine.common.util.TimeParamOperatorUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2019-11-05
 */
@Service
public class KylinJobStartTrigger extends JobStartTriggerBase {

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {
        if (taskShade.getEngineType().equals(ScheduleEngineType.Kylin.getVal())) {
            List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
            Map<String, Object> pluginInfo = (Map<String, Object>) actionParam.get("pluginInfo");
            if (MapUtils.isEmpty(pluginInfo)) {
                throw new RdosDefineException("pluginInfo 不能为空");
            }
            JSONObject exeArgs = JSONObject.parseObject(taskShade.getExeArgs());
            if (null != exeArgs) {
                if (!exeArgs.getBooleanValue("noPartition")) {
                    parseStartEndTime(scheduleJob, taskParamsToReplace, exeArgs);
                }
            }
            JSONObject pluginInfoObj = PublicUtil.mapToObject(pluginInfo, JSONObject.class);
            //目前只支持 BUILD 类型
            pluginInfoObj.put("buildType", "BUILD");
            //默认false
            pluginInfoObj.put("forceMergeEmptySegment", Boolean.FALSE);
            pluginInfoObj.putAll(exeArgs);
            actionParam.put("pluginInfo", pluginInfoObj);
        }

    }

    /**
     * 根据exeArgs 解析出 startTime  和 endTime
     * @param scheduleJob
     * @param taskParamsToReplace
     * @param exeArgs
     */
    private void parseStartEndTime(ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace, JSONObject exeArgs) {
        long startTime;
        long endTime;
        if (exeArgs.getBooleanValue("isUseSystemVar")) {
            //使用系统参数 cycTime
            if (CollectionUtils.isEmpty(taskParamsToReplace)) {
                throw new RdosDefineException("系统参数不能为空");
            }
            //cycTime转化为对应的时间格式
            String paramCommand = taskParamsToReplace.get(0).getParamCommand();
            String transform = TimeParamOperatorUtil.transform(paramCommand, scheduleJob.getCycTime());
            //其中kylinUI上默认的时间是8点整。startTime 和endTime都需要是每天的8点整的毫秒数
            DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
            startTime = DateTime.parse(transform, yyyyMMdd).plusHours(8).getMillis();
            endTime = DateTime.parse(transform,yyyyMMdd).plusHours(8).plusSeconds(1).getMillis();
        } else {
            //使用传入的值
            startTime = DateTime.parse(exeArgs.getString("startTime")).getMillis();
            endTime = DateTime.parse(exeArgs.getString("endTime")).getMillis();
        }
        exeArgs.put("startTime", startTime);
        exeArgs.put("endTime", endTime);
    }
}
