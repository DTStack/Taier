package com.dtstack.engine.lineage.batch;

/**
 * @author chener
 * @Classname DataSourceTypeConvert
 * @Description TODO
 * @Date 2020/12/1 14:18
 * @Created chener@dtstack.com
 */
public enum BatchDataSourceTypeConvert {
    /**
     * hive1
     */
    HIVE1(1,27),
    /**
     * hive2
     */
    HIVE2(2,7),
    /**
     * spark thrift
     */
    SPARK_THRIFT(3,7),
    /**
     * impala
     */
    IMPALA(4,29),
    /**
     * tidb
     */
    TIDB(5,31),
    /**
     * oracle
     */
    ORACLE(6,2),
    /**
     * libra
     */
    LIBRA(7,21),
    /**
     * mysql
     */
    MYSQL(8,1),
    /**
     * greenplum
     */
    GREENPLUM(9,36),;

    private int engineSourceType;

    private int batchSourceType;

    public int getEngineSourceType() {
        return engineSourceType;
    }

    public int getBatchSourceType() {
        return batchSourceType;
    }

    BatchDataSourceTypeConvert(int engineSourceType, int batchSourceType) {
        this.engineSourceType = engineSourceType;
        this.batchSourceType = batchSourceType;
    }

    public static int getEngineSourceTypeByBatchType(int batchSourceType){
        for (BatchDataSourceTypeConvert typeConvert:values()){
            if (typeConvert.getBatchSourceType() == batchSourceType){
                return typeConvert.getEngineSourceType();
            }
        }
        return 0;
    }
}
