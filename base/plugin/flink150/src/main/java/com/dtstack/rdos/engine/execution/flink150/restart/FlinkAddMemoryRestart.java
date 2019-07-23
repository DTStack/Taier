package com.dtstack.rdos.engine.execution.flink150.restart;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.restart.IJobRestartStrategy;
import com.dtstack.rdos.engine.execution.flink150.FlinkPerJobResourceInfo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: maqi
 * @create: 2019/07/17 17:36
 */
public class FlinkAddMemoryRestart implements IJobRestartStrategy {

    private static Logger logger = LoggerFactory.getLogger(FlinkAddMemoryRestart.class);

    private static String TASK_PARAMS_KEY = "taskParams";

    private static String SEPARATOR = "=";

    private static String RUN_MODE_KEY = "flinkTaskRunMode";

    private static String PER_JOB_MODE = "perJob";

    private static int DEFAULT_TASKMANAGER_MEMORY = 1024;


    @Override
        public String restart(String taskParams, int retryNum) {
        try {
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(taskParams, Map.class);
            String tps = String.valueOf(pluginInfoMap.getOrDefault(TASK_PARAMS_KEY, ""));
            Map<String, Object> params = splitStr(tps, SEPARATOR);
            // change run mode
            params.put(RUN_MODE_KEY, PER_JOB_MODE);

            int times = retryNum > 2 ? 3 : retryNum + 1;

            params.put(FlinkPerJobResourceInfo.TASKMANAGER_MEMORY_MB, (times + 1) * DEFAULT_TASKMANAGER_MEMORY );

            pluginInfoMap.put(TASK_PARAMS_KEY, mapToString(params));

            return PublicUtil.objToString(pluginInfoMap);
        } catch (IOException e) {
            logger.error("", e);
        }
        return taskParams;
    }

    public Map<String, Object> splitStr(String str, String Separator) {
        Map<String, Object> res = Maps.newHashMap();

        for (String s : str.split("\n")) {
            String[] keyAndVal = s.split(Separator);
            if (keyAndVal.length > 1) {
                res.put(keyAndVal[0], keyAndVal[1]);
            }
        }

        return res;
    }

    public String mapToString(Map<String, Object> maps) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Object> entity: maps.entrySet()) {
            sb.append(entity.getKey()).append("=").append(entity.getValue()).append("\n");
        }
        return sb.toString();
    }

}