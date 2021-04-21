package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.pojo.lineage.ColumnLineage;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ZYD
 * Date: 2021/4/20 15:28
 * Description: 字段血缘转换器
 * @since 1.0.0
 */
public class ColumnLineageAdapterTest {

    @Test
    public void testSqlColumnLineage2ApiColumnLineage(){

        com.dtstack.sqlparser.common.client.domain.ColumnLineage sqlCl = new com.dtstack.sqlparser.common.client.domain.ColumnLineage();
        sqlCl.setFromColumn("id");
        ColumnLineage columnLineage = ColumnLineageAdapter.sqlColumnLineage2ApiColumnLineage(sqlCl);
        Assert.assertEquals("id",columnLineage.getFromColumn());
    }


    @Test
    public void testSqlColumnLineage2ColumnColumn(){

        Map<String, LineageDataSetInfo> map = new HashMap<>();
        LineageDataSetInfo dataSetInfo1 = new LineageDataSetInfo();
        dataSetInfo1.setId(1L);
        LineageDataSetInfo dataSetInfo2 = new LineageDataSetInfo();
        dataSetInfo1.setId(2L);
        map.put("dev.t1",dataSetInfo1);
        map.put("dev.t2",dataSetInfo2);
        com.dtstack.sqlparser.common.client.domain.ColumnLineage columnLineage = new com.dtstack.sqlparser.common.client.domain.ColumnLineage();
        columnLineage.setFromDb("dev");
        columnLineage.setFromTable("t1");
        columnLineage.setToDb("dev");
        columnLineage.setToTable("t2");
        LineageColumnColumn columnColumn = ColumnLineageAdapter.sqlColumnLineage2ColumnColumn(columnLineage, 1, map);
        Assert.assertNotNull(columnColumn);

    }

    @Test
    public void testColumnColumn2ColumnColumnVO(){

        LineageColumnColumn lineageColumnColumn = new LineageColumnColumn();
        lineageColumnColumn.setLineageSource(0);
        LineageDataSetInfo inputTable = new LineageDataSetInfo();
        LineageDataSetInfo resultTable = new LineageDataSetInfo();
        LineageDataSource inputSource = new LineageDataSource();
        LineageDataSource resultSource = new LineageDataSource();
        LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(lineageColumnColumn, inputTable, resultTable, inputSource, resultSource);
        Assert.assertNotNull(columnColumnVO);
    }


}
