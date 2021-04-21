package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.vo.lineage.QueryTableTree;
import java.util.stream.Collectors;

/**
 * @Author: ZYD
 * Date: 2021/4/2 10:43
 * Description: 查询树对象转换器
 * @since 1.0.0
 */
public class QueryTableTreeAdapter {


    public static QueryTableTree sqlQueryTableTree2ApiQueryTableTree(com.dtstack.sqlparser.common.client.domain.QueryTableTree tableTree){

        if(null == tableTree){
            return null;
        }
        QueryTableTree tree = new QueryTableTree();
        tree.setAlias(tableTree.getAlias());
        tree.setCetQuery(tableTree.isCetQuery());
        tree.setColumns(tableTree.getColumns().stream().map(SelectColumnAdapter::SqlSelectColumn2ApiSelectColumn).collect(Collectors.toList()));
        tree.setName(tableTree.getName());
        tree.setChildren(tableTree.getChildren().stream().map(QueryTableTreeAdapter::sqlQueryTableTree2ApiQueryTableTree).collect(Collectors.toList()));
        tree.setParent(QueryTableTreeAdapter.sqlQueryTableTree2ApiQueryTableTree(tableTree.getParent()));
        return tree;

    }

}
