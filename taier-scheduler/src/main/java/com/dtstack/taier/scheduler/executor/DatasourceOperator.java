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

package com.dtstack.taier.scheduler.executor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.FileStatus;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.pluginapi.pojo.FileResult;
import com.dtstack.taier.scheduler.datasource.convert.engine.PluginInfoToSourceDTO;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * rdb executor
 *
 * @author ：wangchuan
 * date：Created in 14:04 2022/10/8
 * company: www.dtstack.com
 */
@Component
public class DatasourceOperator {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterService clusterService;

    public ComponentTestResult testConnect(Integer dataSourceType, String pluginInfo) {
        // 获取对应的数据源类型
        JSONObject jsonObject = JSONObject.parseObject(pluginInfo);
        jsonObject.put(ConfigConstant.DATA_SOURCE_TYPE, dataSourceType);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(jsonObject.toJSONString());
        IClient client = ClientCache.getClient(dataSourceType);
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            Boolean result = client.testCon(sourceDTO);
            componentTestResult.setResult(result);
        } catch (Exception e) {
            componentTestResult.setResult(Boolean.FALSE);
            componentTestResult.setErrorMsg(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : ExceptionUtil.getErrorMessage(e));
        }
        return componentTestResult;
    }

    /**
     * 上传 string 到 hdfs 路径
     *
     * @param pluginInfo pluginInfo
     * @param tenantId   租户 id
     * @param strContent str 信息
     * @param hdfsPath   hdfs 路径
     * @return 路径
     */
    public String uploadToHdfs(JSONObject pluginInfo, Long tenantId, String strContent, String hdfsPath) {
        handleHdfsPluginInfo(pluginInfo, tenantId);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString());
        return ClientCache.getHdfs(sourceDTO.getSourceType()).uploadStringToHdfs(sourceDTO, strContent, hdfsPath);
    }

    /**
     * 上传字节数组到 hdfs 路径
     *
     * @param pluginInfo pluginInfo
     * @param tenantId   租户 id
     * @param bytes      字节数组
     * @param hdfsPath   hdfs 路径
     */
    public void uploadInputStreamToHdfs(JSONObject pluginInfo, Long tenantId, byte[] bytes, String hdfsPath) {
        handleHdfsPluginInfo(pluginInfo, tenantId);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString());
        ClientCache.getHdfs(sourceDTO.getSourceType()).uploadInputStreamToHdfs(sourceDTO, bytes, hdfsPath);
    }


    /**
     * 处理 pluginInfo 添加 dataSourceType
     *
     * @param pluginInfo pluginInfo
     * @param tenantId   租户信息
     */
    public void handleHdfsPluginInfo(JSONObject pluginInfo, Long tenantId) {
        Long clusterId = clusterService.getClusterIdByTenantId(tenantId);
        // 获取组件信息
        com.dtstack.taier.dao.domain.Component component = componentService.getComponentByClusterId(clusterId, EComponentType.HDFS.getTypeCode(), null);
        // 获取对应的数据源类型
        pluginInfo.put(ConfigConstant.DATA_SOURCE_TYPE, component.getDatasourceType());
    }

    /**
     * 上传 string 到 hdfs 路径
     *
     * @param pluginInfo pluginInfo
     * @param tenantId   租户 id
     * @param hdfsPath   hdfs 路径
     * @return 路径
     */
    public List<FileResult> listFiles(JSONObject pluginInfo, Long tenantId, String hdfsPath, boolean isPathPattern) {
        handleHdfsPluginInfo(pluginInfo, tenantId);
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(pluginInfo.toJSONString());
        List<FileStatus> statuses = ClientCache.getHdfs(sourceDTO.getSourceType()).listFiles(sourceDTO, hdfsPath, isPathPattern);
        List<FileResult> results = Lists.newArrayList();
        for (FileStatus status : statuses) {
            FileResult fileResult = new FileResult();
            fileResult.setBlockSize(status.getBlocksize());
            fileResult.setName(status.getPath());
            fileResult.setPath(status.getPath());
            fileResult.setOwner(status.getOwner());
            fileResult.setModificationTime(status.getModification_time());
            results.add(fileResult);
        }
        return results;
    }
}
