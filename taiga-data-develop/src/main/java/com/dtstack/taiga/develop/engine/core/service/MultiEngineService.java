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

package com.dtstack.taiga.develop.engine.core.service;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.enums.EJobType;
import com.dtstack.taiga.common.enums.MultiEngineType;
import com.dtstack.taiga.common.exception.DtCenterDefException;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.Component;
import com.dtstack.taiga.develop.engine.core.domain.MultiComponentFactory;
import com.dtstack.taiga.develop.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.taiga.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taiga.develop.service.datasource.impl.IMultiEngineService;
import com.dtstack.taiga.develop.service.impl.TenantComponentService;
import com.dtstack.taiga.scheduler.service.ComponentService;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private TenantComponentService tenantEngineService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    public ComponentService componentService;

    // 需要拼接jdbcUrl的引擎类型
    private final static Set<Integer> buildUrlEngineType = Sets.newHashSet(MultiEngineType.HADOOP.getType(), MultiEngineType.LIBRA.getType(),
            MultiEngineType.ANALYTICDB_FOR_PG.getType());

    // 需要拼接schema的引擎类型
    private final static Set<Integer> buildUrlWithSchemaEngineType = Sets.newHashSet(MultiEngineType.LIBRA.getType(), MultiEngineType.ANALYTICDB_FOR_PG.getType());

    @Override
    public List<Integer> getTenantSupportMultiEngine(Long dtuicTenantId) {
        return null;
    }

    /**
     * 从console获取Hadoop的meta数据源
     * @param tenantId
     * @return
     */
    @Override
    public DataSourceType getTenantSupportHadoopMetaDataSource(Long tenantId) {
        Integer metaComponent = Engine2DTOService.getMetaComponent(tenantId);
        if (EComponentType.SPARK_THRIFT.getTypeCode().equals(metaComponent)){
            return DataSourceType.SparkThrift2_1;
        }
        throw new RdosDefineException("not find 'Hadoop' meta DataSource!");
    }

    /**
     * @param tenantId
     * @return
     */
    @Override
    public List<EJobType> getTenantSupportJobType(Long tenantId) {
        List<Component> engineSupportVOS = componentService.listComponents(tenantId);
        if(CollectionUtils.isEmpty(engineSupportVOS)){
            throw new DtCenterDefException("该租户 console 未配置任何 集群");
        }
        List<Integer> tenantSupportMultiEngine = engineSupportVOS.stream().map(Component::getComponentTypeCode).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(tenantSupportMultiEngine)) {
            List<Integer> usedEngineTypeList = tenantEngineService.getUsedEngineTypeList(tenantId);

            List<EComponentType> componentTypeByEngineType = MultiComponentFactory.getComponentTypeByEngineType(usedEngineTypeList);
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
                supportType.add(EJobType.GaussDB_SQL);
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
