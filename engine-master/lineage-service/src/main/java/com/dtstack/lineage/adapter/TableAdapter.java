package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;
import org.apache.commons.collections.CollectionUtils;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chener
 * @Classname TableAdapter
 * @Description 血缘解析核心包中的table转换为api中的table
 * @Date 2020/11/4 10:45
 * @Created chener@dtstack.com
 */
public class TableAdapter {
    public static com.dtstack.engine.api.pojo.lineage.Table sqlTable2ApiTable(com.dtstack.engine.sql.Table sqlTable) {
        if (Objects.isNull(sqlTable)) {
            return null;
        }
        com.dtstack.engine.api.pojo.lineage.Table apiTable = new com.dtstack.engine.api.pojo.lineage.Table(sqlTable.getDb(), sqlTable.getName());
        if (CollectionUtils.isNotEmpty(sqlTable.getColumns())){
            apiTable.setColumns(sqlTable.getColumns().stream().map(ColumnAdapter::sqlColumn2ApiColumn).collect(Collectors.toList()));
        }
        apiTable.setComment(sqlTable.getComment());
        apiTable.setDb(sqlTable.getDb());
        apiTable.setAlias(sqlTable.getAlias());
        apiTable.setDelim(sqlTable.getDelim());
        apiTable.setName(sqlTable.getName());
        apiTable.setLifecycle(sqlTable.getLifecycle());
        if (CollectionUtils.isNotEmpty(sqlTable.getPartitions())){
            apiTable.setPartitions(sqlTable.getPartitions().stream().map(ColumnAdapter::sqlColumn2ApiColumn).collect(Collectors.toList()));
        }
        apiTable.setPartitionTable(sqlTable.isPartitionTable());
        apiTable.setPath(sqlTable.getPath());
        apiTable.setStoreType(sqlTable.getStoreType());
        apiTable.setTemp(sqlTable.isTemp());
        apiTable.setView(sqlTable.isView());
        return apiTable;
    }

    public static LineageTableVO dataSetInfo2LineageTableVO(LineageDataSource dataSource, LineageDataSetInfo dataSetInfo){
        LineageTableVO lineageTableVO = new LineageTableVO();
        LineageDataSourceVO dataSourceVO = DataSourceAdapter.dataSource2DataSourceVO(dataSource);
        lineageTableVO.setDataSourceVO(dataSourceVO);
        lineageTableVO.setTableId(dataSetInfo.getId());
        lineageTableVO.setTableName(dataSetInfo.getTableName());
        lineageTableVO.setDbName(dataSetInfo.getDbName());
        lineageTableVO.setSchemaName(dataSetInfo.getSchemaName());
        return lineageTableVO;
    }
}
