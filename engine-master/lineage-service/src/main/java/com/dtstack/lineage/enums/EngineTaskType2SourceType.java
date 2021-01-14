package com.dtstack.lineage.enums;

import com.dtstack.schedule.common.enums.DataSourceType;
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
    HIVE1(DataSourceType.HIVE1X,null),
    HIVE2(DataSourceType.HIVE, EScheduleJobType.HIVE_SQL),
    SPARK_THRIFT(DataSourceType.Spark,EScheduleJobType.SPARK_SQL),
    IMPALA(DataSourceType.IMPALA,EScheduleJobType.IMPALA_SQL),
    TIDB(DataSourceType.TiDB,EScheduleJobType.TIDB_SQL),
    ORACLE(DataSourceType.Oracle,EScheduleJobType.ORACLE_SQL),
    LIBRA(DataSourceType.LIBRA,EScheduleJobType.LIBRA_SQL),
    MYSQL(DataSourceType.MySQL,null),
    GREENPLUM(DataSourceType.GREENPLUM6,EScheduleJobType.GREENPLUM_SQL),
    SQLSERVER(DataSourceType.SQLServer,null),;

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
