package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.KerberosConfig;
import com.dtstack.engine.master.vo.KerberosConfigVO;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.datasource.facade.datasource.ApiServiceFacade;
import com.dtstack.engine.datasource.param.datasource.api.EditConsoleParam;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceInfoVO;
import com.dtstack.engine.common.enums.DataSourceType;
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

    @Autowired
    private ApiServiceFacade apiServiceFacade;

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
        if (null == apiServiceFacade) {
            LOGGER.info("datasource url is not init so skip");
            return;
        }
        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            return;
        }
        Component component = componentDao.getByEngineIdAndComponentType(engineId, componentTypeCode);
        if (null == component) {
            LOGGER.info("engineId {} componentType {} component is null", engineId, componentTypeCode);
            return;
        }
        DataSourceType dataSourceType = DataSourceType.convertEComponentType(EComponentType.getByCode(componentTypeCode), component.getHadoopVersion());
        if(null == dataSourceType){
            LOGGER.info("engineId {} componentType {} is not support datasource ", engineId, componentTypeCode);
            return;
        }
        EditConsoleParam editConsoleParam = new EditConsoleParam();
        try {
            editConsoleParam = getEditConsoleParam(clusterId, componentTypeCode, dtUicTenantIds, component);
            editConsoleParam.setType(dataSourceType.getVal());
            apiServiceFacade.editConsoleDs(editConsoleParam);
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
        if (StringUtils.isNotBlank(component.getKerberosFileName())) {
            //kerberos 配置信息
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentTypeCode, ComponentVersionUtil.formatMultiVersion(componentTypeCode,component.getHadoopVersion()));
            KerberosConfigVO kerberosConfigVO = clusterService.addKerberosConfigWithHdfs(componentTypeCode, clusterId, kerberosConfig);
            editConsoleParam.setKerberosConfig(JSONObject.parseObject(JSONObject.toJSONString(kerberosConfigVO)));
        }

        JSONObject hdfsConfig = componentService.getComponentByClusterId(clusterId, EComponentType.HDFS.getTypeCode(), false, JSONObject.class, null);
        if (null != hdfsConfig) {
            editConsoleParam.setHdfsConfig(hdfsConfig);
        }
        return editConsoleParam;
    }

    public String loadJdbcInfo(String pluginInfo) {
        if (null == apiServiceFacade) {
            LOGGER.info("datasource url is not init so skip");
            return pluginInfo;
        }

        LOGGER.info("pluginInfo:{}",pluginInfo);

        if (StringUtils.isNotBlank(pluginInfo)) {
            try {
                JSONObject pluginInfoObj = JSON.parseObject(pluginInfo);

                Long dataSourceId = pluginInfoObj.getLong(JdbcInfoConst.DATA_SOURCE_ID);
                if (dataSourceId != null) {
                    DsServiceInfoVO data = apiServiceFacade.getDsInfoById(dataSourceId);
                    if (data != null) {
                        String dataJson = data.getDataJson();
                        LOGGER.info("dataJson:{}",dataJson);
                        JSONObject dataSourceInfo = JSON.parseObject(dataJson);
                        pluginInfoObj.putAll(dataSourceInfo);
                    }
                }
                return pluginInfoObj.toJSONString();
            } catch (Exception e) {
                LOGGER.error("load dtDataSourceId{} error ", pluginInfo, e);
                return pluginInfo;
            }
        } else {
            return pluginInfo;
        }
    }

    interface JdbcInfoConst {
        String TYPE_NAME = "typeName";
        String DATA_SOURCE_ID = "dtDataSourceId";
    }
}
