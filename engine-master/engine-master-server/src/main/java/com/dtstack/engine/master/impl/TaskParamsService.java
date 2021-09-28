/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.impl;

import com.dtstack.engine.pluginapi.enums.ComputeType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.pluginapi.enums.EDeployMode;
import com.dtstack.engine.pluginapi.enums.EngineType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Auther: dazhi
 * @Date: 2020/11/23 11:21 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class TaskParamsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskParamsService.class);

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private EnvironmentContext environmentContext;

    /**
     * 集群配置了standalone 以standalone 为优先
     * 其他的走正常逻辑
     *
     *
     * 除了flink任务有perjob和session之分外，
     * 其他任务默认全部为perjob模式
     * @param taskParams
     * @param computeType
     * @param engineType
     * @param tenantId uic 租户id
     * @return
     */
    public EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType, String engineType,Long tenantId) {
        if (StringUtils.isBlank(engineType) || !EngineType.isFlink(engineType)){
            return EDeployMode.PERJOB;
        }
        if(environmentContext.checkStandalone() && clusterService.hasStandalone(tenantId, EComponentType.FLINK.getTypeCode())){
            return EDeployMode.STANDALONE;
        }
        return parseDeployTypeByTaskParams(taskParams, computeType);
    }

    /**
     * 解析对应数据同步任务的环境参数 获取对应数据同步模式
     * @param taskParams
     * @return
     */
    public EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType) {
        try {
            if (!StringUtils.isBlank(taskParams)) {
                Properties properties = PublicUtil.stringToProperties(taskParams);
                String flinkTaskRunMode = properties.getProperty("flinkTaskRunMode");
                if (!StringUtils.isEmpty(flinkTaskRunMode)) {
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


    public Map<String, Object> convertPropertiesToMap(String taskParams) {
        try {
            Properties properties = PublicUtil.stringToProperties(taskParams);
            return new HashMap<String, Object>((Map) properties);
        } catch (IOException e) {
            LOGGER.error("convertPropertiesToMap {} error", taskParams, e);
        }
        return new HashMap<>();
    }
}
