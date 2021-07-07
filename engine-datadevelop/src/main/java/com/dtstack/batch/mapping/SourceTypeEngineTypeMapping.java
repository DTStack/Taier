package com.dtstack.batch.mapping;

import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

/**
 * Reason:
 * Date: 2019/5/30
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class SourceTypeEngineTypeMapping {

    private final static Map<Integer, MultiEngineType> REF_MAP = Maps.newHashMap();

    static {
        REF_MAP.put(DataSourceType.HIVE.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(DataSourceType.HIVE1X.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(DataSourceType.HIVE3X.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(DataSourceType.SparkThrift2_1.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(DataSourceType.IMPALA.getVal(), MultiEngineType.HADOOP);
        REF_MAP.put(DataSourceType.LIBRA.getVal(), MultiEngineType.LIBRA);
        REF_MAP.put(DataSourceType.TiDB.getVal(), MultiEngineType.TIDB);
        REF_MAP.put(DataSourceType.Oracle.getVal(), MultiEngineType.ORACLE);
        REF_MAP.put(DataSourceType.GREENPLUM6.getVal(), MultiEngineType.GREENPLUM);
        REF_MAP.put(DataSourceType.ADB_FOR_PG.getVal(), MultiEngineType.ANALYTICDB_FOR_PG);
    }

    public static MultiEngineType getEngineTypeBySourceType(Integer sourceType) {
        //默认走hadoop
        return Optional.ofNullable(REF_MAP.get(sourceType)).orElse(MultiEngineType.HADOOP);
    }
}
