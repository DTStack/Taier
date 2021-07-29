package com.dtstack.batch.enums;

import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;

/**
 * @author chener
 * @Classname TaskEngineType
 * @Description TODO
 * @Date 2020/7/16 10:58
 * @Created chener@dtstack.com
 */
public enum TaskEngineType {
    /**
     * VIRTUAL
     */
    VIRTUAL(EJobType.VIRTUAL,null),
    /**
     * SPARK_SQL
     */
    SPARK_SQL(EJobType.SPARK_SQL, MultiEngineType.HADOOP),
    /**
     * SPARK
     */
    SPARK(EJobType.SPARK, MultiEngineType.HADOOP),
    /**
     * SYNC
     */
    SYNC(EJobType.SYNC, MultiEngineType.HADOOP),
    /**
     * SPARK_PYTHON
     */
    SPARK_PYTHON(EJobType.SPARK_PYTHON, MultiEngineType.HADOOP),
    /**
     * PYTHON
     */
    PYTHON(EJobType.PYTHON, MultiEngineType.HADOOP),
    /**
     * SHELL
     */
    SHELL(EJobType.SHELL, MultiEngineType.HADOOP),
    /**
     * HADOOP_MR
     */
    HADOOP_MR(EJobType.HADOOP_MR, MultiEngineType.HADOOP),
    /**
     * WORK_FLOW
     */
    WORK_FLOW(EJobType.WORK_FLOW,null),
    /**
     * CARBON_SQL
     */
    CARBON_SQL(EJobType.CARBON_SQL, MultiEngineType.HADOOP),
    /**
     * LIBRA_SQL
     */
    LIBRA_SQL(EJobType.LIBRA_SQL, MultiEngineType.LIBRA),
    /**
     * HIVE_SQL
     */
    HIVE_SQL(EJobType.HIVE_SQL, MultiEngineType.HADOOP),
    /**
     * IMPALA_SQL
     */
    IMPALA_SQL(EJobType.IMPALA_SQL, MultiEngineType.HADOOP),
    /**
     * TIDB_SQL
     */
    TIDB_SQL(EJobType.TIDB_SQL, MultiEngineType.TIDB),
    /**
     * ORACLE_SQL
     */
    ORACLE_SQL(EJobType.ORACLE_SQL, MultiEngineType.ORACLE),
    /**
     * GREENPLUM_SQL
     */
    GREENPLUM_SQL(EJobType.GREENPLUM_SQL, MultiEngineType.GREENPLUM),

    /**
     * ANALYTICDB_FOR_PG SQL
     */
    ANALYTICDB_FOR_PG(EJobType.ANALYTICDB_FOR_PG, MultiEngineType.ANALYTICDB_FOR_PG);

    private EJobType jobType;

    private MultiEngineType engineType;

    public EJobType getJobType() {
        return jobType;
    }

    public MultiEngineType getEngineType() {
        return engineType;
    }

    TaskEngineType(EJobType jobType, MultiEngineType engineType) {
        this.engineType = engineType;
        this.jobType = jobType;
    }

    public static MultiEngineType getEngineTypeByTaskTypeInt(Integer taskType){
        for (TaskEngineType type:values()){
            if (type.getJobType().getType().equals(taskType)){
                return type.getEngineType();
            }
        }
        return null;
    }

    public static MultiEngineType getEngineTypeByTaskType(EJobType taskType){
        for (TaskEngineType type:values()){
            if (type.getJobType().equals(taskType)){
                return type.getEngineType();
            }
        }
        return null;
    }
}
