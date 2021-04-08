package com.dtstack.lineage.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.common.client.ClientCache;
import com.dtstack.engine.common.client.IClient;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.KerberosDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.lineage.dao.LineageDataSetDao;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.DataSourceType;
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
    public LineageDataSetInfo getOneBySourceIdAndDbNameAndTableName(Long sourceId, String dbName, String tableName, String schemaName){

        LineageDataSetInfo lineageDataSetInfo = lineageDataSetDao.getOneBySourceIdAndDbNameAndTableName(sourceId,dbName,tableName,schemaName);
        if(null != lineageDataSetInfo){
            return lineageDataSetInfo;
        }
        //如果没有查到，则新增表信息
        //根据sourceId查询数据源信息
        LineageDataSource dataSource = sourceService.getDataSourceById(sourceId);
        if(null == dataSource){
            throw new RdosDefineException("该数据源不存在");
        }
        lineageDataSetInfo = generateDataSet(sourceId, tableName, schemaName, dataSource, dbName);
        lineageDataSetDao.insertTableInfo(lineageDataSetInfo);
        return lineageDataSetInfo;
    }

    private LineageDataSetInfo generateDataSet(Long sourceId, String tableName, String schemaName, LineageDataSource dataSource, String dbName) {
        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        BeanUtils.copyProperties(dataSource,dataSetInfo);
        dataSetInfo.setSourceId(sourceId);
        dataSetInfo.setDbName(dbName);
        dataSetInfo.setIsManual(0);
        if(StringUtils.isNotEmpty(schemaName)){
            dataSetInfo.setSchemaName(schemaName);
        }else {
            dataSetInfo.setSchemaName(dbName);
        }
        dataSetInfo.setSetType(0);
        dataSetInfo.setTableName(tableName);
        //生成tableKey
        String tableKey = generateTableKey(dataSource.getId(), dbName, tableName);
        dataSetInfo.setTableKey(tableKey);
        return dataSetInfo;
    }

    private String generateTableKey(Long sourceId, String dbName, String tableName) {

        return sourceId+dbName+tableName;
    }

    public List<Column> getTableColumns(LineageDataSetInfo dataSetInfo){

        //获取数据源信息
        LineageDataSource dataSource = sourceService.getDataSourceById(dataSetInfo.getSourceId());
        if(null == dataSource){
            throw new RdosDefineException("找不到对应的数据源");
        }
        ClientCache clientCache = ClientCache.getInstance();
        IClient iClient ;
        try {
            String kerberosConf = dataSource.getKerberosConf();
            String dataJson = dataSource.getDataJson();
            JSONObject jsonObject = JSON.parseObject(dataJson);
            JSONObject kerberosJsonObj = new JSONObject();
            if(!"-1".equals(kerberosConf)) {
                kerberosJsonObj = JSON.parseObject(kerberosConf);
            }
            Long dtUicTenantId = dataSource.getDtUicTenantId();
            Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
            if(dataSource.getOpenKerberos() == 1 && dataSource.getAppType().equals(AppType.RDOS.getType())){
                //离线开启了kerberos，但是没有存kerberos配置
                Component one = componentDao.getByTenantIdComponentType(tenantId, dataSource.getSourceType());
                if(null == one){
                    throw new RdosDefineException("do not have this component");
                }
                //根据engineId和组件类型获取kerberos配置
                KerberosConfig kerberosConfig = kerberosDao.getByEngineIdAndComponentType(one.getEngineId(),dataSource.getSourceType());
                if(null == kerberosConfig){
                    LOGGER.error("do not have kerberos config,dtUicTenantId:{},engineId:{},sourceType:{}",dtUicTenantId,one.getEngineId(),dataSource.getSourceType());
                    throw new RdosDefineException("do not have kerberos config");
                }
                kerberosJsonObj.put("remoteDir",kerberosConfig.getRemotePath());
                kerberosJsonObj.put("principalFile",kerberosConfig.getPrincipal());
                kerberosJsonObj.put("krbName",kerberosConfig.getKrbName());
                kerberosJsonObj.put("principal",kerberosConfig.getPrincipals());
            }

            JSONObject sftpConf = getJsonObject(dataSource,EComponentType.SFTP.getTypeCode(),tenantId);
            if(dataSource.getOpenKerberos()==1) {
                //开启kerberos
                //获取yarnConf
                JSONObject yarnConf = getJsonObject(dataSource,EComponentType.YARN.getTypeCode(),tenantId);
                jsonObject.put("yarnConf",yarnConf);
                jsonObject.put("sftpConf", sftpConf);
                jsonObject.put("remoteDir",kerberosJsonObj.get("remoteDir"));
                jsonObject.put("principalFile",kerberosJsonObj.get("principalFile"));
                jsonObject.put("krbName",kerberosJsonObj.get("krbName"));
                jsonObject.put("principal",kerberosJsonObj.get("principal"));
                jsonObject.put("kerberosFileTimestamp",kerberosJsonObj.get("kerberosFileTimestamp"));
                jsonObject.put("openKerberos",true);
            }
            if(dataSource.getAppType() == AppType.DATAASSETS.getType()){
                //资产类型需要在pluginInfo中补充typeName
                String typeName = DataSourceType.getEngineType(DataSourceType.getSourceType(dataSource.getSourceType()));
                jsonObject.put("typeName",typeName);
            }
            String pluginInfo = PublicUtil.objToString(jsonObject);
            iClient = getClient(dataSource, clientCache, pluginInfo);
            return getAllColumns(dataSetInfo, iClient);
        } catch (Exception e) {
            throw new RdosDefineException("获取client异常",e);
        }
    }

    /**
     * @author ZYD
     * @Description 获取组件配置
     * @Date 2021/1/29 11:11
     * @param dataSource:
     * @param typeCode:
     * @return: com.alibaba.fastjson.JSONObject
     **/
    private JSONObject getJsonObject(LineageDataSource dataSource,Integer typeCode,Long tenantId) {
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

    public IClient getClient(LineageDataSource dataSource, ClientCache clientCache, String pluginInfo) throws ClientAccessException {
        if(null == clientCache || null == dataSource){
            return null;
        }
        return clientCache.getClient(DataSourceType.getEngineType(DataSourceType.getSourceType(dataSource.getSourceType())), pluginInfo);
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
            dataSetInfo.setSourceId(sourceId);
            List<Column> tableColumns = getTableColumns(dataSetInfo);
            listHashMap.put(table.getDb()+"."+table.getName(),tableColumns);
        }
        return listHashMap;
    }

    }
