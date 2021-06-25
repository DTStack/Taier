package com.dtstack.engine.lineage.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.common.client.ClientCache;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.client.IClient;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EComponentTypeDataSourceType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.dao.LineageDataSetDao;
import com.dtstack.engine.lineage.util.DataSourceUtils;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 4:18 下午 2020/10/30
 */
@Service
public class LineageDataSetInfoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(LineageDataSetInfoService.class);

    @Autowired
    private LineageDataSourceService sourceService;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private LineageDataSetDao lineageDataSetDao;


    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private KerberosDao kerberosDao;

    @Autowired
    private ClientOperator clientOperator;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;



    /**
     * @author zyd
     * @Description 根据条件查询表信息，如果没有则新增
     * @Date 2020/10/30 4:20 下午
     * @param sourceId:
     * @param dbName:
     * @param tableName:
     * @param schemaName:
     * @return: com.dtstack.lineage.impl.LineageTableInfoService
     **/
    public LineageDataSetInfo getOneBySourceIdAndDbNameAndTableName(Long sourceId, String dbName, String tableName, String schemaName,Integer appType){

        LineageDataSetInfo lineageDataSetInfo = lineageDataSetDao.getOneBySourceIdAndDbNameAndTableName(sourceId,dbName,tableName,schemaName,appType);
        if(null != lineageDataSetInfo){
            return lineageDataSetInfo;
        }
        //如果没有查到，则新增表信息
        //根据sourceId查询数据源信息
        ApiResponse<DsServiceInfoDTO> dsInfoById = dataSourceAPIClient.getDsInfoById(sourceId);
        if(dsInfoById.getCode() != 1){
            LOGGER.error("getDsInfoById query failed,param:{}",JSON.toJSONString(sourceId));
            throw new RdosDefineException("调用数据源中心根据id查询数据源接口失败");
        }
        if( null == dsInfoById.getData()){
            throw new RdosDefineException("该id对应的数据源在数据源中心不存在");
        }
        DsServiceInfoDTO dsServiceInfoDTO = dsInfoById.getData();
        lineageDataSetInfo = generateDataSet(sourceId, tableName, schemaName, dsServiceInfoDTO, dbName,appType);
        lineageDataSetDao.insertTableInfo(lineageDataSetInfo);
        return lineageDataSetInfo;
    }

    /**
     * @author zyd
     * @Description 根据条件查询表信息，如果没有则新增
     * @Date 2020/10/30 4:20 下午
     * @param sourceId:
     * @param dbName:
     * @param tableName:
     * @param schemaName:
     * @return: com.dtstack.lineage.impl.LineageTableInfoService
     **/
    public List<LineageDataSetInfo> getListByParams(Long sourceId, String dbName, String tableName, String schemaName,Integer appType){

        return lineageDataSetDao.getListByParams(sourceId,dbName,tableName,schemaName,appType);
    }

    private LineageDataSetInfo generateDataSet(Long sourceId, String tableName, String schemaName, DsServiceInfoDTO dataSource, String dbName,Integer appType) {
        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        dataSetInfo.setAppType(appType);
        dataSetInfo.setSourceName(dataSource.getDataName());
        dataSetInfo.setSourceType(dataSource.getType());
        dataSetInfo.setDataInfoId(sourceId);
        dataSetInfo.setDbName(dbName);
        dataSetInfo.setIsManual(0);
        dataSetInfo.setDtUicTenantId(dataSource.getDtuicTenantId());
        if(StringUtils.isNotEmpty(schemaName)){
            dataSetInfo.setSchemaName(schemaName);
        }else {
            dataSetInfo.setSchemaName(dbName);
        }
        dataSetInfo.setSetType(0);
        dataSetInfo.setTableName(tableName);
        //生成tableKey
        String tableKey = generateTableKey(sourceId, dbName, tableName);
        dataSetInfo.setTableKey(tableKey);
        return dataSetInfo;
    }

    private String generateTableKey(Long sourceId, String dbName, String tableName) {

        return sourceId+dbName+tableName;
    }

    public List<Column> getTableColumns(LineageDataSetInfo dataSetInfo){

        //获取数据源信息
        ApiResponse<DsServiceInfoDTO> dsInfoById = dataSourceAPIClient.getDsInfoById(dataSetInfo.getDataInfoId());
        if(dsInfoById.getCode() != 1){
            LOGGER.error("getDsInfoById query failed,param:{}",JSON.toJSONString(dataSetInfo.getDataInfoId()));
            throw new RdosDefineException("调用数据源中心根据id查询数据源接口失败");
        }
        DsServiceInfoDTO dsServiceInfoDTO = dsInfoById.getData();
        if(null == dsServiceInfoDTO){
            throw new RdosDefineException("找不到对应的数据源");
        }
        ClientCache clientCache = ClientCache.getInstance(environmentContext.getPluginPath());
        IClient iClient ;
        try {
            String dataJson = dsServiceInfoDTO.getDataJson();
            JSONObject jsonObject = DataSourceUtils.getDataSourceJson(dataJson);
            Long dtUicTenantId = dsServiceInfoDTO.getDtuicTenantId();
            Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
            JSONObject sftpConf = getJsonObject(EComponentType.SFTP.getTypeCode(),tenantId);
            if(DataSourceUtils.judgeOpenKerberos(dataJson)) {
                handleKerberosConfig(dataJson, jsonObject, tenantId, sftpConf);
            }
            //需要在pluginInfo中补充typeName
            String typeName = DataSourceType.getEngineType(DataSourceType.getSourceType(dsServiceInfoDTO.getType()));
            jsonObject.put("typeName",typeName);
            String pluginInfo = PublicUtil.objToString(jsonObject);
            iClient = getClient(dsServiceInfoDTO, clientCache, pluginInfo);
            return getAllColumns(dataSetInfo, iClient);
        } catch (Exception e) {
            throw new RdosDefineException("获取client异常",e);
        }
    }

    private void handleKerberosConfig(String dataJson, JSONObject jsonObject, Long tenantId, JSONObject sftpConf) {
        JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataJson);
        JSONObject kerberosConfig = dataSourceJson.getJSONObject(DataSourceUtils.KERBEROS_CONFIG);
        //开启kerberos
        //获取yarnConf
        JSONObject yarnConf = getJsonObject(EComponentType.YARN.getTypeCode(), tenantId);
        jsonObject.put("yarnConf",yarnConf);
        jsonObject.put("sftpConf", sftpConf);
        jsonObject.put("remoteDir",kerberosConfig.get("remoteDir"));
        jsonObject.put("principalFile",kerberosConfig.get("principalFile"));
        jsonObject.put("krbName",kerberosConfig.get("krbName"));
        jsonObject.put("principal",kerberosConfig.get("principal"));
        jsonObject.put("kerberosFileTimestamp",kerberosConfig.get("kerberosFileTimestamp"));
        jsonObject.put("openKerberos",true);
    }

    /**
     * @author ZYD
     * @Description 获取组件配置
     * @Date 2021/1/29 11:11
     * @param typeCode:
     * @return: com.alibaba.fastjson.JSONObject
     **/
    private JSONObject getJsonObject(Integer typeCode,Long tenantId) {
        //获取sftp配置
        Component one = componentDao.getByTenantIdComponentType(tenantId, typeCode);
        if(null == one){
            throw new RdosDefineException("该租户没有绑定集群");
        }
        Component component = componentDao.getOne(one.getId());
        List<ComponentConfig> componentConfigs = componentConfigDao.listByComponentId(component.getId(), false);
        if(null == componentConfigs){
            throw new RdosDefineException("sftp配置信息为空");
        }
        Map<String, Object> sftpConfigMap = ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
        return JSONObject.parseObject(JSONObject.toJSONString(sftpConfigMap));
    }

    public List<Column> getAllColumns(LineageDataSetInfo dataSetInfo, IClient iClient) {

        if(null == dataSetInfo){
            return new ArrayList<>();
        }
        return iClient.getAllColumns(dataSetInfo.getTableName(), dataSetInfo.getSchemaName(), dataSetInfo.getDbName());
    }

    public IClient getClient(DsServiceInfoDTO dsServiceInfoDTO, ClientCache clientCache, String pluginInfo) throws ClientAccessException {
        if(null == clientCache || null == dsServiceInfoDTO){
            return null;
        }
        return clientCache.getClient(DataSourceType.getEngineType(DataSourceType.getSourceType(dsServiceInfoDTO.getType())), pluginInfo);
    }

    /**
     * @author zyd
     * @Description 根据id查询表信息
     * @Date 2020/11/11 5:11 下午
     * @param id:
     * @return: com.dtstack.engine.api.domain.LineageDataSetInfo
     **/
    public LineageDataSetInfo getOneById(Long id){

        return lineageDataSetDao.getOneById(id);
    }

    /**
     * @author zyd
     * @Description 根据ids批量查询表信息
     * @Date 2020/11/11 5:14 下午
     * @param ids:
     * @return: com.dtstack.engine.api.domain.LineageDataSetInfo
     **/
    public List<LineageDataSetInfo> getDataSetListByIds(List<Long> ids){

        if(CollectionUtils.isEmpty(ids)){
            throw new RdosDefineException("表id列表不能为空");
        }
        return lineageDataSetDao.getDataSetListByIds(ids);
    }


    /**
     * @author zyd
     * @Description 根据数据源id和table列表查询字段信息
     * @Date 2020/11/13 10:57 上午
     * @param sourceId:
     * @param tables:
     * @return: java.util.Map<java.lang.String,java.util.List<com.dtstack.engine.api.pojo.lineage.Column>>
     **/
    public Map<String,List<Column>> getColumnsBySourceIdAndListTable(Long sourceId, List<Table> tables){

        HashMap<String, List<Column>> listHashMap = new HashMap<>(16);
        if(CollectionUtils.isEmpty(tables)){
            return listHashMap;
        }
        for (Table table : tables) {
            LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
            dataSetInfo.setDbName(table.getDb());
            dataSetInfo.setSchemaName(table.getSchemaName());
            dataSetInfo.setTableName(table.getName());
            dataSetInfo.setDataInfoId(sourceId);
            List<Column> tableColumns = getTableColumns(dataSetInfo);
            listHashMap.put(table.getDb()+"."+table.getName(),tableColumns);
        }
        return listHashMap;
    }

    /**
     * 根据表名和数据源信息修改表名
     * @param oldTableName
     * @param newTableName
     * @param dataSourceId
     */
    public void updateTableNameByTableNameAndSourceId(String oldTableName,String newTableName,String dbName,Long dataSourceId) {

        String oldTableKey = generateTableKey(dataSourceId, dbName, oldTableName);
        String newTableKey = generateTableKey(dataSourceId, dbName, newTableName);
        lineageDataSetDao.updateTableNameByTableNameAndSourceId(newTableName,oldTableKey,newTableKey);
    }
}
