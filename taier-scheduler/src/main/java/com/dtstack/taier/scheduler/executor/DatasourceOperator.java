package com.dtstack.taier.scheduler.executor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.DatasourceTypeUtil;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.scheduler.datasource.convert.engine.PluginInfoToSourceDTO;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.service.ScheduleDictService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * rdb executor
 *
 * @author ：wangchuan
 * date：Created in 14:04 2022/10/8
 * company: www.dtstack.com
 */
@Component
public class DatasourceOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceOperator.class);

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    public static final String DATA_SOURCE_TYPE = "dataSourceType";

    public void executeBatchQuery(JobClient jobClient) {
        Long clusterId = clusterService.getClusterIdByTenantId(jobClient.getTenantId());
        Integer componentType = EScheduleJobType.getByTaskType(jobClient.getTaskType()).getComponentType().getTypeCode();
        // 获取组件信息
        com.dtstack.taier.dao.domain.Component component = componentService.getComponentByClusterId(clusterId, componentType, jobClient.getComponentVersion());
        Integer datasourceType = DatasourceTypeUtil.getTypeByComponentAndVersion(componentType, component.getVersionName());
        ISourceDTO sourceDTO = PluginInfoToSourceDTO.getSourceDTO(getPluginInfo(jobClient, datasourceType));
        IClient client = ClientCache.getClient(datasourceType);
        client.executeBatchQuery(sourceDTO, SqlQueryDTO.builder().sql(jobClient.getSql()).build());
    }

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

    private String getPluginInfo(JobClient jobClient, Integer dataSourceType) {
        try {
            String componentVersionValue = scheduleDictService.convertVersionNameToValue(jobClient.getComponentVersion(), jobClient.getTaskType());
            JSONObject info = clusterService.pluginInfoJSON(jobClient.getTenantId(), jobClient.getTaskType(),
                    jobClient.getDeployMode(), componentVersionValue, jobClient.getQueueName());
            if (Objects.isNull(info)) {
                return null;
            }
            info.put(DATA_SOURCE_TYPE, dataSourceType);
            return info.toJSONString();
        } catch (Exception e) {
            LOGGER.error("jobId {} buildPluginInfo failed!", jobClient.getJobId(), e);
            throw new RdosDefineException("buildPluginInfo error", e);
        }
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
