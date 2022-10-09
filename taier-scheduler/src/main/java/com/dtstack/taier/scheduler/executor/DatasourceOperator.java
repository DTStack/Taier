package com.dtstack.taier.scheduler.executor;

import com.alibaba.fastjson.JSONObject;
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
}
