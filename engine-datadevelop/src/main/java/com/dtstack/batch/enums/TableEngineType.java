package com.dtstack.batch.enums;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;

/**
 * @author chener
 * @Classname TableEngineType
 * @Description TODO
 * @Date 2020/7/16 11:29
 * @Created chener@dtstack.com
 */
public enum TableEngineType {
    /**
     * hive
     */
    HIVE(ETableType.HIVE, MultiEngineType.HADOOP),

    /**
     * Libra
     */
    LIBRA(ETableType.LIBRA, MultiEngineType.LIBRA),

    /**
     * TiDB
     */
    TIDB(ETableType.TIDB, MultiEngineType.TIDB),

    /**
     * Oracle
     */
    ORACLE(ETableType.ORACLE, MultiEngineType.ORACLE),

    /**
     * Greenplum
     */
    GREENPLUM(ETableType.GREENPLUM, MultiEngineType.GREENPLUM),

    /**
     * IMPALA
     */
    IMPALA(ETableType.IMPALA, MultiEngineType.HADOOP);

    private ETableType tableType;
    private MultiEngineType engineType;

    public ETableType getTableType() {
        return tableType;
    }

    public MultiEngineType getEngineType() {
        return engineType;
    }

    TableEngineType(ETableType tableType, MultiEngineType engineType) {
        this.tableType = tableType;
        this.engineType = engineType;
    }

    public static MultiEngineType getEngineTypeByTableType(ETableType tableType){
        for (TableEngineType type:values()){
            if (type.getTableType().equals(tableType)){
                return type.getEngineType();
            }
        }
        return null;
    }

    public static MultiEngineType getEngineTypeByTableTypeInt(Integer tableType){
        for (TableEngineType type:values()){
            if (type.getTableType().getType() == tableType){
                return type.getEngineType();
            }
        }
        return null;
    }
}
