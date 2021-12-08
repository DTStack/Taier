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

package com.dtstack.engine.master;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.mapper.ScheduleTaskShadeDao;
import com.dtstack.engine.pluginapi.pojo.ParamAction;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.pluginapi.enums.EngineType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ScheduleDictService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.common.enums.Deleted;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.DEPLOY_MODEL;

@Component
public class PluginWrapper{

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginWrapper.class);

    private static final String PARAMS_DELIM = "&";
    private static final String URI_PARAMS_DELIM = "?";
    private static final String LADP_USER_NAME = "ldapUserName";
    private static final String LADP_PASSWORD = "ldapPassword";
    private static final String DB_NAME = "dbName";
    private static final String CLUSTER = "cluster";

    private static final String QUEUE = "queue";
    private static final String NAMESPACE = "namespace";
    public static final String PLUGIN_INFO = "pluginInfo";

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    public Map<String, Object> wrapperPluginInfo(ParamAction action) throws Exception{

        Map actionParam = PublicUtil.objectToMap(action);
        Long tenantId = action.getTenantId();
        String engineType = action.getEngineType();
        /*if (null == deployMode && ScheduleEngineType.Flink.getEngineName().equalsIgnoreCase(engineType)) {
            //解析参数
            deployMode = taskParamsService.parseDeployTypeByTaskParams(action.getTaskParams(),action.getComputeType(), EngineType.Flink.name(),tenantId).getType();
        }
        String componentVersionValue = scheduleDictService.convertVersionNameToValue(action.getComponentVersion(), action.getEngineType());
        // 需要传入组件版本,涉及Null值构建Map需要检验Null兼容
        return clusterService.pluginInfoJSON(tenantId, engineType, action.getUserId(),deployMode,
                Collections.singletonMap(EngineTypeComponentType.engineName2ComponentType(engineType),componentVersionValue));*/
        return null;
    }


    public String getPluginInfo(String taskParams, Integer computeType, String engineType, Long tenantId, Long userId, Map<String, String> pluginInfoCache,String componentVersion) {
        /*try {
            Integer deployMode = null;
            if (ScheduleEngineType.Flink.getEngineName().equalsIgnoreCase(engineType)) {
                //解析参数
                deployMode = taskParamsService.parseDeployTypeByTaskParams(taskParams, computeType,EngineType.Flink.name(),tenantId).getType();
            }
            String componentVersionValue = scheduleDictService.convertVersionNameToValue(componentVersion, engineType);
            String cacheKey = String.format("%s.%s.%s.%s.%s", tenantId, engineType, userId, deployMode,componentVersion);
            Integer finalDeployMode = deployMode;
            return pluginInfoCache.computeIfAbsent(cacheKey, (k) -> {
                JSONObject infoJSON = clusterService.pluginInfoJSON(tenantId, engineType, userId, finalDeployMode,
                        StringUtils.isBlank(componentVersionValue)?null:Collections.singletonMap(EngineTypeComponentType.getByEngineName(engineType).getComponentType().getTypeCode(),componentVersionValue));
                if (Objects.nonNull(infoJSON)) {
                    return infoJSON.toJSONString();
                }
                return "";
            });

        } catch (Exception e) {
            LOGGER.error("getPluginInfo tenantId {} engineType {} error ", tenantId, engineType,e);
        }*/
        return "";
    }
}
