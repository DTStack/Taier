package com.dtstack.batch.common.enums;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:44 2020/12/26
 * @Description：表类型
 */
public enum ETableType {
    HIVE(1, "Hive", DataSourceType.HIVE),
    LIBRA(2, "LibrA", DataSourceType.LIBRA),
    TIDB(3, "TiDB", DataSourceType.TiDB),
    ORACLE(4, "Oracle", DataSourceType.Oracle),
    GREENPLUM(5, "GreenPlum", DataSourceType.GREENPLUM6),
    IMPALA(6, "IMPALA", DataSourceType.IMPALA),
    ADB_FOR_PG(7, "AnalyticDB for PostgreSQL", DataSourceType.ADB_FOR_PG);

    int type;
    String content;
    DataSourceType dataSourceType;

    final static Set<Integer> dataSourceTypeToHiveSet = Sets.newHashSet(DataSourceType.SparkThrift2_1.getVal(),
            DataSourceType.Spark.getVal(), DataSourceType.HIVE1X.getVal(), DataSourceType.HIVE.getVal(), DataSourceType.HIVE3X.getVal(),
            DataSourceType.IMPALA.getVal());

    ETableType(int type, String content, DataSourceType dataSourceType) {
        this.content = content;
        this.type = type;
        this.dataSourceType = dataSourceType;
    }

    /**
     * 获取类型
     *
     * @return
     */
    public int getType() {
        return this.type;
    }

    /**
     * 获取描述
     *
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * 获取数据源类型
     *
     * @return
     */
    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    /**
     * 根据表类型获取枚举值
     *
     * @param tableType
     * @return
     */
    public static ETableType getTableType(int tableType) {
        for (ETableType eType : ETableType.values()) {
            if (eType.getType() == tableType) {
                return eType;
            }
        }

        throw new DtCenterDefException(String.format("not support tableType's ETableType:%d", tableType));
    }

    /**
     * 根据数据源类型获取枚举值
     *
     * @param datasourceType
     * @return
     */
    public static ETableType getDatasourceType(Integer datasourceType) {
        if (dataSourceTypeToHiveSet.contains(datasourceType)){
            return ETableType.HIVE;
        }
        for (ETableType etype : ETableType.values()) {
            if (etype.getDataSourceType().getVal().equals(datasourceType)) {
                return etype;
            }
        }
        throw new DtCenterDefException(String.format("not support datasourceType's ETableType : %d", datasourceType));
    }
}
