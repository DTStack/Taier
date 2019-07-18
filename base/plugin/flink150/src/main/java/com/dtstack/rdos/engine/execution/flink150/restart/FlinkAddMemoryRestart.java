package com.dtstack.rdos.engine.execution.flink150.restart;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.restart.IExtractStrategy;
import com.dtstack.rdos.engine.execution.flink150.FlinkPerJobResourceInfo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: maqi
 * @create: 2019/07/17 17:36
 */
public class FlinkAddMemoryRestart implements IExtractStrategy {

    private static Logger logger = LoggerFactory.getLogger(FlinkAddMemoryRestart.class);

    private static String TASK_PARAMS_KEY = "taskParams";

    private static String SEPARATOR = "=";

    private static String RUN_MODE_KEY = "flinkTaskRunMode";

    private static String PER_JOB_MODE = "perJob";

    private static int DEFAULT_TASKMANAGER_MEMORY = 512;

    private static int DEFAULT_INCREASE_FACTOR = 2;

    @Override
    public String restart(String taskParams) {
        //1. 解析任务参数
        try {
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(taskParams, Map.class);
            String tps = String.valueOf(pluginInfoMap.getOrDefault(TASK_PARAMS_KEY, ""));
            Map<String, Object> params = splitStr(tps, SEPARATOR);
            // change run mode
            params.put(RUN_MODE_KEY, PER_JOB_MODE);

            Integer memory = MathUtil.getIntegerVal(params.getOrDefault(FlinkPerJobResourceInfo.TASKMANAGER_MEMORY_MB, DEFAULT_TASKMANAGER_MEMORY));

            params.put(FlinkPerJobResourceInfo.TASKMANAGER_MEMORY_MB, memory * DEFAULT_INCREASE_FACTOR);

            pluginInfoMap.putAll(params);

            return PublicUtil.mapToObject(pluginInfoMap, String.class);
        } catch (IOException e) {
            logger.error("", e);
        }

        //2. 重试日志更新
        return null;
    }

    public Map<String, Object> splitStr(String str, String Separator) {
        Map<String, Object> res = Maps.newHashMap();

        for (String s : str.split("\n")) {
            String[] keyAndVal = str.split(Separator);
            if (keyAndVal.length > 1) {
                res.put(keyAndVal[0], keyAndVal[1]);
            }
        }

        return res;
    }

}