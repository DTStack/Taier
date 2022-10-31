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

package com.dtstack.taier.scheduler.server.pluginInfo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.util.ComponentVersionUtil;
import com.dtstack.taier.dao.domain.KerberosConfig;
import com.dtstack.taier.dao.mapper.ComponentMapper;
import com.dtstack.taier.dao.mapper.ConsoleKerberosMapper;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.KERBEROS_FILE_TIMESTAMP;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.KRB_NAME;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.MERGE_KRB5_CONTENT_KEY;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.OPEN_KERBEROS;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.PRINCIPAL;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.PRINCIPAL_FILE;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.REMOTE_DIR;

public class KerberosPluginInfo implements SecurityPluginInfo {

    private ComponentPluginInfoStrategy componentPluginInfoStrategy;
    private ConsoleKerberosMapper consoleKerberosMapper;
    private ComponentMapper componentMapper;

    public KerberosPluginInfo(ComponentPluginInfoStrategy componentPluginInfoStrategy, ConsoleKerberosMapper consoleKerberosMapper, ComponentMapper componentMapper) {
        this.componentPluginInfoStrategy = componentPluginInfoStrategy;
        this.consoleKerberosMapper = consoleKerberosMapper;
        this.componentMapper = componentMapper;
    }

    @Override
    public JSONObject configSecurity(JSONObject clusterConfigJson, Long clusterId, Integer deployMode) {
        JSONObject pluginJson;
        JSONObject yarnConfig = clusterConfigJson.getJSONObject(EComponentType.YARN.getConfName());
        Integer componentTypCode = componentPluginInfoStrategy.getComponentTypeCode().getTypeCode();
        String versionName = ComponentVersionUtil.isMultiVersionComponent(componentTypCode) ? componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentTypCode) : null;
        KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(clusterId, componentTypCode, versionName);
        pluginJson = componentPluginInfoStrategy.convertPluginInfo(clusterConfigJson, clusterId, deployMode);
        if (null != kerberosConfig) {
            Integer openKerberos = kerberosConfig.getOpenKerberos();
            String remotePath = kerberosConfig.getRemotePath();
            Preconditions.checkState(StringUtils.isNotEmpty(remotePath), "kerberos remotePath can not be null");
            pluginJson.fluentPut(OPEN_KERBEROS, null != openKerberos && openKerberos > 0)
                    .fluentPut(REMOTE_DIR, remotePath)
                    .fluentPut(PRINCIPAL_FILE, kerberosConfig.getName())
                    .fluentPut(PRINCIPAL, kerberosConfig.getPrincipal())
                    .fluentPut(KRB_NAME, kerberosConfig.getKrbName())
                    .fluentPut(KERBEROS_FILE_TIMESTAMP, kerberosConfig.getGmtModified())
                    .fluentPut(MERGE_KRB5_CONTENT_KEY, kerberosConfig.getMergeKrbContent());
            //如果 hiveSQL  impalaSQL中没有yarnConf 需要添加yarnConf做kerberos认证
            pluginJson.putIfAbsent(EComponentType.YARN.getConfName(), yarnConfig);
        }
        return pluginJson;
    }
}
