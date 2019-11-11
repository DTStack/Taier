package com.dtstack.engine.dtscript.execution.flink150.restart;

import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.restart.IJobRestartStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @description:
 * @author: maqi
 * @create: 2019/07/17 17:36
 */
public class FlinkUndoRestart implements IJobRestartStrategy {

    private static Logger logger = LoggerFactory.getLogger(FlinkUndoRestart.class);

    private static final String TASK_PARAMS_KEY = "taskParams";

    @Override
    public String restart(String jobInfo, int retryNum, String lastRetryParams) {
        if (StringUtils.isEmpty(lastRetryParams)) {
            return jobInfo;
        }

        try {
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(jobInfo, Map.class);
            pluginInfoMap.put(TASK_PARAMS_KEY, lastRetryParams);
            return PublicUtil.objToString(pluginInfoMap);
        } catch (Exception e) {
            logger.error("", e);
        }

        return jobInfo;
    }

}