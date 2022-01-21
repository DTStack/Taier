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

package com.dtstack.batch.engine.rdbms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.dtcenter.loader.cache.pool.config.PoolConfig;
import com.dtstack.dtcenter.loader.dto.source.Hive1SourceDTO;
import com.dtstack.dtcenter.loader.dto.source.Hive3SourceDTO;
import com.dtstack.dtcenter.loader.dto.source.HiveSourceDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.SparkSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.common.engine.JdbcInfo;
import com.dtstack.engine.common.engine.JdbcUrlPropertiesValue;
import com.dtstack.engine.common.engine.KerberosConfig;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.ETableType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.kerberos.KerberosConfigVerify;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.master.service.ClusterService;
import com.dtstack.engine.master.service.ComponentService;
import com.dtstack.engine.master.vo.components.ComponentsConfigOfComponentsVO;
import com.google.common.base.Preconditions;
import com.jcraft.jsch.SftpException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 提供根据dtUicTenantId、dtUicUserId、engine类型获取对应的SourceDTO
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
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.Spark.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    SPARK_THRIFT2_1(DataSourceType.SparkThrift2_1.getVal() ){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.SparkThrift2_1.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    HIVE(DataSourceType.HIVE.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            ISourceDTO sourceDTO = HiveSourceDTO.builder()
                        .sourceType(DataSourceType.HIVE.getVal())
                        .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                        .username(jdbcInfo.getUsername())
                        .password(jdbcInfo.getPassword())
                        .kerberosConfig(jdbcInfo.getKerberosConfig())
                        .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                        .config(config)
                        .poolConfig(buildPoolConfig())
                        .build();
            return sourceDTO;
        }
    },

    HIVE3(DataSourceType.HIVE3X.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            ISourceDTO sourceDTO = Hive3SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE3X.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
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
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            ISourceDTO sourceDTO = Hive1SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE1X.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
    };

    /**
     * 处理 JDBC 参数
     * @param jdbcUrl
     * @param params
     * @return
     */
    public static String buildJdbcURLWithParam(String jdbcUrl, Map<String, String> params) {
        if (MapUtils.isEmpty(params)) {
            return jdbcUrl;
        }

        String splicingSymbol = needSplicingSymbol(jdbcUrl) ? "?" : "";
        jdbcUrl = jdbcUrl + splicingSymbol;
        StringBuilder paramBuilder = new StringBuilder();

        params.forEach((key, value) -> {
            paramBuilder.append("&")
                    .append(key)
                    .append("=")
                    .append(value);
        });

        String paramStr = paramBuilder.toString();
        paramStr = paramStr.replaceFirst("&", "");
        jdbcUrl += paramStr;

        return jdbcUrl;
    }

    /**
     * 是否需要jdbc url 的参数拼接符 "?"
     *
     * @param jdbcURL
     * @return
     */
    private static boolean needSplicingSymbol(String jdbcURL) {
        return !jdbcURL.contains("?");
    }


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

    protected abstract ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName);

    /**
     * 根据dtUicTenantId、dtUicUserId、tableType、dbName获取对应的sourceDTO，供外部调用
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param tableType 表类型 {@link DataSourceType}
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long dtUicTenantId, Long dtUicUserId, ETableType tableType, String dbName) {
        JdbcInfo jdbcInfo = getJdbcInfo(dtUicTenantId, dtUicUserId, tableType);
        DataSourceType dataSourceType = tableTypeTransitionDataSourceType(tableType, jdbcInfo.getVersion(), dtUicTenantId);
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType.getVal());
        return engine2DTOEnum.getSourceDTO(jdbcInfo, dtUicTenantId, dtUicUserId, dbName);
    }

    /**
     * 根据dtUicTenantId、dtUicUserId、tableType、dbName获取对应的sourceDTO，供外部调用
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param eScheduleJobType  任务类型
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long dtUicTenantId, Long dtUicUserId, EScheduleJobType eScheduleJobType, String dbName) {
        JdbcInfo jdbcInfo = getJdbcInfo(dtUicTenantId, dtUicUserId, eScheduleJobType);
        DataSourceType dataSourceType = jobTypeTransitionDataSourceType(eScheduleJobType, jdbcInfo.getVersion());
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType.getVal());
        return engine2DTOEnum.getSourceDTO(jdbcInfo, dtUicTenantId, dtUicUserId, dbName);
    }


    /**
     * 根据dtUicTenantId、dtUicUserId、engineType、dbName、jdbcinfo获取对应的sourceDTO，供外部调用
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param dataSourceType 引擎类型 {@link DataSourceType}
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long dtUicTenantId, Long dtUicUserId, Integer dataSourceType, String dbName, JdbcInfo jdbcInfo) {
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType);
        return engine2DTOEnum.getSourceDTO(jdbcInfo, dtUicTenantId, dtUicUserId, dbName);
    }

    /**
     * 获取引擎对应的jdbcInfo
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param eTableType 表类型
     * @return 数据源连接信息
     */
    public static JdbcInfo getJdbcInfo (Long dtUicTenantId, Long dtUicUserId, ETableType eTableType) {
        JdbcInfo jdbcInfo = null;
        if (dtUicTenantId != null) {
            if (ETableType.HIVE.equals(eTableType)) {
                EScheduleJobType eScheduleJobType = getJobTypeByHadoopMetaType(dtUicTenantId);
                jdbcInfo = getJdbcInfo(dtUicTenantId, dtUicUserId, eScheduleJobType);
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
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param eScheduleJobType 任务类型
     * @return 数据源连接信息
     */
    public static JdbcInfo getJdbcInfo (Long dtUicTenantId, Long dtUicUserId, EScheduleJobType eScheduleJobType) {
        JdbcInfo jdbcInfo = null;
        if (clusterService != null && dtUicTenantId != null) {
            if (EScheduleJobType.SPARK_SQL.equals(eScheduleJobType)) {
                jdbcInfo = getSparkThrift(dtUicTenantId);
            }
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
     * @param dtUicUserId
     * @return
     */
    protected String buildHadoopConfig(Long dtUicUserId){
        String config;
        try {
            config = PublicUtil.objectToStr(HadoopConf.getConfiguration(dtUicUserId));
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
     * @param dtUicTenantId
     * @return
     */
    public static DataSourceType tableTypeTransitionDataSourceType(ETableType eTableType, String version, Long dtUicTenantId){
        if (ETableType.HIVE.equals(eTableType)) {
            EScheduleJobType eScheduleJobType = getJobTypeByHadoopMetaType(dtUicTenantId);
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
        } else {
            throw new RdosDefineException("jobType not transition dataSourceType");
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
     * @param uicTenantId
     * @return
     */
    public static Map<String, String> getSftp(Long uicTenantId) {
        JSONObject data = clusterService.getConfigByKey(uicTenantId, EComponentType.SFTP.getConfName(), null);
        if (data == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, uicTenantId, EComponentType.SFTP.name()));
        }
        Map<String, Object> conf = JSONObject.parseObject(data.toJSONString(), Map.class);
        return conf.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (entry.getValue() == null ? null : entry.getValue().toString())));
    }


    /**
     * 获取 Hadoop 集群信息，并填充 Kerberos 文件之类的操作
     *
     * @param uicTenantId
     * @return
     */
    public static Map<String, Object> getHdfs(Long uicTenantId) {
        Map<String, Object> data = getHdfsInfo(uicTenantId);
        return checkKerberosWithPeriod(uicTenantId, data);
    }


    /**
     * 获取 HiveServer 信息，并填充 Kerberos 文件之类的操作
     *
     * @param uicTenantId
     * @return
     */
    public static JdbcInfo getHiveServer(Long uicTenantId) {
        JdbcInfo data = getPluginInfo(uicTenantId, EComponentType.HIVE_SERVER);
        return checkKerberosWithPeriod(uicTenantId, data);
    }


    /**
     * 获取 SparkThrift 信息，并填充 Kerberos 文件之类的操作
     *
     * @param uicTenantId
     * @return
     */
    public static JdbcInfo getSparkThrift(Long uicTenantId) {
        JdbcInfo data = getPluginInfo(uicTenantId, EComponentType.SPARK_THRIFT);
        return checkKerberosWithPeriod(uicTenantId, data);
    }




    /**
     * 获取 Hadoop 集群信息
     *
     * @param uicTenantId
     * @return
     */
    private static Map<String, Object> getHdfsInfo(Long uicTenantId) {
        JSONObject data = clusterService.getConfigByKey(uicTenantId, EComponentType.HDFS.getConfName(), null);
        if (data == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, uicTenantId, EComponentType.HDFS.name()));
        }
        return JSONObject.parseObject(data.toJSONString(), Map.class);
    }

    /**
     * 获取集群组件 JDBC 信息
     *
     * @param uicTenantId
     * @param componentType
     * @return
     */
    private static JdbcInfo getPluginInfo(Long uicTenantId, EComponentType componentType) {
        JSONObject configByKey = clusterService.getConfigByKey(uicTenantId, componentType.getConfName(), null);
        if (configByKey == null) {
            throw new DtCenterDefException(String.format(ERROR_MSG_CLUSTER_INFO, uicTenantId, componentType.getName()));
        }
        return JSONObject.parseObject(configByKey.toString(), JdbcInfo.class);
    }



    /**
     * 获取集群的信息
     *
     * @param tenantId
     * @return
     */
    public static JSONObject getYarnConf(Long tenantId) {
        return clusterService.getConfigByKey(tenantId,EComponentType.YARN.getConfName(),null);
    }

    /**
     * 获取指定租户指定引擎的插件信息
     *
     * @param uicTenantId
     * @param engineType
     * @return
     */
    public static String getEnginePluginInfo(Long uicTenantId, Integer engineType) {
        List<ComponentsConfigOfComponentsVO> data = componentService.listConfigOfComponents(uicTenantId, engineType,null);
        if (CollectionUtils.isEmpty(data)) {
            throw new RdosDefineException("Component does not exist");
        }
        JSONObject enginePluginInfo = new JSONObject();
        for (ComponentsConfigOfComponentsVO vo : data) {
            enginePluginInfo.put(String.valueOf(vo.getComponentTypeCode()), vo.getComponentConfig());
        }
        return enginePluginInfo.toJSONString();
    }


    public static Integer getMetaComponent(Long tenantId){
       return clusterService.getMetaComponent(tenantId);
    }



    /**
     * 校验 Kerberos 参数及文件
     *
     * @param uicTenantId
     * @param data
     */
    private static <T> T checkKerberosWithPeriod(Long uicTenantId, T data) {
        if(data instanceof JdbcInfo){
            JSONObject kerberosConfig = ((JdbcInfo) data).getKerberosConfig();
            if(kerberosConfig != null){
                data = fullKerberosFilePath(uicTenantId, data);
            }
        }else if(data instanceof Map){
            Set keySet = ((Map) data).keySet();
            if(keySet.contains("kerberosConfig")){
                data = fullKerberosFilePath(uicTenantId, data);
            }
        }

        return data;
    }

    /**
     * 填充 Kerberos 参数
     *
     * @param uicTenantId
     * @param data
     * @param <T>
     * @return
     */
    public static <T> T fullKerberosFilePath(Long uicTenantId, T data) {
        Map<String, String> sftp = getSftp(uicTenantId);
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
