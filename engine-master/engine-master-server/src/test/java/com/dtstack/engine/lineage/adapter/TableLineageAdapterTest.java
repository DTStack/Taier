package com.dtstack.engine.lineage.adapter;


import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.sqlparser.common.client.domain.TableLineage;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: ZYD
 * Date: 2021/4/20 16:34
 * Description: 单测
 * @since 1.0.0
 */
public class TableLineageAdapterTest {

    @Test
    public void testSqlTableLineage2ApiTableLineage(){

        TableLineage tableLineage = new TableLineage();

        com.dtstack.engine.api.pojo.lineage.TableLineage lineage = TableLineageAdapter.sqlTableLineage2ApiTableLineage(tableLineage);
        Assert.assertNotNull(lineage);
    }


    @Test
    public void testSqlTableLineage2DbTableLineage(){

        TableLineage tableLineage = new TableLineage();
        tableLineage.setFromDb("dev");
        tableLineage.setFromTable("t1");
        tableLineage.setToDb("dev");
        tableLineage.setToTable("t2");
        Map<String, LineageDataSetInfo> map = new HashMap<>();
        LineageDataSetInfo dataSetInfo1 = new LineageDataSetInfo();
        map.put("dev.t1",dataSetInfo1);
        LineageDataSetInfo dataSetInfo2 = new LineageDataSetInfo();
        map.put("dev.t2",dataSetInfo2);
        LineageTableTable lineageTableTable = TableLineageAdapter.sqlTableLineage2DbTableLineage(tableLineage, map, LineageOriginType.SQL_PARSE);
        Assert.assertNotNull(lineageTableTable);
    }

    @Test
    public void testTableTable2TableTableVO(){

        LineageTableTable tableTable = new LineageTableTable();
        tableTable.setLineageSource(0);
        LineageDataSetInfo inputTable = new LineageDataSetInfo();
        LineageDataSetInfo resultTable = new LineageDataSetInfo();
        DsServiceInfoDTO inputSource = new DsServiceInfoDTO();
        DsServiceInfoDTO resultSource = new DsServiceInfoDTO();
        LineageTableTableVO tableTableVO = TableLineageAdapter.tableTable2TableTableVO(tableTable, inputTable, resultTable, inputSource, resultSource);
        Assert.assertNotNull(tableTableVO);
    }

}
