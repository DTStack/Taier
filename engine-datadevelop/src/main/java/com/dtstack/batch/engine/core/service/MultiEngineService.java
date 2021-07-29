package com.dtstack.batch.engine.core.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.engine.core.domain.MultiEngineFactory;
import com.dtstack.batch.engine.rdbms.hive.util.SparkThriftConnectionUtils;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.datasource.impl.IMultiEngineService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.dtcenter.common.engine.ConsoleSend;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.EScriptType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.EngineService;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 和console交互获取多集群的配置信息
 * Date: 2019/4/25
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class MultiEngineService implements IMultiEngineService {

    private static final Logger LOG = LoggerFactory.getLogger(MultiEngineService.class);

    @Autowired
    private ConsoleSend consoleSend;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Autowired
    public EngineService engineService;

    @Autowired
    public ComponentService componentService;

    // 需要拼接jdbcUrl的引擎类型
    private final static Set<Integer> buildUrlEngineType = Sets.newHashSet(MultiEngineType.HADOOP.getType(), MultiEngineType.LIBRA.getType(),
            MultiEngineType.ANALYTICDB_FOR_PG.getType());

    // 需要拼接schema的引擎类型
    private final static Set<Integer> buildUrlWithSchemaEngineType = Sets.newHashSet(MultiEngineType.LIBRA.getType(), MultiEngineType.ANALYTICDB_FOR_PG.getType());

    @Override
    public List<Integer> getTenantSupportMultiEngine(Long dtuicTenantId) {
        List<EngineSupportVO> engineSupportVOS = consoleSend.listSupportEngine(dtuicTenantId);
        return engineSupportVOS.stream().map(EngineSupportVO::getEngineType).collect(Collectors.toList());
    }

    /**
     * 从console获取Hadoop的meta数据源
     * @param dtuicTenantId
     * @return
     */
    @Override
    public DataSourceType getTenantSupportHadoopMetaDataSource(Long dtuicTenantId) {
        List<EngineSupportVO> engineSupportVOS = consoleSend.listSupportEngine(dtuicTenantId);
        for (EngineSupportVO engineSupportVO : engineSupportVOS) {
            if (MultiEngineType.HADOOP.getType() == engineSupportVO.getEngineType()) {
                if (EComponentType.HIVE_SERVER.getTypeCode() == engineSupportVO.getMetadataComponent()){
                    JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, null, EJobType.HIVE_SQL);
                    SparkThriftConnectionUtils.HiveVersion hiveVersion = SparkThriftConnectionUtils.HiveVersion.getByVersion(jdbcInfo.getVersion());
                    if (SparkThriftConnectionUtils.HiveVersion.HIVE_1x.equals(hiveVersion)){
                        return DataSourceType.HIVE1X;
                    }else if (SparkThriftConnectionUtils.HiveVersion.HIVE_3x.equals(hiveVersion)){
                        return DataSourceType.HIVE3X;
                    }else {
                        return DataSourceType.HIVE;
                    }
                }
                if (EComponentType.SPARK_THRIFT.getTypeCode() == engineSupportVO.getMetadataComponent()){
                    return DataSourceType.SparkThrift2_1;
                }
                if (EComponentType.IMPALA_SQL.getTypeCode() == engineSupportVO.getMetadataComponent()){
                    return DataSourceType.IMPALA;
                }
            }
        }
        throw new RdosDefineException("not find 'Hadoop' meta DataSource!");
    }

    /**
     * @param dtuicTenantId
     * @return
     */
    @Override
    public List<EJobType> getTenantSupportJobType(Long dtuicTenantId, Long projectId) {
        List<Component> engineSupportVOS = componentService.listComponents(dtuicTenantId, null);
        if(CollectionUtils.isEmpty(engineSupportVOS)){
            throw new DtCenterDefException("该租户 console 未配置任何 集群");
        }
        List<Integer> tenantSupportMultiEngine = engineSupportVOS.stream().map(Component::getComponentTypeCode).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(tenantSupportMultiEngine)) {
            List<Integer> usedEngineTypeList = projectEngineService.getUsedEngineTypeList(projectId);

            List<EComponentType> componentTypeByEngineType = MultiEngineFactory.getComponentTypeByEngineType(usedEngineTypeList);
            List<Integer> userEcomponentList = componentTypeByEngineType.stream().map(EComponentType::getTypeCode).collect(Collectors.toList());
            //项目配置  和 租户 支持 引擎交集
            Sets.SetView<Integer> intersection = Sets.intersection(new HashSet<>(tenantSupportMultiEngine), new HashSet<>(userEcomponentList));
            // 任务类型有 顺序
            if (CollectionUtils.isNotEmpty(intersection)) {
                //console 配置组件
                List<EJobType> supportJobTypes = this.convertComponentTypeToJobType(new ArrayList<>(intersection));
                //排序
                supportJobTypes.sort(Comparator.comparing(EJobType::getSort));
                return supportJobTypes;
            }
        }

        return new ArrayList<>();
    }

    /**
     * 根据console端配置配置组件 转换为对应支持job类型
     * @return
     */
    private List<EJobType> convertComponentTypeToJobType(List<Integer> component) {
        List<EJobType> supportType = new ArrayList<>();
        supportType.add(EJobType.WORK_FLOW);
        supportType.add(EJobType.VIRTUAL);
        if(!CollectionUtils.isEmpty(component)){
            if(component.contains(EComponentType.HDFS.getTypeCode()) || component.contains(EComponentType.YARN.getTypeCode())){
                //hdfs 对应hadoopMR
                supportType.add(EJobType.HADOOP_MR);
            }
            if(component.contains(EComponentType.FLINK.getTypeCode())){
                supportType.add(EJobType.SYNC);
            }

            if(component.contains(EComponentType.SPARK.getTypeCode())){
                supportType.add(EJobType.SPARK_SQL);
                supportType.add(EJobType.SPARK);
                supportType.add(EJobType.SPARK_PYTHON);
            }
            if(component.contains(EComponentType.DT_SCRIPT.getTypeCode())){
                supportType.add(EJobType.PYTHON);
                supportType.add(EJobType.SHELL);
            }
            if(component.contains(EComponentType.CARBON_DATA.getTypeCode())){
                supportType.add(EJobType.CARBON_SQL);
            }
            if(component.contains(EComponentType.LIBRA_SQL.getTypeCode())){
                supportType.add(EJobType.LIBRA_SQL);
            }
            if(component.contains(EComponentType.TIDB_SQL.getTypeCode())){
                supportType.add(EJobType.TIDB_SQL);
            }
            if(component.contains(EComponentType.ORACLE_SQL.getTypeCode())){
                supportType.add(EJobType.ORACLE_SQL);
            }
            if(component.contains(EComponentType.HIVE_SERVER.getTypeCode())){
                supportType.add(EJobType.HIVE_SQL);
            }
            if (component.contains(EComponentType.IMPALA_SQL.getTypeCode())) {
                supportType.add(EJobType.IMPALA_SQL);
            }
            if (component.contains(EComponentType.GREENPLUM_SQL.getTypeCode())) {
                supportType.add(EJobType.GREENPLUM_SQL);
            }
            if (component.contains(EComponentType.INCEPTOR_SQL.getTypeCode())) {
                supportType.add(EJobType.INCEPTOR_SQL);
            }
            if (component.contains(EComponentType.DTSCRIPT_AGENT.getTypeCode())) {
                supportType.add(EJobType.SHELL_ON_AGENT);
            }
            if (component.contains(EComponentType.ANALYTICDB_FOR_PG.getTypeCode())) {
                supportType.add(EJobType.ANALYTICDB_FOR_PG);
            }
        }
        return supportType;
    }


    @Override
    public List<EScriptType> getTenantSupportScriptType(Long dtuicTenantId, Long projectId) {
        Set<EScriptType> scriptTypes = new LinkedHashSet<>();
        List<Integer> tenantSupportMultiEngine = this.getTenantSupportMultiEngine(dtuicTenantId);
        if (CollectionUtils.isNotEmpty(tenantSupportMultiEngine)) {
            List<Integer> usedEngineTypeList = projectEngineService.getUsedEngineTypeList(projectId);
            //项目配置  和 租户 支持 引擎交集
            Sets.SetView<Integer> intersection = Sets.intersection(new HashSet<>(tenantSupportMultiEngine), new HashSet<>(usedEngineTypeList));
            if (CollectionUtils.isNotEmpty(intersection)) {
                if (intersection.contains(MultiEngineType.HADOOP.getType())) {
                    scriptTypes.add(EScriptType.SparkSQL);
                    scriptTypes.add(EScriptType.Python_2x);
                    scriptTypes.add(EScriptType.Python_3x);
                    scriptTypes.add(EScriptType.Shell);
                    String  enginePluginInfo = consoleSend.getEnginePluginInfo(dtuicTenantId, MultiEngineType.HADOOP.getType());
                    Map<String,Object>  pluginMap = null;
                    try {
                        pluginMap = PublicUtil.strToMap(enginePluginInfo);
                    } catch (IOException e) {
                        LOG.error("Map转换错误，原因是:{}",e);
                    }
                    if(null!=pluginMap){
                        Iterator<String> iterator = pluginMap.keySet().iterator();
                        while(iterator.hasNext()){
                            if (EComponentType.IMPALA_SQL.getTypeCode() == Integer.valueOf(iterator.next())){
                                scriptTypes.add(EScriptType.ImpalaSQL);
                            }
                        }
                    }
                }
                if (intersection.contains(MultiEngineType.LIBRA.getType())) {
                    scriptTypes.add(EScriptType.LibrASQL);
                }
            }
        }
        return new ArrayList<>(scriptTypes);
    }


    @Override
    public EngineInfo getEnginePluginInfo(Long dtuicTenantId, Integer engineType, Long projectId) {
        String jsonStr = consoleSend.getEnginePluginInfo(dtuicTenantId, engineType);
        if (StringUtils.isEmpty(jsonStr)) {
            throw new RdosDefineException(String.format("该租户 console 集群类型 %d 未配置任何插件.", engineType));
        }

        Map<String, String> pluginMap;
        try {
            pluginMap = JSONObject.parseObject(jsonStr, HashMap.class);
        } catch (Exception e) {
            LOG.error("get engine plugin with tenantId:{}, type:{} , consoleResultJson:{}", dtuicTenantId, engineType, jsonStr);
            throw new RdosDefineException(String.format("解析 console 返回json 失败。原因是：%s", e.getMessage()), e);
        }

        if (MapUtils.isEmpty(pluginMap)) {
            throw new RdosDefineException("解析 console 返回json 为空! consoleResultJson:" + jsonStr);
        }

        EngineInfo engineInfo = MultiEngineFactory.createEngineInfo(engineType);
        //如果是Hadoop、Libra、ADB For PG 引擎，则需要替换jdbc信息中的%s为数据库名称
        if (buildUrlEngineType.contains(engineType)){
            int eComponentType;
            //处理Libra引擎
            if(MultiEngineType.LIBRA.getType() == engineType){
                eComponentType = EComponentType.LIBRA_SQL.getTypeCode();
            } else if(MultiEngineType.ANALYTICDB_FOR_PG.getType() == engineType){
                eComponentType = EComponentType.ANALYTICDB_FOR_PG.getTypeCode();
            } else {
                //处理Hadoop引擎，Hadoop引擎则获取mete数据源
                DataSourceType dataSourceType = this.getTenantSupportHadoopMetaDataSource(dtuicTenantId);
                eComponentType = batchDataSourceService.getEComponentTypeByDataSourceType(dataSourceType.getVal());
                pluginMap.put("metePluginInfo", pluginMap.get(eComponentType + ""));
            }

            ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, engineType);
            String dbName = projectEngine == null? null : projectEngine.getEngineIdentity();
            String pluginInfo = pluginMap.get(eComponentType + "");
            if (StringUtils.isNotBlank(dbName) && StringUtils.isNotBlank(pluginInfo)) {
                JSONObject pluginInfoJsonObj = JSONObject.parseObject(pluginInfo);
                if(pluginInfoJsonObj != null  && pluginInfoJsonObj.getString("jdbcUrl") != null){
                    //用dbName替换console返回的jdbcUrl中的%s
                    pluginInfoJsonObj.put("jdbcUrl", buildUrl(pluginInfoJsonObj.getString("jdbcUrl"), dbName, engineType));
                    pluginMap.put(eComponentType + "", pluginInfoJsonObj.toJSONString());
                    //在下面的init方法中不知道Hadoop的组件类型，所以Hadoop使用单独的字段存放jdbc连接信息
                    if (engineType.equals(MultiEngineType.HADOOP.getType())){
                        pluginMap.put("metePluginInfo", pluginInfoJsonObj.toJSONString());
                    }
                }
            }
        }

        if (Objects.nonNull(engineInfo)) {
            engineInfo.init(pluginMap);
        }
        return engineInfo;
    }

    /**
     * 拼接jdbcUrl中db或者schema信息
     *
     * @param jdbcUrl
     * @param dbName
     * @param engineType
     * @return
     */
    private String buildUrl(String jdbcUrl, String dbName, Integer engineType) {
        if (buildUrlWithSchemaEngineType.contains(engineType)) {
            Map<String, String> params = new HashMap<>();
            params.put("currentSchema", dbName);
            return Engine2DTOService.buildJdbcURLWithParam(jdbcUrl, params);
        }
        return Engine2DTOService.buildUrlWithDb(jdbcUrl, dbName);
    }

}
