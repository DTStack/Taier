package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;
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
        LineageDataSource dataSource = new LineageDataSource();
        dataSource.setAppType(1);
        LineageTableVO tableVO = TableAdapter.dataSetInfo2LineageTableVO(dataSource,dataSetInfo);
        Assert.assertNotNull(tableVO);
    }
}
