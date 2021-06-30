package com.dtstack.engine.lineage.adapter;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.sqlparser.common.client.domain.Table;
import com.dtstack.sqlparser.common.client.enums.TableOperateEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/20 16:08
 * Description: 单测
 * @since 1.0.0
 */
public class TableAdapterTest {

    @Test
    public void testSqlTable2ApiTable(){

        Table table = new Table();
        table.setOperate(TableOperateEnum.ALTER);
        com.dtstack.engine.api.pojo.lineage.Table t = TableAdapter.sqlTable2ApiTable(table);
        Assert.assertNotNull(t);
    }

    @Test
    public void testDataSetInfo2LineageTableVO(){

        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        DsServiceInfoDTO dataSource = new DsServiceInfoDTO();
        LineageTableVO tableVO = TableAdapter.dataSetInfo2LineageTableVO(dataSource,dataSetInfo);
        Assert.assertNotNull(tableVO);
    }
}
