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

package com.dtstack.taiga.develop.engine.hdfs.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taiga.common.annotation.Forbidden;
import com.dtstack.taiga.common.engine.JdbcInfo;
import com.dtstack.taiga.common.enums.MultiEngineType;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.develop.engine.core.service.MultiEngineService;
import com.dtstack.taiga.develop.engine.rdbms.common.HadoopConf;
import com.dtstack.taiga.develop.engine.rdbms.service.IJdbcService;
import com.dtstack.taiga.develop.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.taiga.develop.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.taiga.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taiga.develop.service.tenant.ITenantService;
import com.google.common.collect.Lists;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * hadoop集群项目相关的操作
 * Date: 2019/5/5
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
public class HadoopTenantService implements ITenantService {


    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private MultiEngineService multiEngineService;

    @Override
    public List<String> getRetainDB(Long tenantId, Long userId) throws Exception {
        DataSourceType metaDataSourceType = multiEngineService.getTenantSupportHadoopMetaDataSource(tenantId);
        //hadoop引擎可能未配置spark thriftServer
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()));
        if (StringUtils.isEmpty(jdbcInfo.getJdbcUrl())){
            return Lists.newArrayList();
        }
        List<String> dbList = jdbcServiceImpl.getAllDataBases(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), null);
        return dbList;
    }

    @Override
    public List<String> getDBTableList(Long tenantId, Long userId, String dbName) {
        DataSourceType metaDataSourceType = multiEngineService.getTenantSupportHadoopMetaDataSource(tenantId);
        List<String> tableList = jdbcServiceImpl.getTableList(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), dbName);
        return tableList;
    }

    /**
     * 获取表名列表
     *
     * @param tenantId
     * @return
     * @throws Exception
     */
    public List<String> getTableNameList(Long tenantId, String database)  {
        DataSourceType metaDataSourceType = datasourceService.getHadoopDefaultDataSourceByTenantId(tenantId);
        List<String> tables = jdbcServiceImpl.getTableList(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), database);
        return tables;
    }

    @Forbidden
    private void initDefaultSource(Long tenantId, String tenantName, Integer dataSourceType, String tenantDesc, Long userId, String dbName) throws IOException, SftpException {
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, userId, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType));
        String jdbcUrl = jdbcInfo.getJdbcUrl();
        JSONObject dataJson = new JSONObject();
        dataJson.put("username",jdbcInfo.getUsername());
        dataJson.put("password",jdbcInfo.getPassword());
        if (!jdbcUrl.contains("%s")) {
            throw new RdosDefineException("控制台 HiveServer URL 不包含占位符 %s");
        }
        jdbcUrl = String.format(jdbcUrl, dbName);
        dataJson.put("jdbcUrl", jdbcUrl);
        String defaultFs = HadoopConf.getDefaultFs(tenantId);
        if (StringUtils.isNotBlank(defaultFs)) {
            dataJson.put("defaultFS", defaultFs);
        }else {
            throw new RdosDefineException("默认数据源的defaultFs未找到");
        }

        JSONObject hdpConfig = createHadoopConfigObject(tenantId);
        if (!hdpConfig.isEmpty()) {
            dataJson.put("hadoopConfig", hdpConfig.toJSONString());
        }

        String dataSourceName = tenantName + "_" + MultiEngineType.HADOOP.name();
        dataJson.put("hasHdfsConfig", true);

        JdbcInfo hive = Engine2DTOService.getJdbcInfo(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType));
        JSONObject kerberosConfig = hive.getKerberosConfig();
        if (Objects.nonNull(kerberosConfig)) {
            Map<String, String> sftpMap = datasourceService.getSftpMap(tenantId);
            String remotePath = kerberosConfig.getString("remotePath");
            kerberosConfig.put("remotePath", remotePath.replaceAll(sftpMap.get("path"), ""));
            kerberosConfig.put("hive.server2.authentication", "KERBEROS");
            dataJson.put("kerberosConfig", hive.getKerberosConfig());
        }
        datasourceService.createMateDataSource(tenantId, userId, dataJson.toJSONString(), dataSourceName, dataSourceType, tenantDesc, dbName);
    }

    //TODO 需要抽取出来一个公用的方法
    @Forbidden
    public JSONObject createHadoopConfigObject(Long tenantId) {
        JSONObject hadoop = new JSONObject();
        Map<String, Object> config = HadoopConf.getConfiguration(tenantId);
        String nameServices = config.getOrDefault("dfs.nameservices","").toString();
        if (StringUtils.isNotBlank(nameServices)) {
            hadoop.put("dfs.nameservices", nameServices);
            String nameNodes = config.getOrDefault(String.format("dfs.ha.namenodes.%s", nameServices),"").toString();
            if (StringUtils.isNotBlank(nameNodes)) {
                hadoop.put(String.format("dfs.ha.namenodes.%s", nameServices), nameNodes);
                for (String nameNode : nameNodes.split(",")) {
                    String key = String.format("dfs.namenode.rpc-address.%s.%s", nameServices, nameNode);
                    hadoop.put(key, config.get(key));
                }
            }
            String failoverKey = String.format("dfs.client.failover.proxy.provider.%s", nameServices);
            hadoop.put(failoverKey, config.get(failoverKey));
        }

        return hadoop;
    }

}
