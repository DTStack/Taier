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
    KUBERNETES(14, "Kubernetes", "kubernetesConf");

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
    public static List<EComponentType> StorageScheduling = Lists.newArrayList(EComponentType.HDFS);

    // 计算组件
    public static List<EComponentType> ComputeScheduling = Lists.newArrayList(EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER, EComponentType.IMPALA_SQL, EComponentType.DT_SCRIPT,
            EComponentType.LEARNING, EComponentType.TIDB_SQL, EComponentType.LIBRA_SQL, EComponentType.ORACLE_SQL);

    public static List<EComponentType> CommonScheduling = Lists.newArrayList(EComponentType.SFTP);


    // hadoop引擎组件
    public static List<EComponentType> HadoopComponents = Lists.newArrayList(EComponentType.SPARK, EComponentType.SPARK_THRIFT,
            EComponentType.FLINK, EComponentType.HIVE_SERVER, EComponentType.IMPALA_SQL, EComponentType.DT_SCRIPT,
            EComponentType.LEARNING, EComponentType.YARN, EComponentType.KUBERNETES, EComponentType.HDFS, EComponentType.SFTP);

    // TiDB引擎组件
    public static List<EComponentType> TiDBComponents = Lists.newArrayList(EComponentType.TIDB_SQL);

    // LibrA引擎组件
    public static List<EComponentType> LibrAComponents = Lists.newArrayList(EComponentType.LIBRA_SQL);

    // Oracle引擎组件
    public static List<EComponentType> OracleComponents = Lists.newArrayList(EComponentType.ORACLE_SQL);


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
        throw new RdosDefineException("不支持的引擎组件");
    }

    public static EComponentScheduleType getScheduleTypeByComponent(Integer componentCode) {
        EComponentType code = getByCode(componentCode);
        if (ComputeScheduling.contains(code)) {
            return EComponentScheduleType.computeScheduling;
        }
        if (ResourceScheduling.contains(code)) {
            return EComponentScheduleType.resourceScheduling;
        }
        if (StorageScheduling.contains(code)) {
            return EComponentScheduleType.storageScheduling;
        }
        if (CommonScheduling.contains(code)) {
            return EComponentScheduleType.commonScheduling;
        }
        throw new RdosDefineException("不支持的组件");
    }


    public static String convertPluginNameByComponent(EComponentType componentCode){
        switch (componentCode){
            case SPARK_THRIFT:
            case HIVE_SERVER:
                return "hive";
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

        }
        return "";
    }

    // 需要添加TypeName的组件
    public static List<EComponentType> typeComponentVersion = Lists.newArrayList(EComponentType.DT_SCRIPT,EComponentType.FLINK,EComponentType.LEARNING,EComponentType.SPARK,
            EComponentType.HDFS);

    public static List<EComponentType> notCheckComponent = Lists.newArrayList(EComponentType.SPARK,EComponentType.DT_SCRIPT,EComponentType.LEARNING,EComponentType.FLINK);

}
