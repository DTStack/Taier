package com.dtstack.engine.master.enums;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.collect.Lists;

import java.util.List;

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
    NFS(17, "NFS", "nfsConf");

    private int typeCode;

    private String name;

    private String confName;

    EComponentType(int typeCode, String name, String confName) {
        this.typeCode = typeCode;
        this.name = name;
        this.confName = confName;
    }

    public static EComponentType getByCode(int code) {
        for (EComponentType value : EComponentType.values()) {
            if (value.getTypeCode() == code) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + code);
    }

    public static EComponentType getByName(String name) {
        for (EComponentType value : EComponentType.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with name:" + name);
    }

    public static EComponentType getByConfName(String ConfName) {
        for (EComponentType value : EComponentType.values()) {
            if (value.getConfName().equalsIgnoreCase(ConfName)) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with conf name:" + ConfName);
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public String getConfName() {
        return confName;
    }


    // 资源调度组件
    public static List<EComponentType> ResourceScheduling = Lists.newArrayList(EComponentType.YARN, EComponentType.KUBERNETES);

    // 存储组件
    public static List<EComponentType> StorageScheduling = Lists.newArrayList(EComponentType.HDFS, EComponentType.NFS);

    // 计算组件
    public static List<EComponentType> ComputeScheduling = Lists.newArrayList(EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER, EComponentType.IMPALA_SQL, EComponentType.DT_SCRIPT,
            EComponentType.LEARNING, EComponentType.TIDB_SQL, EComponentType.PRESTO_SQL, EComponentType.LIBRA_SQL, EComponentType.ORACLE_SQL, EComponentType.CARBON_DATA, EComponentType.GREENPLUM_SQL);

    public static List<EComponentType> CommonScheduling = Lists.newArrayList(EComponentType.SFTP);


    // hadoop引擎组件
    public static List<EComponentType> HadoopComponents = Lists.newArrayList(EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER, EComponentType.IMPALA_SQL, EComponentType.DT_SCRIPT,
            EComponentType.LEARNING, EComponentType.YARN, EComponentType.KUBERNETES, EComponentType.SFTP, EComponentType.CARBON_DATA);

    // TiDB引擎组件
    public static List<EComponentType> TiDBComponents = Lists.newArrayList(EComponentType.TIDB_SQL);

    // LibrA引擎组件
    public static List<EComponentType> LibrAComponents = Lists.newArrayList(EComponentType.LIBRA_SQL);

    // Oracle引擎组件
    public static List<EComponentType> OracleComponents = Lists.newArrayList(EComponentType.ORACLE_SQL);

    public static List<EComponentType> GreenplumComponents = Lists.newArrayList(EComponentType.GREENPLUM_SQL);

    //Presto引擎组件
    public static List<EComponentType> PrestoComponents = Lists.newArrayList(EComponentType.PRESTO_SQL);


    public static MultiEngineType getEngineTypeByComponent(EComponentType componentType) {
        if (HadoopComponents.contains(componentType)) {
            return MultiEngineType.HADOOP;
        }
        if (LibrAComponents.contains(componentType)) {
            return MultiEngineType.LIBRA;
        }
        if (OracleComponents.contains(componentType)) {
            return MultiEngineType.ORACLE;
        }
        if (TiDBComponents.contains(componentType)) {
            return MultiEngineType.TIDB;
        }
        if (GreenplumComponents.contains(componentType)) {
            return MultiEngineType.GREENPLUM;
        }
        if (PrestoComponents.contains(componentType)) {
            return MultiEngineType.PRESTO;
        }
        return null;
    }

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

    // 需要添加TypeName的组件
    public static List<EComponentType> typeComponentVersion = Lists.newArrayList(EComponentType.DT_SCRIPT, EComponentType.FLINK, EComponentType.LEARNING, EComponentType.SPARK,
            EComponentType.HDFS, EComponentType.FLINK);

    public static List<EComponentType> notCheckComponent = Lists.newArrayList(EComponentType.SPARK, EComponentType.DT_SCRIPT, EComponentType.LEARNING, EComponentType.FLINK);

    //SQL组件
    public static List<EComponentType> sqlComponent = Lists.newArrayList(EComponentType.SPARK_THRIFT, EComponentType.HIVE_SERVER, EComponentType.TIDB_SQL, EComponentType.ORACLE_SQL,
            EComponentType.LIBRA_SQL, EComponentType.IMPALA_SQL, EComponentType.GREENPLUM_SQL, EComponentType.PRESTO_SQL);

    //对应引擎的组件不能删除
    public static List<EComponentType> requireComponent = Lists.newArrayList(EComponentType.ORACLE_SQL, EComponentType.HDFS, EComponentType.TIDB_SQL, EComponentType.ORACLE_SQL,
            EComponentType.LIBRA_SQL, EComponentType.GREENPLUM_SQL, EComponentType.PRESTO_SQL);

    //基础配置信息
    public static List<String> BASE_CONFIG = Lists.newArrayList(EComponentType.HDFS.getConfName(),EComponentType.NFS.getConfName(),
            EComponentType.YARN.getConfName(), EComponentType.SPARK_THRIFT.getConfName(), EComponentType.SFTP.getConfName(),EComponentType.KUBERNETES.getConfName());

}
