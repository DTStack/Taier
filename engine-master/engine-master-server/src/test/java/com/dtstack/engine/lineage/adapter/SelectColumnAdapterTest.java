package com.dtstack.engine.lineage.adapter;

import com.dtstack.sqlparser.common.client.domain.SelectColumn;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/20 15:59
 * Description: 单测
 * @since 1.0.0
 */
public class SelectColumnAdapterTest {


    @Test
    public void test(){
        SelectColumn selectColumn = new SelectColumn();
        com.dtstack.engine.api.vo.lineage.SelectColumn sc = SelectColumnAdapter.SqlSelectColumn2ApiSelectColumn(selectColumn);
        Assert.assertNotNull(sc);
    }
}
