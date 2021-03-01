package com.dtstack.engine.common.enums;

import com.dtstack.schedule.common.enums.DataSourceType;

/**
 * 插件名和数据源枚举对应关系
 * @author
 * @date 2019/5/30
 */
public enum EComponentTypeDataSourceType {

    SPARK_THRIFT(DataSourceType.Spark, EComponentType.SPARK_THRIFT),
    CARBON_DATA(DataSourceType.CarbonData, EComponentType.CARBON_DATA),
    LIBRA_SQL(DataSourceType.LIBRA, EComponentType.LIBRA_SQL),
    HIVE_SERVER(DataSourceType.HIVE, EComponentType.HIVE_SERVER),
    IMPALA_SQL(DataSourceType.IMPALA, EComponentType.IMPALA_SQL),
    TIDB_SQL(DataSourceType.TiDB, EComponentType.TIDB_SQL),
    ORACLE_SQL(DataSourceType.Oracle, EComponentType.ORACLE_SQL),
    GREENPLUM_SQL(DataSourceType.GREENPLUM6, EComponentType.GREENPLUM_SQL),
    PRESTO_SQL(DataSourceType.Presto, EComponentType.PRESTO_SQL);

    private DataSourceType dataSourceType;

    private EComponentType componentType;

    EComponentTypeDataSourceType(DataSourceType typeCode, EComponentType componentType) {
        this.dataSourceType = typeCode;
        this.componentType = componentType;
    }

    public static EComponentTypeDataSourceType getByCode(int dataSourceType) {
        for (EComponentTypeDataSourceType value : EComponentTypeDataSourceType.values()) {
            if (value.getDataSourceType().getVal() == dataSourceType) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + dataSourceType);
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public EComponentType getComponentType() {
        return componentType;
    }
}
