package com.dtstack.engine.lineage.batch;

/**
 * @author chener
 * @Classname DataSourceTypeConvert
 * @Description TODO
 * @Date 2020/12/1 14:18
 * @Created chener@dtstack.com
 */
public enum BatchDataSourceTypeConvert {
    HIVE1(1,27),
    HIVE2(2,7),
    SPARK_THRIFT(3,7),
    IMPALA(4,29),
    TIDB(5,31),
    ORACLE(6,2),
    LIBRA(7,21),
    MYSQL(8,1),
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
