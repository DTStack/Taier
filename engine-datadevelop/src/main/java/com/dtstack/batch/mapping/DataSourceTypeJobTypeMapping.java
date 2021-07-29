package com.dtstack.batch.mapping;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/4/1 2:56 下午
 */
public class DataSourceTypeJobTypeMapping {

    private final static Map<Integer, EJobType> mappingMap = Maps.newHashMap();

    static {
        mappingMap.put(DataSourceType.SparkThrift2_1.getVal(), EJobType.SPARK_SQL);
        mappingMap.put(DataSourceType.Spark.getVal(), EJobType.SPARK_SQL);
        mappingMap.put(DataSourceType.HIVE.getVal(), EJobType.HIVE_SQL);
        mappingMap.put(DataSourceType.HIVE1X.getVal(), EJobType.HIVE_SQL);
        mappingMap.put(DataSourceType.HIVE3X.getVal(), EJobType.HIVE_SQL);
        mappingMap.put(DataSourceType.IMPALA.getVal(), EJobType.IMPALA_SQL);
        mappingMap.put(DataSourceType.Oracle.getVal(), EJobType.ORACLE_SQL);
        mappingMap.put(DataSourceType.TiDB.getVal(), EJobType.TIDB_SQL);
        mappingMap.put(DataSourceType.GREENPLUM6.getVal(), EJobType.GREENPLUM_SQL);
        mappingMap.put(DataSourceType.LIBRA.getVal(), EJobType.LIBRA_SQL);
        mappingMap.put(DataSourceType.INCEPTOR.getVal(), EJobType.INCEPTOR_SQL);
        mappingMap.put(DataSourceType.ADB_FOR_PG.getVal(), EJobType.ANALYTICDB_FOR_PG);
    }

    public static Integer getJobTypeByDataSourceType(Integer dataSourceType){
        return getTaskTypeByDataSourceType(dataSourceType).getType();
    }

    /**
     * 获取 根据dataSourceType 获取jobType
     *
     * @param dataSourceType
     * @return
     */
    public static EJobType getTaskTypeByDataSourceType(Integer dataSourceType){
        EJobType jobType = mappingMap.get(dataSourceType);
        if(jobType == null){
            throw new RdosDefineException("无法通过dataSourceType获取jobType");
        }
        return jobType;
    }
}
