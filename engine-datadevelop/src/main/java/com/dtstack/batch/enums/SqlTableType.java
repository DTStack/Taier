package com.dtstack.batch.enums;

import com.dtstack.batch.common.enums.ETableType;

/**
 * @author chener
 * @Classname SqlTableType
 * @Description TODO
 * @Date 2020/8/10 20:10
 * @Created chener@dtstack.com
 */
public enum SqlTableType {

    /**
     * Hive
     */
    HIVE(ETableType.HIVE, com.dtstack.sqlparser.common.client.enums.ETableType.HIVE),

    /**
     * LibrA
     */
    LIBRA(ETableType.LIBRA, com.dtstack.sqlparser.common.client.enums.ETableType.LIBRA),

    /**
     * TiDB
     */
    TIDB(ETableType.TIDB, com.dtstack.sqlparser.common.client.enums.ETableType.TIDB),

    /**
     * Oracle
     */
    ORACLE(ETableType.ORACLE, com.dtstack.sqlparser.common.client.enums.ETableType.ORACLE),

    /**
     * GreenPlum
     */
    GREENPLUM(ETableType.GREENPLUM, com.dtstack.sqlparser.common.client.enums.ETableType.GREENPLUM),

    /**
     * IMPALA
     */
    IMPALA(ETableType.IMPALA, com.dtstack.sqlparser.common.client.enums.ETableType.IMPALA);

    private ETableType batchTableType;

    private com.dtstack.sqlparser.common.client.enums.ETableType sqlTableType;

    SqlTableType(ETableType batchTableType, com.dtstack.sqlparser.common.client.enums.ETableType sqlTableType) {
        this.batchTableType = batchTableType;
        this.sqlTableType = sqlTableType;
    }

    public ETableType getBatchTableType() {
        return batchTableType;
    }

    public com.dtstack.sqlparser.common.client.enums.ETableType getSqlTableType() {
        return sqlTableType;
    }

    public static SqlTableType getBySqlTableType(com.dtstack.sqlparser.common.client.enums.ETableType tableType){
        for (SqlTableType type:values()){
            if (type.getSqlTableType().equals(tableType)){
                return type;
            }
        }
        return null;
    }

    public static ETableType getTypeBySqlTableType(com.dtstack.sqlparser.common.client.enums.ETableType tableType){
        for (SqlTableType type:values()){
            if (type.getSqlTableType().equals(tableType)){
                return type.getBatchTableType();
            }
        }
        return null;
    }

    public static SqlTableType getByBatchTableType(ETableType tableType){
        for (SqlTableType type:values()){
            if (type.getBatchTableType().equals(tableType)){
                return type;
            }
        }
        return null;
    }

    public static com.dtstack.sqlparser.common.client.enums.ETableType getTypeByBatchTableType(ETableType tableType){
        for (SqlTableType type:values()){
            if (type.getBatchTableType().equals(tableType)){
                return type.getSqlTableType();
            }
        }
        return null;
    }

    public static com.dtstack.sqlparser.common.client.enums.ETableType getTypeByBatchTableType(int tableType){
        ETableType tType = ETableType.getTableType(tableType);
        if (tType == null){
            return null;
        }
       return getTypeByBatchTableType(tType);
    }
}
