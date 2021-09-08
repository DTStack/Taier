package com.dtstack.engine.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 8:30 下午 2020/11/25
 */
public enum  DataSourceTypeEnum {

    SPARK_THRIFT(6, "SparkThrift", "hiveConf"),
    LIBRA_SQL(8, "LibrA SQL", "libraConf"),
    IMPALA_SQL(11, "Impala SQL", "impalaSqlConf"),
    TIDB_SQL(12, "TiDB SQL", "tidbConf"),
    ORACLE_SQL(13, "Oracle SQL", "oracleConf"),
    GREENPLUM_SQL(14, "Greenplum SQL", "greenplumConf");

    private int typeCode;

    private String name;

    private String confName;

    DataSourceTypeEnum(int typeCode, String name, String confName) {
        this.typeCode = typeCode;
        this.name = name;
        this.confName = confName;
    }

    public static DataSourceTypeEnum getByCode(int code) {
        for (DataSourceTypeEnum value : DataSourceTypeEnum.values()) {
            if (value.getTypeCode() == code) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + code);
    }

    public static DataSourceTypeEnum getByName(String name) {
        for (DataSourceTypeEnum value : DataSourceTypeEnum.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with name:" + name);
    }

    public static DataSourceTypeEnum getByConfName(String ConfName) {
        for (DataSourceTypeEnum value : DataSourceTypeEnum.values()) {
            if (value.getConfName().equalsIgnoreCase(ConfName)) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with conf name:" + ConfName);
    }

    public static List<Integer> getAllTypeCodes(){
        DataSourceTypeEnum[] values = DataSourceTypeEnum.values();
        return Arrays.stream(values).map(DataSourceTypeEnum::getTypeCode).collect(Collectors.toList());
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
