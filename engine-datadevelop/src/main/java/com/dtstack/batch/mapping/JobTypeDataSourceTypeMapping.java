package com.dtstack.batch.mapping;

import com.dtstack.batch.common.enums.HiveVersion;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.Maps;

import java.util.Map;

public class JobTypeDataSourceTypeMapping {

    private final static Map<Integer, Integer> mappingMap = Maps.newHashMap();

    static {
        mappingMap.put(EJobType.SPARK_SQL.getVal(), DataSourceType.SparkThrift2_1.getVal());
        mappingMap.put(EJobType.HIVE_SQL.getVal(), DataSourceType.HIVE.getVal());
        mappingMap.put(EJobType.IMPALA_SQL.getVal(), DataSourceType.IMPALA.getVal());
    }

    /**
     * 根据任务类型、版本信息  获取数据源类型
     * @param jobType
     * @param version
     * @return
     */
    public static Integer getDataSourceTypeByJobType(Integer jobType, String version) {
        Integer dataSourceType = mappingMap.get(jobType);
        if (DataSourceType.HIVE.getVal().equals(dataSourceType)) {
            if (HiveVersion.HIVE_1x.getVersion().equals(version)) {
                return DataSourceType.HIVE1X.getVal();
            } else if (HiveVersion.HIVE_3x.getVersion().equals(version)) {
                return DataSourceType.HIVE3X.getVal();
            } else {
                return DataSourceType.HIVE.getVal();
            }
        }
        if (dataSourceType == null) {
            throw new RdosDefineException("无法通过jobType获取dataSourceType");
        }
        return dataSourceType;
    }
}
