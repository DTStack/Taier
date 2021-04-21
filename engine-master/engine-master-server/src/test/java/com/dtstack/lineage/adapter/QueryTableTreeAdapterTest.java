package com.dtstack.lineage.adapter;

import com.dtstack.sqlparser.common.client.domain.QueryTableTree;
import com.dtstack.sqlparser.common.client.domain.SelectColumn;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ZYD
 * Date: 2021/4/20 15:49
 * Description: 查询表树转换器
 * @since 1.0.0
 */
public class QueryTableTreeAdapterTest {




    @Test
    public void test(){

        QueryTableTree queryTableTree = new QueryTableTree();
        List<SelectColumn> columns = new ArrayList<>();
        SelectColumn selectColumn = new SelectColumn();
        columns.add(selectColumn);
        queryTableTree.setColumns(columns);
        com.dtstack.engine.api.vo.lineage.QueryTableTree tableTree = QueryTableTreeAdapter.sqlQueryTableTree2ApiQueryTableTree(queryTableTree);
        Assert.assertNotNull(tableTree);
    }

}
