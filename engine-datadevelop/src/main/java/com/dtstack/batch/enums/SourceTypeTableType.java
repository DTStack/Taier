package com.dtstack.batch.enums;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.dtcenter.loader.source.DataSourceType;

/**
 * @author chener
 * @Classname SourceTypeTableType
 * @Description TODO
 * @Date 2020/8/31 21:12
 * @Created chener@dtstack.com
 */
public enum SourceTypeTableType {

    /**
     * hive
     */
    HIVE(ETableType.HIVE, DataSourceType.HIVE),

    /**
     * hive1x
     */
    HIVE1X(ETableType.HIVE, DataSourceType.HIVE1X),

    /**
     * hive3x
     */
    HIVE3X(ETableType.HIVE, DataSourceType.HIVE3X),

    /**
     * libra
     */
    LIBRA(ETableType.LIBRA, DataSourceType.LIBRA),

    /**
     * mysql5
     */
    TIDB(ETableType.TIDB, DataSourceType.TiDB),

    /**
     * oracle
     */
    ORACLE(ETableType.ORACLE, DataSourceType.Oracle),

    /**
     * greenplum6
     */
    GREENPLUM(ETableType.GREENPLUM, DataSourceType.GREENPLUM6),

    /**
     * impala
     */
    IMPALA(ETableType.IMPALA, DataSourceType.IMPALA),

    /**
     * SparkThrift
     */
    SPARKTHRIFT(ETableType.HIVE, DataSourceType.SparkThrift2_1),

    /**
     * AnalyticDB for PostgreSQL
     */
    ADB_FOR_PG(ETableType.ADB_FOR_PG, DataSourceType.ADB_FOR_PG);

    private ETableType tableType;

    private DataSourceType sourceType;

    SourceTypeTableType(ETableType tableType, DataSourceType sourceType) {
        this.tableType = tableType;
        this.sourceType = sourceType;
    }

    public ETableType getTableType() {
        return tableType;
    }

    public DataSourceType getSourceType() {
        return sourceType;
    }

    public static ETableType getBySourceType(Integer sourceType){
        for (SourceTypeTableType sourceTypeTableType:values()){
            if (sourceTypeTableType.getSourceType().getVal() == sourceType){
                return sourceTypeTableType.getTableType();
            }
        }
        return null;
    }
}
