package com.dtstack.taier.scheduler.executor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.util.DatasourceTypeUtil;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.scheduler.datasource.convert.engine.PluginInfoToSourceDTO;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public static final String DATA_SOURCE_TYPE = "dataSourceType";

    public ComponentTestResult testConnect(Integer componentType, String pluginInfo, String versionName) {
        // 获取对应的数据源类型
        Integer dataSourceType = DatasourceTypeUtil.getTypeByComponentAndVersion(componentType, versionName);
        JSONObject jsonObject = JSONObject.parseObject(pluginInfo);
        jsonObject.put(DATA_SOURCE_TYPE, dataSourceType);
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
        Integer dataSourceType = DatasourceTypeUtil.getTypeByComponentAndVersion(EComponentType.HDFS.getTypeCode(), component.getVersionName());
        pluginInfo.put(DatasourceOperator.DATA_SOURCE_TYPE, dataSourceType);
    }
}
