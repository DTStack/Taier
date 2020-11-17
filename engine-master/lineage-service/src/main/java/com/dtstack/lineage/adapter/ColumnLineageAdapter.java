package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.pojo.lineage.ColumnLineage;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;

import java.util.Map;

/**
 * @author chener
 * @Classname ColumnLineageAdapter
 * @Description
 * @Date 2020/11/5 10:53
 * @Created chener@dtstack.com
 */
public class ColumnLineageAdapter {
    public static ColumnLineage sqlColumnLineage2ApiColumnLineage(com.dtstack.engine.sql.ColumnLineage sqlColumnLineage) {
        ColumnLineage apiColumnLineage = new ColumnLineage();
        apiColumnLineage.setFromDb(sqlColumnLineage.getFromDb());
        apiColumnLineage.setFromSchema(sqlColumnLineage.getFromDb());
        apiColumnLineage.setFromColumn(sqlColumnLineage.getFromColumn());
        apiColumnLineage.setFromTable(sqlColumnLineage.getFromTable());
        apiColumnLineage.setToDb(sqlColumnLineage.getToDb());
        apiColumnLineage.setToSchema(sqlColumnLineage.getToDb());
        apiColumnLineage.setToTable(sqlColumnLineage.getToTable());
        apiColumnLineage.setToColumn(sqlColumnLineage.getToColumn());
        apiColumnLineage.setFromTempTable(sqlColumnLineage.isFromTempTable());
        apiColumnLineage.setToTempTable(sqlColumnLineage.isToTempTable());
        return apiColumnLineage;
    }

    public static LineageColumnColumn sqlColumnLineage2ColumnColumn(com.dtstack.engine.sql.ColumnLineage sqlColumnLineage, Integer appType, Map<String, LineageDataSetInfo> tableMap) {
        String keyPref = "%s.%s";
        LineageColumnColumn lineageColumnColumn = new LineageColumnColumn();
        LineageDataSetInfo inputTable = tableMap.get(String.format(keyPref, sqlColumnLineage.getFromDb(), sqlColumnLineage.getFromTable()));
        LineageDataSetInfo resultTable = tableMap.get(String.format(keyPref, sqlColumnLineage.getToDb(), sqlColumnLineage.getToTable()));
        lineageColumnColumn.setAppType(appType);
        lineageColumnColumn.setInputTableId(inputTable.getId());
        lineageColumnColumn.setInputColumnName(lineageColumnColumn.getInputColumnName());
        lineageColumnColumn.setInputTableKey(inputTable.getTableKey());
        lineageColumnColumn.setResultTableId(resultTable.getId());
        lineageColumnColumn.setResultColumnName(lineageColumnColumn.getResultColumnName());
        lineageColumnColumn.setResultTableKey(lineageColumnColumn.getResultTableKey());
        lineageColumnColumn.setDtUicTenantId(inputTable.getDtUicTenantId());
        return lineageColumnColumn;
    }

    public static LineageColumnColumnVO columnColumn2ColumnColumnVO(LineageColumnColumn columnColumn, LineageDataSetInfo inputTable, LineageDataSetInfo resultTable, LineageDataSource inputSource, LineageDataSource resultSource){
        LineageColumnColumnVO columnColumnVO = new LineageColumnColumnVO();
        columnColumnVO.setAppType(columnColumn.getAppType());
        columnColumnVO.setDtUicTenantId(columnColumn.getDtUicTenantId());
        columnColumnVO.setInputColumnName(columnColumn.getInputColumnName());
        LineageTableVO inputTableVO = TableAdapter.dataSetInfo2LineageTableVO(inputSource,inputTable);
        LineageTableVO resultTableVO = TableAdapter.dataSetInfo2LineageTableVO(resultSource,resultTable);
        columnColumnVO.setInputTableInfo(inputTableVO);
        columnColumnVO.setResultTableInfo(resultTableVO);
        columnColumnVO.setResultColumnName(columnColumn.getResultColumnName());
        return columnColumnVO;
    }
}
