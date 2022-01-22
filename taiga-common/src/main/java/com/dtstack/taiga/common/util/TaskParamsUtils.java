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

package com.dtstack.taiga.common.util;

import com.dtstack.taiga.pluginapi.enums.ComputeType;
import com.dtstack.taiga.pluginapi.enums.EDeployMode;
import com.dtstack.taiga.pluginapi.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class TaskParamsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskParamsUtils.class);

    /**
     * 解析对应数据同步任务的环境参数 获取对应数据同步模式
     * @param taskParams
     * @return
     */
    public static EDeployMode parseDeployTypeByTaskParams(String taskParams, Integer computeType) {
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
