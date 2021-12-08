///*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.dtstack.batch.engine.hdfs.service;
//
//import com.alibaba.fastjson.JSONObject;
//import com.dtstack.batch.common.enums.ETableType;
//import com.dtstack.batch.common.enums.ProjectCreateModel;
//import com.dtstack.batch.common.exception.RdosDefineException;
//import com.dtstack.engine.domain.User;
//import com.dtstack.batch.engine.core.service.MultiEngineService;
//import com.dtstack.batch.engine.rdbms.common.HadoopConf;
//import com.dtstack.batch.engine.rdbms.service.IJdbcService;
//import com.dtstack.batch.engine.rdbms.service.ITableService;
//import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
//import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
//import com.dtstack.batch.service.project.IProjectService;
//import com.dtstack.batch.vo.ProjectEngineVO;
//import com.dtstack.dtcenter.common.annotation.Forbidden;
//import com.dtstack.dtcenter.common.engine.JdbcInfo;
//import com.dtstack.dtcenter.common.enums.MultiEngineType;
//import com.dtstack.dtcenter.loader.source.DataSourceType;
//import com.dtstack.engine.master.impl.UserService;
//import com.google.common.collect.Lists;
//import com.jcraft.jsch.SftpException;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * hadoop集群项目相关的操作
// * Date: 2019/5/5
// * Company: www.dtstack.com
// * @author xuchao
// */
//
//@Service
//public class HadoopProjectService implements IProjectService {
//
//    public static Logger LOG = LoggerFactory.getLogger(HadoopProjectService.class);
//
//    @Autowired
//    private IJdbcService jdbcServiceImpl;
//
//    @Autowired
//    private ITableService iTableServiceImpl;
//
////    @Autowired
////    private BatchDataSourceService batchDataSourceService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private MultiEngineService multiEngineService;
//
////    @Override
////    public int createProject(Long projectId, String projectName, String projectDesc, Long userId, Long tenantId,
////                             Long dtuicTenantId, ProjectEngineVO projectEngineVO) throws Exception {
////
////        //从console获取Hadoop默认数据源的类型
////        DataSourceType dataSourceType = multiEngineService.getTenantSupportHadoopMetaDataSource(dtuicTenantId);
////        String dbName = projectName.toLowerCase();
////        if (ProjectCreateModel.intrinsic.getType().equals(projectEngineVO.getCreateModel())) {
////            //如果是对接已有数据源，则先初始化项目默认hive数据源，这样可以避免在循环同步hive表的时候频繁调用Engine获取mete数据源类型
////            dbName = projectEngineVO.getDatabase();
////        } else {
////            // 如果是新建数据源，则需要先在hive中创建db，本地再初始化数据源
////            iTableServiceImpl.createDatabase(dtuicTenantId, null, dbName, ETableType.getDatasourceType(dataSourceType.getVal()), projectDesc);
////        }
////        initDefaultSource(dtuicTenantId, projectId, projectName, dataSourceType.getVal(), projectDesc, tenantId, userId, dbName);
////        return 1;
////    }
//
//    @Override
//    public List<String> getRetainDB(Long dtuicTenantId,Long userId) throws Exception {
//        DataSourceType metaDataSourceType = multiEngineService.getTenantSupportHadoopMetaDataSource(dtuicTenantId);
//        //hadoop引擎可能未配置spark thriftServer
//        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()));
//        if (StringUtils.isEmpty(jdbcInfo.getJdbcUrl())){
//            return Lists.newArrayList();
//        }
//        List<String> dbList = jdbcServiceImpl.getAllDataBases(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), null);
//        return dbList;
//    }
//
//    @Override
//    public List<String> getDBTableList(Long dtuicTenantId,Long userId, String dbName, Long projectId) {
//        DataSourceType metaDataSourceType = multiEngineService.getTenantSupportHadoopMetaDataSource(dtuicTenantId);
//        List<String> tableList = jdbcServiceImpl.getTableList(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), dbName);
//        return tableList;
//    }
//
//    /**
//     * 获取表名列表
//     *
//     * @param dtuicTenantId
//     * @return
//     * @throws Exception
//     */
////    public List<String> getTableNameList(Long dtuicTenantId, String database, Long projectId)  {
////        DataSourceType metaDataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
////        List<String> tables = jdbcServiceImpl.getTableList(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), database);
////        return tables;
////    }
//
////    @Forbidden
////    private void initDefaultSource(Long dtuicTenantId, Long projectId, String projectName, Integer dataSourceType, String projectDesc, Long tenantId, Long userId, String dbName) throws IOException, SftpException {
////        Long dtUiceUser = null;
////        User user = userService.getById(userId);
////        if (user != null) {
////            dtUiceUser = user.getDtuicUserId();
////        }
////        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, dtUiceUser, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType));
////        String jdbcUrl = jdbcInfo.getJdbcUrl();
////        JSONObject dataJson = new JSONObject();
////        dataJson.put("username",jdbcInfo.getUsername());
////        dataJson.put("password",jdbcInfo.getPassword());
////        if (!jdbcUrl.contains("%s")) {
////            throw new RdosDefineException("控制台 HiveServer URL 不包含占位符 %s");
////        }
////        jdbcUrl = String.format(jdbcUrl, dbName);
////        dataJson.put("jdbcUrl", jdbcUrl);
////        String defaultFs = HadoopConf.getDefaultFs(dtuicTenantId);
////        if (StringUtils.isNotBlank(defaultFs)) {
////            dataJson.put("defaultFS", defaultFs);
////        }else {
////            throw new RdosDefineException("默认数据源的defaultFs未找到");
////        }
////
////        JSONObject hdpConfig = createHadoopConfigObject(dtuicTenantId);
////        if (!hdpConfig.isEmpty()) {
////            dataJson.put("hadoopConfig", hdpConfig.toJSONString());
////        }
////
////        String dataSourceName = projectName + "_" + MultiEngineType.HADOOP.name();
////        dataJson.put("hasHdfsConfig", true);
////
////        JdbcInfo hive = Engine2DTOService.getJdbcInfo(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType));
////        JSONObject kerberosConfig = hive.getKerberosConfig();
////        if (Objects.nonNull(kerberosConfig)) {
////            Map<String, String> sftpMap = batchDataSourceService.getSftpMap(dtuicTenantId);
////            String remotePath = kerberosConfig.getString("remotePath");
////            kerberosConfig.put("remotePath", remotePath.replaceAll(sftpMap.get("path"), ""));
////            kerberosConfig.put("hive.server2.authentication", "KERBEROS");
////            dataJson.put("kerberosConfig", hive.getKerberosConfig());
////        }
////        batchDataSourceService.createMateDataSource(dtuicTenantId, tenantId, projectId, userId, dataJson.toJSONString(), dataSourceName, dataSourceType, projectDesc, dbName);
////    }
//
//    //TODO 需要抽取出来一个公用的方法
//    @Forbidden
//    public JSONObject createHadoopConfigObject(Long dtuicTenantId) {
//        JSONObject hadoop = new JSONObject();
//        Map<String, Object> config = HadoopConf.getConfiguration(dtuicTenantId);
//        String nameServices = config.getOrDefault("dfs.nameservices","").toString();
//        if (StringUtils.isNotBlank(nameServices)) {
//            hadoop.put("dfs.nameservices", nameServices);
//            String nameNodes = config.getOrDefault(String.format("dfs.ha.namenodes.%s", nameServices),"").toString();
//            if (StringUtils.isNotBlank(nameNodes)) {
//                hadoop.put(String.format("dfs.ha.namenodes.%s", nameServices), nameNodes);
//                for (String nameNode : nameNodes.split(",")) {
//                    String key = String.format("dfs.namenode.rpc-address.%s.%s", nameServices, nameNode);
//                    hadoop.put(key, config.get(key));
//                }
//            }
//            String failoverKey = String.format("dfs.client.failover.proxy.provider.%s", nameServices);
//            hadoop.put(failoverKey, config.get(failoverKey));
//        }
//
//        return hadoop;
//    }
//
//}
