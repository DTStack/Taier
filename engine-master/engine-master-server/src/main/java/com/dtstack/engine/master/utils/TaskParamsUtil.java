package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.master.enums.EDeployMode;
import com.dtstack.engine.master.impl.ActionService;
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

    private static final Logger logger = LoggerFactory.getLogger(TaskParamsUtil.class);

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
            logger.error(" parseDeployTypeByTaskParams {} error", taskParams, e);
        }
        if (ComputeType.STREAM.getType().equals(computeType)) {
            return EDeployMode.PERJOB;
        } else {
            return EDeployMode.SESSION;
        }
    }
}
