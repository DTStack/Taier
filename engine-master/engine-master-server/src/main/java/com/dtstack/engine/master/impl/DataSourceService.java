package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.vo.KerberosConfigVO;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.datasource.EditConsoleParam;
import com.dtstack.schedule.common.enums.DataSourceType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * @author yuebai
 * @date 2021-04-06
 */
@Service
public class DataSourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceService.class);

    @Autowired(required = false)
    private DataSourceAPIClient dataSourceAPIClient;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private KerberosDao kerberosDao;


    /**
     * 将最新的sql组件的jdbc信息提交到数据源中心
     *
     * @param engineId
     * @param componentTypeCode
     * @param dtUicTenantIds
     */
    public void publishSqlComponent(Long clusterId, Long engineId, Integer componentTypeCode, Set<Long> dtUicTenantIds) {
        if (null == dataSourceAPIClient) {
            LOGGER.info("datasource url is not init so skip");
            return;
        }
        if(CollectionUtils.isEmpty(dtUicTenantIds)){
            return;
        }

        Component component = componentDao.getByEngineIdAndComponentType(engineId, componentTypeCode);
        if (null == component) {
            LOGGER.info("engineId {} componentType {} component is null", engineId, componentTypeCode);
            return;
        }
        EditConsoleParam editConsoleParam = new EditConsoleParam();
        try {
            editConsoleParam = getEditConsoleParam(clusterId, componentTypeCode, dtUicTenantIds, component);
            dataSourceAPIClient.editConsoleDs(editConsoleParam);
            LOGGER.info("update datasource jdbc engineId {} componentType {} component info {}", engineId, componentTypeCode, editConsoleParam.toString());
        } catch (Exception e) {
            LOGGER.error("update datasource jdbc engineId {} componentType {} component info {} error ", engineId, componentTypeCode, editConsoleParam.toString(), e);
            throw new RdosDefineException(ExceptionUtil.getErrorMessage(e));
        }
    }

    private EditConsoleParam getEditConsoleParam(Long clusterId, Integer componentTypeCode, Set<Long> dtUicTenantIds, Component component) {
        Map<String, Object> configMap = componentConfigService.convertComponentConfigToMap(component.getId(), false);
        //推送数据源中心
        EditConsoleParam editConsoleParam = new EditConsoleParam();
        editConsoleParam.setDsDtuicTenantIdList(new ArrayList<>(dtUicTenantIds));
        editConsoleParam.setJdbcUrl((String) configMap.get(ConfigConstant.JDBCURL));
        editConsoleParam.setUsername((String) configMap.get(ConfigConstant.USERNAME));
        editConsoleParam.setPassword((String) configMap.get(ConfigConstant.PASSWORD));
        editConsoleParam.setDataVersion(component.getHadoopVersion());
        JSONObject sftpConfig = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, JSONObject.class,null);
        editConsoleParam.setSftpConf(sftpConfig);
        DataSourceType dataSourceType = DataSourceType.convertEComponentType(EComponentType.getByCode(componentTypeCode), component.getHadoopVersion());
        if (null != dataSourceType) {
            editConsoleParam.setType(dataSourceType.getVal());
        }
        if (StringUtils.isNotBlank(component.getKerberosFileName())) {
            //kerberos 配置信息
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentTypeCode,component.getHadoopVersion());
            KerberosConfigVO kerberosConfigVO = clusterService.addKerberosConfigWithHdfs(componentTypeCode, clusterId, kerberosConfig);
            editConsoleParam.setKerberosConfig(JSONObject.parseObject(JSONObject.toJSONString(kerberosConfigVO)));
        }
        return editConsoleParam;
    }
}
