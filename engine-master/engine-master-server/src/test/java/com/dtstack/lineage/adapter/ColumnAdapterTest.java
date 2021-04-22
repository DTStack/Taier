package com.dtstack.lineage.adapter;

import com.dtstack.sqlparser.common.client.domain.Column;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/20 15:18
 * Description: 字段结果转换类
 * @since 1.0.0
 */
public class ColumnAdapterTest {

    @Test
    public void testSqlColumn2ApiColumn(){

        Column column = new Column();
        column.setName("id");
        com.dtstack.engine.api.pojo.lineage.Column apiColumn = ColumnAdapter.sqlColumn2ApiColumn(column);
        Assert.assertEquals("id",column.getName());
    }

}
