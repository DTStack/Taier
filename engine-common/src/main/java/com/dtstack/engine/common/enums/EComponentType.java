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

package com.dtstack.engine.common.enums;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.constrant.ComponentConstant;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jiangbo
 * @date 2019/5/30
 */
public enum EComponentType {

    FLINK(0, "Flink", "flinkConf"),
    SPARK(1, "Spark", "sparkConf"),
    LEARNING(2, "Learning", "learningConf"),
    DT_SCRIPT(3, "DtScript", "dtscriptConf"),
    HDFS(4, "HDFS", "hadoopConf"),
    YARN(5, "YARN", "yarnConf"),
    SPARK_THRIFT(6, "SparkThrift", "hiveConf"),
    CARBON_DATA(7, "CarbonData ThriftServer", "carbonConf"),
    LIBRA_SQL(8, "LibrA SQL", "libraConf"),
    HIVE_SERVER(9, "HiveServer", "hiveServerConf"),
    SFTP(10, "SFTP", "sftpConf"),
    IMPALA_SQL(11, "Impala SQL", "impalaSqlConf"),
    TIDB_SQL(12, "TiDB SQL", "tidbConf"),
    ORACLE_SQL(13, "Oracle SQL", "oracleConf"),
    GREENPLUM_SQL(14, "Greenplum SQL", "greenplumConf"),
    KUBERNETES(15, "Kubernetes", "kubernetesConf"),
    PRESTO_SQL(16, "Presto SQL", "prestoConf"),
    NFS(17, "NFS", "nfsConf"),
    DTSCRIPT_AGENT(18,"DtScript Agent","dtScriptAgentConf"),
    INCEPTOR_SQL(19,"InceptorSql","inceptorSqlConf"),
//    FLINK_ON_STANDALONE(20,"FlinkOnStandalone","flinkOnStandaloneConf"),
    ANALYTICDB_FOR_PG(21, ComponentConstant.ANALYTICDB_FOR_PG_NAME,ComponentConstant.ANALYTICDB_FOR_PG_CONFIG_NAME),
    MYSQL(22, "Mysql", "mysqlConf"),
    SQL_SERVER(23, "SqlServer", "sqlServerConf"),
    DB2(24, "DB2", "db2Conf"),
    OCEANBASE(25, "OceanBase", "oceanBaseConf");

    private Integer typeCode;

    private String name;

    private String confName;

    EComponentType(int typeCode, String name, String confName) {
        this.typeCode = typeCode;
        this.name = name;
        this.confName = confName;
    }

    private static final Map<Integer,EComponentType> COMPONENT_TYPE_CODE_MAP=new ConcurrentHashMap<>(16);
    private static final Map<String ,EComponentType> COMPONENT_TYPE_NAME_MAP=new ConcurrentHashMap<>(16);
    private static final Map<String ,EComponentType> COMPONENT_TYPE_CONF_NAME_MAP=new ConcurrentHashMap<>(16);
    static {
        for (EComponentType componentType : EComponentType.values()) {
            COMPONENT_TYPE_CODE_MAP.put(componentType.getTypeCode(),componentType);
            COMPONENT_TYPE_NAME_MAP.put(componentType.getName(),componentType);
            COMPONENT_TYPE_CONF_NAME_MAP.put(componentType.getConfName(),componentType);
        }
    }

    public static EComponentType getByCode(int code) {
        EComponentType componentType = COMPONENT_TYPE_CODE_MAP.get(code);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with type code:" + code);
    }

    public static EComponentType getByName(String name) {
        EComponentType componentType = COMPONENT_TYPE_NAME_MAP.get(name);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with name:" + name);
    }

    public static EComponentType getByConfName(String confName) {
        EComponentType componentType = COMPONENT_TYPE_CONF_NAME_MAP.get(confName);
        if (Objects.nonNull(componentType)){
            return componentType;
        }

        throw new IllegalArgumentException("No enum constant with conf name:" + confName);
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public String getConfName() {
        return confName;
    }


    // 资源调度组件
    private static List<EComponentType> ResourceScheduling = Lists.newArrayList(EComponentType.YARN, EComponentType.KUBERNETES);

    // 存储组件
    private static List<EComponentType> StorageScheduling = Lists.newArrayList(EComponentType.HDFS, EComponentType.NFS);

    // 计算组件
    private static List<EComponentType> ComputeScheduling = Lists.newArrayList(
            EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER,
            EComponentType.IMPALA_SQL, EComponentType.DT_SCRIPT,
            EComponentType.LEARNING, EComponentType.TIDB_SQL,
            EComponentType.PRESTO_SQL, EComponentType.LIBRA_SQL,
            EComponentType.ORACLE_SQL, EComponentType.CARBON_DATA,
            EComponentType.GREENPLUM_SQL,EComponentType.INCEPTOR_SQL,
            EComponentType.DTSCRIPT_AGENT,EComponentType.ANALYTICDB_FOR_PG,
            EComponentType.MYSQL,EComponentType.SQL_SERVER,
            EComponentType.DB2,EComponentType.OCEANBASE
    );

    private static List<EComponentType> CommonScheduling = Lists.newArrayList(EComponentType.SFTP);

    public static EComponentScheduleType getScheduleTypeByComponent(Integer componentCode) {
        EComponentType code = getByCode(componentCode);
        if (ComputeScheduling.contains(code)) {
            return EComponentScheduleType.COMPUTE;
        }
        if (ResourceScheduling.contains(code)) {
            return EComponentScheduleType.RESOURCE;
        }
        if (StorageScheduling.contains(code)) {
            return EComponentScheduleType.STORAGE;
        }
        if (CommonScheduling.contains(code)) {
            return EComponentScheduleType.COMMON;
        }
        throw new RdosDefineException("不支持的组件");
    }


    // hadoop引擎组件
    private static List<EComponentType> HadoopComponents = Lists.newArrayList(
            EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER,
            EComponentType.IMPALA_SQL, EComponentType.DT_SCRIPT,
            EComponentType.LEARNING, EComponentType.YARN,
            EComponentType.KUBERNETES, EComponentType.SFTP,
            EComponentType.CARBON_DATA,EComponentType.INCEPTOR_SQL
    );

    // TiDB引擎组件
    private static List<EComponentType> TiDBComponents = Lists.newArrayList(EComponentType.TIDB_SQL);

    // LibrA引擎组件
    private static List<EComponentType> LibrAComponents = Lists.newArrayList(EComponentType.LIBRA_SQL);

    // Oracle引擎组件
    private static List<EComponentType> OracleComponents = Lists.newArrayList(EComponentType.ORACLE_SQL);

    private static List<EComponentType> GreenplumComponents = Lists.newArrayList(EComponentType.GREENPLUM_SQL);

    //Presto引擎组件
    private static List<EComponentType> PrestoComponents = Lists.newArrayList(EComponentType.PRESTO_SQL);

    private static List<EComponentType> EmptyComponents = Lists.newArrayList(EComponentType.DTSCRIPT_AGENT);

    private static List<EComponentType> analyticDbForPgComponents = Collections.unmodifiableList(Lists.newArrayList(EComponentType.ANALYTICDB_FOR_PG));

    public static MultiEngineType getEngineTypeByComponent(EComponentType componentType,Integer deployType) {
        if(EComponentType.FLINK.equals(componentType) && EDeployType.STANDALONE.getType() == deployType){
            return MultiEngineType.FLINK_ON_STANDALONE;
        }
        if (HadoopComponents.contains(componentType)) {
            return MultiEngineType.HADOOP;
        }
        return null;
    }

    // 需要添加TypeName的组件
    public static List<EComponentType> typeComponentVersion = Lists.newArrayList(
            EComponentType.DT_SCRIPT, EComponentType.FLINK,
            EComponentType.LEARNING, EComponentType.SPARK,
            EComponentType.HDFS
    );

    /**
     * 直接定位的插件的组件类型
     *
     * @param componentCode
     * @return
     */
    public static String convertPluginNameByComponent(EComponentType componentCode) {
        switch (componentCode) {
            case TIDB_SQL:
                return "tidb";
            case ORACLE_SQL:
                return "oracle";
            case SFTP:
                return "dummy";
            case LIBRA_SQL:
                return "postgresql";
            case IMPALA_SQL:
                return "impala";
            case GREENPLUM_SQL:
                return "greenplum";
            case PRESTO_SQL:
                return "presto";
            case KUBERNETES:
                return "kubernetes";
            case NFS:
                return "nfs";
            case INCEPTOR_SQL:
                return "inceptor";
            case DTSCRIPT_AGENT:
                return "dtscript-agent";
            case ANALYTICDB_FOR_PG:
                return ComponentConstant.ANALYTICDB_FOR_PG_PLUGIN;
            case SQL_SERVER:
                return "sqlserver";
            case DB2:
                return "db2";
            case OCEANBASE:
                return "oceanbase";
        }
        return "";
    }


    /**
     * 需要拼接  调度-存储-组件 的组件类型
     *
     * @param componentCode
     * @return
     */
    public static String convertPluginNameWithNeedVersion(EComponentType componentCode) {
        switch (componentCode) {
            case SPARK:
                return "spark";
            case FLINK:
                return "flink";
            case LEARNING:
                return "learning";
            case DT_SCRIPT:
                return "dtscript";
            case HDFS:
                return "hadoop";
        }
        return "";
    }

    public static List<EComponentType> notCheckComponent = Lists.newArrayList(
            EComponentType.SPARK, EComponentType.DT_SCRIPT,
            EComponentType.LEARNING, EComponentType.FLINK
    );

    //允许一个组件 on yarn 或 其他多种模式
    public static List<EComponentType> deployTypeComponents = Lists.newArrayList(EComponentType.FLINK);
    //允许一个组件多个版本
    public static List<EComponentType> multiVersionComponents = Lists.newArrayList(EComponentType.FLINK,EComponentType.SPARK);

    //对应引擎的组件不能删除
    public static List<EComponentType> requireComponent = Lists.newArrayList(
            EComponentType.ORACLE_SQL, EComponentType.TIDB_SQL,
            EComponentType.LIBRA_SQL, EComponentType.GREENPLUM_SQL,
            EComponentType.PRESTO_SQL
    );

    //SQL组件
    public static List<EComponentType> sqlComponent = Lists.newArrayList(
            EComponentType.SPARK_THRIFT, EComponentType.HIVE_SERVER,
            EComponentType.TIDB_SQL, EComponentType.ORACLE_SQL,
            EComponentType.LIBRA_SQL, EComponentType.IMPALA_SQL,
            EComponentType.GREENPLUM_SQL, EComponentType.PRESTO_SQL,
            EComponentType.INCEPTOR_SQL,EComponentType.ANALYTICDB_FOR_PG,
            EComponentType.MYSQL, EComponentType.SQL_SERVER,
            EComponentType.DB2, EComponentType.OCEANBASE
    );


    //没有控件渲染的组件
    public static List<EComponentType> noControlComponents = Lists.newArrayList(EComponentType.YARN, EComponentType.KUBERNETES,EComponentType.HDFS);

    //多hadoop版本选择组件
    public static List<EComponentType> hadoopVersionComponents = Lists.newArrayList(EComponentType.YARN,EComponentType.HDFS);

    //metadata组件
    public static List<EComponentType> metadataComponents = Lists.newArrayList(EComponentType.HIVE_SERVER,EComponentType.SPARK_THRIFT);


}

