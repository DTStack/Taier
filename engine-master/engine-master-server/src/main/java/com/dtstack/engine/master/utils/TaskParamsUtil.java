package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.EngineType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @Auther: dazhi
 * @Date: 2020/11/23 11:21 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskParamsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskParamsUtil.class);

    /**
     * 除了flink任务有perjob和session之分外，
     * 其他任务默认全部为perjob模式
     * @param taskParams
     * @param computeType
     * @param engineType
     * @return
     */
    public static EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType, String engineType) {
        if (StringUtils.isBlank(engineType) || !EngineType.isFlink(engineType)){
            return EDeployMode.PERJOB;
        }
        return parseDeployTypeByTaskParams(taskParams, computeType);
    }

    /**
     * 解析对应数据同步任务的环境参数 获取对应数据同步模式
     * @param taskParams
     * @return
     */
    public static EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType) {
        try {
            if (!org.apache.commons.lang.StringUtils.isBlank(taskParams)) {
                Properties properties = com.dtstack.engine.common.util.PublicUtil.stringToProperties(taskParams);
                String flinkTaskRunMode = properties.getProperty("flinkTaskRunMode");
                if (!org.apache.commons.lang.StringUtils.isEmpty(flinkTaskRunMode)) {
                    if (flinkTaskRunMode.equalsIgnoreCase("session")) {
                        return EDeployMode.SESSION;
                    } else if (flinkTaskRunMode.equalsIgnoreCase("per_job")) {
                        return EDeployMode.PERJOB;
                    } else if (flinkTaskRunMode.equalsIgnoreCase("standalone")) {
                        return EDeployMode.STANDALONE;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(" parseDeployTypeByTaskParams {} error", taskParams, e);
        }
        if (ComputeType.STREAM.getType().equals(computeType)) {
            return EDeployMode.PERJOB;
        } else {
            return EDeployMode.SESSION;
        }
    }
}
