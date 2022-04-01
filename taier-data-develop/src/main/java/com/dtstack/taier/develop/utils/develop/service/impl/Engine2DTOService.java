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

package com.dtstack.taier.develop.utils.develop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.cache.pool.config.PoolConfig;
import com.dtstack.dtcenter.loader.dto.source.*;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.engine.JdbcInfo;
import com.dtstack.taier.common.engine.JdbcUrlPropertiesValue;
import com.dtstack.taier.common.engine.KerberosConfig;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.ETableType;
import com.dtstack.taier.common.enums.HiveVersion;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.kerberos.KerberosConfigVerify;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.develop.utils.develop.common.HadoopConf;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.google.common.base.Preconditions;
import com.jcraft.jsch.SftpException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 提供根据tenantId、tenantId、engine类型获取对应的SourceDTO
 *
 * @author ：wangchuan
 * date：Created in 下午9:12 2020/11/4
 * company: www.dtstack.com
 */
public enum Engine2DTOService {

    /**
     * spark 不支持hive1.x
     */
    SPARK(DataSourceType.Spark.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long tenantId, Long userId, String dbName) {
            String config = buildHadoopConfig(tenantId);
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.Spark.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(tenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
        }

        @Override
        public ISourceDTO getSourceDTOByClusterId(JdbcInfo jdbcInfo, Long clusterId, String dbName) {
            String config = buildHadoopConfigByClusterId(clusterId);
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.Spark.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(clusterId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    SPARK_THRIFT2_1(DataSourceType.SparkThrift2_1.getVal() ){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long tenantId, Long userId, String dbName) {
            String config = buildHadoopConfig(tenantId);
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.SparkThrift2_1.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(tenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
        }
        @Override
        public ISourceDTO getSourceDTOByClusterId(JdbcInfo jdbcInfo, Long clusterId, String dbName) {
            String config = buildHadoopConfigByClusterId(clusterId);
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.SparkThrift2_1.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(clusterId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    HIVE(DataSourceType.HIVE.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long tenantId, Long userId, String dbName) {
            String config = buildHadoopConfig(tenantId);
            ISourceDTO sourceDTO = HiveSourceDTO.builder()
                        .sourceType(DataSourceType.HIVE.getVal())
                        .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                        .username(jdbcInfo.getUsername())
                        .password(jdbcInfo.getPassword())
                        .kerberosConfig(jdbcInfo.getKerberosConfig())
                        .defaultFS(HadoopConf.getDefaultFs(tenantId))
                        .config(config)
                        .poolConfig(buildPoolConfig())
                        .build();
            return sourceDTO;
        }
        @Override
        public ISourceDTO getSourceDTOByClusterId(JdbcInfo jdbcInfo, Long clusterId, String dbName) {
            String config = buildHadoopConfigByClusterId(clusterId);
            ISourceDTO sourceDTO = HiveSourceDTO.builder()
                    .sourceType(DataSourceType.HIVE.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFsByClusterId(clusterId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
    },

    HIVE3(DataSourceType.HIVE3X.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long tenantId, Long userId, String dbName) {
            String config = buildHadoopConfig(tenantId);
            ISourceDTO sourceDTO = Hive3SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE3X.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(tenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
        @Override
        public ISourceDTO getSourceDTOByClusterId(JdbcInfo jdbcInfo, Long clusterId, String dbName) {
            String config = buildHadoopConfigByClusterId(clusterId);
            ISourceDTO sourceDTO = Hive3SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE3X.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFsByClusterId(clusterId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
    },

    /**
     * hive 1.x版本
     */
    HIVE1(DataSourceType.HIVE1X.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long tenantId, Long userId, String dbName) {
            String config = buildHadoopConfig(tenantId);
            ISourceDTO sourceDTO = Hive1SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE1X.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(tenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
        @Override
        public ISourceDTO getSourceDTOByClusterId(JdbcInfo jdbcInfo, Long clusterId, String dbName) {
            String config = buildHadoopConfigByClusterId(clusterId);
            ISourceDTO sourceDTO = Hive1SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE1X.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFsByClusterId(clusterId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
    };
    

   

    /**
     * 获取集群信息
     */
    private static final String ERROR_MSG_CLUSTER_INFO = "集群ID:%s，获取组件标识:%s，信息为空";

    public static ClusterService clusterService;
    public static ComponentService componentService;
    public static EnvironmentContext environmentContext;


    public static void init(ComponentService componentService, ClusterService clusterService, EnvironmentContext environmentContext) {
        Engine2DTOService.clusterService = clusterService;
        Engine2DTOService.componentService = componentService;
        Engine2DTOService.environmentContext = environmentContext;

    }

    private Integer val;

    Engine2DTOService(Integer val) {
        this.val = val;
    }

    public Integer getVal() {
        return val;
    }

    protected abstract ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long tenantId, Long userId, String dbName);

    protected abstract ISourceDTO getSourceDTOByClusterId(JdbcInfo jdbcInfo, Long clusterId, String dbName);

    /**
     * 根据tenantId、userId、tableType、dbName获取对应的sourceDTO，供外部调用
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param tableType 表类型 {@link DataSourceType}
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long tenantId, Long userId, ETableType tableType, String dbName) {
        JdbcInfo jdbcInfo = getJdbcInfo(tenantId, userId, tableType);
        DataSourceType dataSourceType = tableTypeTransitionDataSourceType(tableType, jdbcInfo.getVersion(), tenantId);
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType.getVal());
        return engine2DTOEnum.getSourceDTO(jdbcInfo, tenantId, userId, dbName);
    }

    /**
     * 根据tenantId、userId、tableType、dbName获取对应的sourceDTO，供外部调用
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param eScheduleJobType  任务类型
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String dbName) {
        JdbcInfo jdbcInfo = getJdbcInfo(tenantId, userId, eScheduleJobType);
        DataSourceType dataSourceType = jobTypeTransitionDataSourceType(eScheduleJobType, jdbcInfo.getVersion());
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType.getVal());
        return engine2DTOEnum.getSourceDTO(jdbcInfo, tenantId, userId, dbName);
    }

    /**
     * 根据clusterId、eComponentType、dbName获取对应的sourceDTO，供外部调用
     *
     * @param clusterId      集群ID
     * @param eComponentType 组件类型
     * @param dbName         db名称
     * @return 对应的sourceDTO
     */
    public static ISourceDTO getByClusterId(Long clusterId, EComponentType eComponentType, String dbName) {
        JdbcInfo jdbcInfo = getJdbcInfoByClusterId(clusterId, eComponentType);
        jdbcInfo.setUsername("admin");
        DataSourceType dataSourceType = componentTypeToDataSourceType(eComponentType, jdbcInfo.getVersion());
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType.getVal());
        return engine2DTOEnum.getSourceDTOByClusterId(jdbcInfo, clusterId, dbName);
    }

    /**
     * 根据tenantId、userId、engineType、dbName、jdbcinfo获取对应的sourceDTO，供外部调用
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param dataSourceType 引擎类型 {@link DataSourceType}
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long tenantId, Long userId, Integer dataSourceType, String dbName, JdbcInfo jdbcInfo) {
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType);
        return engine2DTOEnum.getSourceDTO(jdbcInfo, tenantId, userId, dbName);
    }

    /**
     * 获取引擎对应的jdbcInfo
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param eTableType 表类型
     * @return 数据源连接信息
     */
    public static JdbcInfo getJdbcInfo (Long tenantId, Long userId, ETableType eTableType) {
        JdbcInfo jdbcInfo = null;
        if (tenantId != null) {
            if (ETableType.HIVE.equals(eTableType)) {
                EScheduleJobType eScheduleJobType = getJobTypeByHadoopMetaType(tenantId);
                jdbcInfo = getJdbcInfo(tenantId, userId, eScheduleJobType);
            }
        }
        if (jdbcInfo == null) {
            throw new DtCenterDefException("can't get jdbc conf from console");
        }
        JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
        return jdbcInfo;
    }


    /**
     * 获取引擎对应的jdbcInfo
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param eScheduleJobType 任务类型
     * @return 数据源连接信息
     */
    public static JdbcInfo getJdbcInfo (Long tenantId, Long userId, EScheduleJobType eScheduleJobType) {
        JdbcInfo jdbcInfo = null;
        if (EScheduleJobType.SPARK_SQL.equals(eScheduleJobType)) {
            jdbcInfo = getSparkThrift(tenantId);
        } else if(EScheduleJobType.HIVE_SQL.equals(eScheduleJobType)){
            jdbcInfo = getHiveServer(tenantId);
        }
        if (jdbcInfo == null) {
            throw new DtCenterDefException("can't get jdbc conf from console");
        }
        JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
        return jdbcInfo;
    }

    /**
     * 获取引擎对应的jdbcInfo
     *
     * @param clusterId 集群ID
     * @param eComponentType 组件信息
     * @return 数据源连接信息
     */
    public static JdbcInfo getJdbcInfoByClusterId (Long clusterId, EComponentType eComponentType) {
        JdbcInfo jdbcInfo = null;
        if (EComponentType.SPARK_THRIFT.equals(eComponentType)) {
            jdbcInfo = getSparkThriftByClusterId(clusterId);
        }else if ((EComponentType.HIVE_SERVER.equals(eComponentType))){
            jdbcInfo = getHiveServerByClusterId(clusterId);
        }
        if (jdbcInfo == null) {
            throw new DtCenterDefException("can't get jdbc conf from console");
        }
        JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
        return jdbcInfo;
    }

    /**
     * 根据枚举值获取对应的枚举类
     * @param val 引擎类型
     * @return {@link Engine2DTOService}
     */
    private static Engine2DTOService getSourceDTOType(Integer val) {
        for (Engine2DTOService engine2DTOEnum : values()) {
            if (engine2DTOEnum.val.equals(val)) {
                return engine2DTOEnum;
            }
        }
        throw new DtCenterDefException("暂时不支持该引擎类型");
    }

    /**
     * 构建连接池 TODO 后续使用common-loader中的连接池、ide中不再维护连接池
     *
     * @return
     */
    public static PoolConfig buildPoolConfig () {

        // 构建连接池
        return PoolConfig.builder()
                .build();
    }

    /**
     * 构建hadoop配置参数
     *
     * @param tenantId
     * @return
     */
    protected String buildHadoopConfig(Long tenantId){
        String config;
        try {
            config = PublicUtil.objectToStr(HadoopConf.getConfiguration(tenantId));
        } catch (IOException e) {
            throw new DtCenterDefException(String.format("hadoop配置转换异常，原因是：%s", e.getMessage()));
        }
        return config;
    }

    /**
     * 构建hadoop配置参数
     *
     * @param clusterId 集群ID
     * @return
     */
    protected String buildHadoopConfigByClusterId(Long clusterId) {
        String config;
        try {
            config = PublicUtil.objectToStr(HadoopConf.getConfigurationByClusterId(clusterId));
        } catch (IOException e) {
            throw new DtCenterDefException(String.format("hadoop配置转换异常，原因是：%s", e.getMessage()));
        }
        return config;
    }

    /**
     * 根据db构建url
     * @param jdbcUrl url
     * @param dbName 数据库
     * @return 构建后的url
     */
    public static String buildUrlWithDb(String jdbcUrl, String dbName) {

        dbName = StringUtils.isNotBlank(dbName) ? dbName.trim() : "";

        if (StringUtils.isNotBlank(jdbcUrl) && jdbcUrl.trim().contains("%s")) {
            return String.format(jdbcUrl, dbName);
        }
        return jdbcUrl;
    }

    /**
     * tableType 转化为datasourceType
     *
     * @param eTableType
     * @param version 小版本信息
     * @param tenantId
     * @return
     */
    public static DataSourceType tableTypeTransitionDataSourceType(ETableType eTableType, String version, Long tenantId){
        if (ETableType.HIVE.equals(eTableType)) {
            EScheduleJobType eScheduleJobType = getJobTypeByHadoopMetaType(tenantId);
            return jobTypeTransitionDataSourceType(eScheduleJobType, version);
        } else {
            throw new RdosDefineException("tableType not transition dataSourceType");
        }
    }

    /**
     * jobType 转化为 对应的dataSourceType
     * 如果没有对应的数据源类型 抛出异常
     *
     * @return
     */
    public static DataSourceType jobTypeTransitionDataSourceType(EScheduleJobType eScheduleJobType, String version) {
        if (EScheduleJobType.SPARK_SQL.equals(eScheduleJobType)) {
            return DataSourceType.SparkThrift2_1;
        } else if(EScheduleJobType.HIVE_SQL.equals(eScheduleJobType)){
                if (HiveVersion.HIVE_1x.getVersion().equals(version)) {
                    return DataSourceType.HIVE1X;
                } else if (HiveVersion.HIVE_3x.getVersion().equals(version)) {
                    return DataSourceType.HIVE3X;
                } else {
                    return DataSourceType.HIVE;
                }
        }else {
            throw new RdosDefineException("jobType not transition dataSourceType");
        }
    }

    /**
     * eComponentType 转化为 对应的dataSourceType
     * 如果没有对应的数据源类型 抛出异常
     *
     * @return
     */
    public static DataSourceType componentTypeToDataSourceType(EComponentType eComponentType, String version) {
        if (EComponentType.SPARK_THRIFT.equals(eComponentType)) {
            return DataSourceType.SparkThrift2_1;
        } else if(EComponentType.HIVE_SERVER.equals(eComponentType)){
            return DataSourceType.HIVE;
        }else {
            throw new RdosDefineException("eComponentType not transition dataSourceType");
        }
    }

    /**
     * 根据当前租户绑定集群的元数据方式 获取 对应的 JobType
     *
     * @param tenantId
     * @return
     */
    public static EScheduleJobType getJobTypeByHadoopMetaType(Long tenantId) {
        Integer metaComponent = getMetaComponent(tenantId);
        if(null == metaComponent){
            throw new RdosDefineException("not find 'Hadoop' meta DataSource!");
        }
        EComponentType componentType = EComponentType.getByCode(metaComponent);
        switch (componentType){
            case SPARK_THRIFT:
                return EScheduleJobType.SPARK_SQL;
            default:
                throw new RdosDefineException("not support meta DataSource!");
        }

    }

    /**
     * 获取 SFTP 信息
     *
     * @param tenantId
     * @return
     */
    public static Map<String, String> getSftp(Long tenantId) {
        JSONObject data = getComponentConfig(tenantId, EComponentType.SFTP);
        if (data == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, tenantId, EComponentType.SFTP.name()));
        }
        Map<String, Object> conf = JSONObject.parseObject(data.toJSONString(), Map.class);
        return conf.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (entry.getValue() == null ? null : entry.getValue().toString())));
    }

    /**
     * 获取 SFTP 信息
     *
     * @param clusterId 集群ID
     * @return
     */
    public static Map<String, String> getSftpByClusterId(Long clusterId) {
        JSONObject data = getComponentConfigByClusterId(clusterId, EComponentType.SFTP);
        if (data == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, clusterId, EComponentType.SFTP.name()));
        }
        Map<String, Object> conf = JSONObject.parseObject(data.toJSONString(), Map.class);
        return conf.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (entry.getValue() == null ? null : entry.getValue().toString())));
    }


    /**
     * 获取 Hadoop 集群信息，并填充 Kerberos 文件之类的操作
     *
     * @param tenantId
     * @return
     */
    public static Map<String, Object> getHdfs(Long tenantId) {
        Map<String, Object> data = getHdfsInfo(tenantId);
        return checkKerberosWithPeriod(tenantId, data);
    }

    /**
     * 获取 Hadoop 集群信息，并填充 Kerberos 文件之类的操作
     *
     * @param clusterId 集群ID
     * @return
     */
    public static Map<String, Object> getHdfsByClusterId(Long clusterId) {
        Map<String, Object> data = getHdfsInfoByClusterId(clusterId);
        return checkKerberosWithPeriodByClusterId(clusterId, data);
    }

    /**
     * 获取 SparkThrift 信息，并填充 Kerberos 文件之类的操作
     *
     * @param tenantId
     * @return
     */
    public static JdbcInfo getSparkThrift(Long tenantId) {
        JdbcInfo data = getPluginInfo(tenantId, EComponentType.SPARK_THRIFT);
        return checkKerberosWithPeriod(tenantId, data);
    }

    /**
     * 获取 HiveServer 信息，并填充 Kerberos 文件之类的操作
     *
     * @param tenantId
     * @return
     */
    public static JdbcInfo getHiveServer(Long tenantId) {
        JdbcInfo data = getPluginInfo(tenantId, EComponentType.HIVE_SERVER);
        return checkKerberosWithPeriod(tenantId, data);
    }

    /**
     * 获取 SparkThrift 信息，并填充 Kerberos 文件之类的操作
     *
     * @param clusterId 集群ID
     * @return
     */
    public static JdbcInfo getSparkThriftByClusterId(Long clusterId) {
        JdbcInfo data = getPluginInfoByClusterId(clusterId, EComponentType.SPARK_THRIFT);
        return checkKerberosWithPeriodByClusterId(clusterId, data);
    }

    /**
     * 获取 hiveServer 信息，并填充 Kerberos 文件之类的操作
     *
     * @param clusterId 集群ID
     * @return
     */
    public static JdbcInfo getHiveServerByClusterId(Long clusterId) {
        JdbcInfo data = getPluginInfoByClusterId(clusterId, EComponentType.HIVE_SERVER);
        return checkKerberosWithPeriodByClusterId(clusterId, data);
    }

    /**
     * 获取 Hadoop 集群信息
     *
     * @param tenantId
     * @return
     */
    private static Map<String, Object> getHdfsInfo(Long tenantId) {
        JSONObject data = getComponentConfig(tenantId, EComponentType.HDFS);
        if (data == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, tenantId, EComponentType.HDFS.name()));
        }
        return JSONObject.parseObject(data.toJSONString(), Map.class);
    }

    /**
     * 获取 Hadoop 集群信息
     *
     * @param clusterId 集群信息
     * @return
     */
    private static Map<String, Object> getHdfsInfoByClusterId(Long clusterId) {
        JSONObject data = getComponentConfigByClusterId(clusterId, EComponentType.HDFS);
        if (data == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, clusterId, EComponentType.HDFS.name()));
        }
        return JSONObject.parseObject(data.toJSONString(), Map.class);
    }

    /**
     * 获取集群组件 JDBC 信息
     *
     * @param tenantId
     * @param componentType
     * @return
     */
    private static JdbcInfo getPluginInfo(Long tenantId, EComponentType componentType) {
        JSONObject configByKey = getComponentConfig(tenantId, componentType);
        if (configByKey == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, tenantId, componentType.getName()));
        }
        return JSONObject.parseObject(configByKey.toString(), JdbcInfo.class);
    }

    /**
     * 获取集群组件 JDBC 信息
     *
     * @param cluster       集群ID
     * @param componentType 组件类型
     * @return
     */
    private static JdbcInfo getPluginInfoByClusterId(Long cluster, EComponentType componentType) {
        JSONObject configByKey = getComponentConfigByClusterId(cluster, componentType);
        if (configByKey == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, cluster, componentType.getName()));
        }
        return JSONObject.parseObject(configByKey.toString(), JdbcInfo.class);
    }

    public static JSONObject getComponentConfig(Long tenantId, EComponentType componentType) {
        return clusterService.getConfigByKey(tenantId, componentType.getConfName(), null);
    }

    /**
     * 根据集群ID获取对应的组件信息
     *
     * @param clusterId     集群ID
     * @param componentType 组件类型
     * @return
     */
    public static JSONObject getComponentConfigByClusterId(Long clusterId, EComponentType componentType) {
        return clusterService.getConfigByKeyByClusterId(clusterId, componentType.getConfName(), null);
    }
    
    public static Integer getMetaComponent(Long tenantId){
       return clusterService.getMetaComponent(tenantId);
    }



    /**
     * 校验 Kerberos 参数及文件
     *
     * @param tenantId
     * @param data
     */
    private static <T> T checkKerberosWithPeriod(Long tenantId, T data) {
        if(data instanceof JdbcInfo){
            JSONObject kerberosConfig = ((JdbcInfo) data).getKerberosConfig();
            if(kerberosConfig != null){
                data = fullKerberosFilePath(tenantId, data);
            }
        }else if(data instanceof Map){
            Set keySet = ((Map) data).keySet();
            if(keySet.contains("kerberosConfig")){
                data = fullKerberosFilePath(tenantId, data);
            }
        }

        return data;
    }

    /**
     * 校验 Kerberos 参数及文件
     *
     * @param clusterId 集群ID
     * @param data
     */
    private static <T> T checkKerberosWithPeriodByClusterId(Long clusterId, T data) {
        if (data instanceof JdbcInfo) {
            JSONObject kerberosConfig = ((JdbcInfo) data).getKerberosConfig();
            if (kerberosConfig != null) {
                data = fullKerberosFilePathByClusterId(clusterId, data);
            }
        } else if (data instanceof Map) {
            Set keySet = ((Map) data).keySet();
            if (keySet.contains("kerberosConfig")) {
                data = fullKerberosFilePathByClusterId(clusterId, data);
            }
        }
        return data;
    }

    /**
     * 填充 Kerberos 参数
     *
     * @param tenantId
     * @param data
     * @param <T>
     * @return
     */
    public static <T> T fullKerberosFilePath(Long tenantId, T data) {
        Map<String, String> sftp = getSftp(tenantId);
        return dealKerberos(data, sftp);
    }

    /**
     * 填充 Kerberos 参数
     *
     * @param clusterId
     * @param data
     * @param <T>
     * @return
     */
    public static <T> T fullKerberosFilePathByClusterId(Long clusterId, T data) {
        Map<String, String> sftp = getSftpByClusterId(clusterId);
        return dealKerberos(data, sftp);
    }

    /**
     * 处理kerberos 信息
     *
     * @param data
     * @param sftp
     * @param <T>
     * @return
     */
    private static <T> T dealKerberos( T data, Map<String, String> sftp){
        if (MapUtils.isEmpty(sftp)) {
            return data;
        }
        //1.解析为可用类型
        JSONObject dataMap = getJsonObject(data);
        //2.主要逻辑：下载文件，替换路径
        accordToKerberosFile(sftp, dataMap);
        //3.转换回原类型
        data = convertJsonOverBack(data, dataMap);
        return data;
    }

    private static <T> T convertJsonOverBack(T data, JSONObject dataMap) {
        if (data instanceof String) {
            data = (T) dataMap.toString();
        } else {
            data = JSONObject.parseObject(dataMap.toString(), (Class<T>) data.getClass());
        }
        return data;
    }

    private static void accordToKerberosFile(Map<String, String> sftp, JSONObject dataMap) {
        try {
            // 判断空指针处理
            if (MapUtils.isEmpty(dataMap)) {
                return;
            }

            JSONObject configJsonObject = dataMap.getJSONObject("kerberosConfig");
            if (Objects.isNull(configJsonObject)) {
                return;
            }
            KerberosConfig kerberosConfig = PublicUtil.strToObject(configJsonObject.toString(), KerberosConfig.class);
            if (Objects.nonNull(kerberosConfig)) {
                Preconditions.checkState(Objects.nonNull(kerberosConfig.getClusterId()));
                Preconditions.checkState(Objects.nonNull(kerberosConfig.getOpenKerberos()));
                Preconditions.checkState(StringUtils.isNotEmpty(kerberosConfig.getPrincipal()));
                Preconditions.checkState(StringUtils.isNotEmpty(kerberosConfig.getRemotePath()));
                if (kerberosConfig.getOpenKerberos() > 0) {
                    String clusterKey = kerberosConfig.getRemotePath().replaceAll(sftp.get("path"), "");
                    String localPath = System.getProperty("user.dir") + "/kerberosConf/" + clusterKey;
                    KerberosConfigVerify.downloadKerberosFromSftp(clusterKey, localPath, sftp, kerberosConfig.getKerberosFileTimestamp());
                    File file = new File(localPath);
                    Preconditions.checkState(file.exists() && file.isDirectory(), "console kerberos local path not exist");

                    String principalFile = kerberosConfig.getPrincipalFile();
                    // 判断控制台是否有返回 principalFile，规则是顾虑隐藏文件同时获取第一个以 keytab 为后缀的文件
                    if (StringUtils.isBlank(kerberosConfig.getPrincipalFile())) {
                        File keytabFile = Arrays.stream(file.listFiles()).filter(obj -> !obj.getName().startsWith(".") && obj.getName().endsWith("keytab")).findFirst().orElseThrow(() -> new DtCenterDefException("keytab文件不存在"));
                        principalFile = keytabFile.getName();
                    }

                    List<File> krb5Conf = Arrays.stream(file.listFiles()).filter(obj -> !obj.getName().startsWith(".") && obj.getName().equalsIgnoreCase("krb5.conf")).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(krb5Conf)) {
                        configJsonObject.put("java.security.krb5.conf", krb5Conf.get(0).getPath());
                    }
                    configJsonObject.put("keytabPath", (localPath + "/" + principalFile).replaceAll("/+",  "/"));
                    configJsonObject.put("principalFile", principalFile);
                    configJsonObject.putAll(Optional.ofNullable(configJsonObject.getJSONObject("hdfsConfig")).orElse(new JSONObject()));
                    configJsonObject.remove("hdfsConfig");
                }
            }
        } catch (SftpException | IOException e) {
            throw new DtCenterDefException("下载kerberos文件失败");
        }
    }

    private static <T> JSONObject getJsonObject(T data) {
        JSONObject dataMap = new JSONObject();
        if (data == null) {
            return dataMap;
        }
        if (data instanceof String) {
            dataMap = JSONObject.parseObject((String) data);
        } else {
            dataMap = (JSONObject) JSONObject.toJSON(data);
        }
        return dataMap;
    }
}
