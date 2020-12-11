package com.dtstack.lineage.enums;

import com.dtstack.engine.api.enums.DataSourceType;
import com.dtstack.schedule.common.enums.EScheduleJobType;

import java.util.Objects;

/**
 * @author chener
 * @Classname EngineTaskType2SourceType
 * @Description TODO
 * @Date 2020/12/11 14:57
 * @Created chener@dtstack.com
 */
public enum EngineTaskType2SourceType {
    HIVE1(DataSourceType.HIVE1,null),
    HIVE2(DataSourceType.HIVE2, EScheduleJobType.HIVE_SQL),
    SPARK_THRIFT(DataSourceType.SPARK_THRIFT,EScheduleJobType.SPARK_SQL),
    IMPALA(DataSourceType.IMPALA,EScheduleJobType.IMPALA_SQL),
    TIDB(DataSourceType.TIDB,EScheduleJobType.TIDB_SQL),
    ORACLE(DataSourceType.ORACLE,EScheduleJobType.ORACLE_SQL),
    LIBRA(DataSourceType.LIBRA,EScheduleJobType.LIBRA_SQL),
    MYSQL(DataSourceType.MYSQL,null),
    GREENPLUM(DataSourceType.GREENPLUM,EScheduleJobType.GREENPLUM_SQL),
    SQLSERVER(DataSourceType.SQLSERVER,null),;

    private DataSourceType dataSourceType;

    private EScheduleJobType taskType;

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public EScheduleJobType getTaskType() {
        return taskType;
    }

    EngineTaskType2SourceType(DataSourceType dataSourceType, EScheduleJobType taskType) {
        this.dataSourceType = dataSourceType;
        this.taskType = taskType;
    }

    public static DataSourceType getDataSourceTypeByTaskType(EScheduleJobType taskType){
        if (Objects.isNull(taskType)){
            return null;
        }
        for (EngineTaskType2SourceType holderType : values()){
            EScheduleJobType taskType1 = holderType.getTaskType();
            if (taskType.equals(taskType1)){
                return holderType.getDataSourceType();
            }
        }
        return null;
    }

    public static DataSourceType getDataSourceTypeByTaskTypeInt(Integer taskType){
        if (Objects.isNull(taskType)){
            return null;
        }
        for (EngineTaskType2SourceType holderType : values()){
            EScheduleJobType taskType1 = holderType.getTaskType();
            if (Objects.isNull(taskType1)){
                continue;
            }
            if (taskType1.getVal().equals(taskType)){
                return holderType.getDataSourceType();
            }
        }
        return null;
    }
}
