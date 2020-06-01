package com.dtstack.engine.master.enums;

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
    SPARK_THRIFT(6, "SparkThrift","hiveConf"),
    CARBON_DATA(7, "CarbonData ThriftServer", "carbonConf"),
    LIBRA_SQL(8, "LibrA SQL", "libraConf"),
    HIVE_SERVER(9,"HiveServer","hiveServerConf"),
    SFTP(10,"SFTP","sftpConf"),
    IMPALA_SQL(11,"Impala SQL","impalaSqlConf"),
    TIDB_SQL(12,"TiDB SQL","tidbConf"),
    ORACLE_SQL(13,"Oracle SQL","oracleConf"),
    GREENPLUM_SQL(14, "Greenplum SQL", "greenplumConf")
    ;

    private int typeCode;

    private String name;

    private String confName;

    EComponentType(int typeCode, String name, String confName) {
        this.typeCode = typeCode;
        this.name = name;
        this.confName = confName;
    }

    public static EComponentType getByCode(int code){
        for (EComponentType value : EComponentType.values()) {
            if (value.getTypeCode() == code) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + code);
    }

    public static EComponentType getByName(String name){
        for (EComponentType value : EComponentType.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with name:" + name);
    }

    public static EComponentType getByConfName(String ConfName){
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
}
