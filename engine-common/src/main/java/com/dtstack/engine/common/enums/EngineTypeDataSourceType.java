package com.dtstack.engine.common.enums;

import com.dtstack.schedule.common.enums.DataSourceType;

import java.util.Objects;

/**
 * 类名称:EngineTypeDataSourceType
 * 类描述:engineType和数据源类型枚举对应关系
 * 创建人:newman
 * 创建时间:2021/4/28 7:58 下午
 * Version 1.0
 */
public enum EngineTypeDataSourceType {


    HIVE1(DataSourceType.HIVE1X,null),
    HIVE2(DataSourceType.HIVE, EngineType.Hive),
    SPARK_THRIFT(DataSourceType.SPARKTHRIFT2_1,EngineType.Spark),
    IMPALA(DataSourceType.IMPALA, EngineType.Impala),
    TIDB(DataSourceType.TiDB,EngineType.TiDB),
    ORACLE(DataSourceType.Oracle,EngineType.Oracle),
    LIBRA(DataSourceType.LIBRA,EngineType.PostgreSQL),
    MYSQL(DataSourceType.MySQL,null),
    GREENPLUM(DataSourceType.GREENPLUM6,EngineType.GreenPlum),
    SQLSERVER(DataSourceType.SQLServer,null),
    ADB_POSTGREPSQL(DataSourceType.ADB_POSTGREPSQL,EngineType.AnalyticdbForPg);

    private DataSourceType dataSourceType;

    private EngineType engineType;

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    EngineTypeDataSourceType(DataSourceType dataSourceType, EngineType engineType) {
        this.dataSourceType = dataSourceType;
        this.engineType = engineType;
    }

    public static DataSourceType getDataSourceTypeByTaskType(EngineType engineType){
        if (Objects.isNull(engineType)){
            return null;
        }
        for (EngineTypeDataSourceType holderType : values()){
            EngineType engineTye1 = holderType.getEngineType();
            if (engineType.equals(engineTye1)){
                return holderType.getDataSourceType();
            }
        }
        return null;
    }

    public static DataSourceType getDataSourceTypeByTaskTypeString(String engineType){
        if (Objects.isNull(engineType)){
            return null;
        }
        for (EngineTypeDataSourceType holderType : values()){
            EngineType engineType1 = holderType.getEngineType();
            if (Objects.isNull(engineType1)){
                continue;
            }
            if (engineType1.name().equals(engineType)){
                return holderType.getDataSourceType();
            }
        }
        return null;
    }
}
