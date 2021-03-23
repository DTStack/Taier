package com.dtstack.lineage.enums;

import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.sql.client.enums.ETableType;
import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

/**
 * @author chener
 * @Classname SourceType2TableType
 * @Description TODO
 * @Date 2020/11/4 10:16
 * @Created chener@dtstack.com
 */
public enum SourceType2TableType {
    HIVE(ETableType.HIVE, Sets.newHashSet(DataSourceType.HIVE1X,DataSourceType.HIVE,DataSourceType.Spark)),
    LIBRA(ETableType.LIBRA,Sets.newHashSet(DataSourceType.LIBRA)),
    TIDB(ETableType.TIDB,Sets.newHashSet(DataSourceType.TiDB)),
    ORACLE(ETableType.ORACLE,Sets.newHashSet(DataSourceType.Oracle)),
    GREENPLUM(ETableType.GREENPLUM,Sets.newHashSet(DataSourceType.GREENPLUM6)),
    IMPALA(ETableType.IMPALA,Sets.newHashSet(DataSourceType.IMPALA)),
    ;

    private ETableType tableType;
    private Set<DataSourceType> dataSourceTypeSet;

    public ETableType getTableType() {
        return tableType;
    }

    public Set<DataSourceType> getDataSourceTypeSet() {
        return dataSourceTypeSet;
    }

    SourceType2TableType(ETableType tableType, Set<DataSourceType> dataSourceTypeSet) {
        this.tableType = tableType;
        this.dataSourceTypeSet = dataSourceTypeSet;
    }

    public static SourceType2TableType getByTableType(Integer tableType){
        if (Objects.isNull(tableType)){
            return null;
        }
        for (SourceType2TableType mulType:values()){
            if (mulType.getTableType().getType() == tableType){
                return mulType;
            }
        }
        return null;
    }

    public static SourceType2TableType getBySourceType(DataSourceType sourceType){
        if (Objects.isNull(sourceType)){
            return null;
        }
        for (SourceType2TableType mulType:values()){
            if (mulType.getDataSourceTypeSet().contains(sourceType)){
                return mulType;
            }
        }
        return null;
    }

    public static SourceType2TableType getBySourceType(Integer sourceType){
        if (Objects.isNull(sourceType)){
            return null;
        }
        for (SourceType2TableType mulType:values()){
            DataSourceType byType = DataSourceType.getSourceType(sourceType);
            if (mulType.getDataSourceTypeSet().contains(byType)){
                return mulType;
            }
        }
        return null;
    }
}
