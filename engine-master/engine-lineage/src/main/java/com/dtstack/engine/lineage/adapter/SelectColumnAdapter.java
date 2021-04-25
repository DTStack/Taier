package com.dtstack.engine.lineage.adapter;

import com.dtstack.engine.api.vo.lineage.SelectColumn;

/**
 * @Author: ZYD
 * Date: 2021/4/2 10:51
 * Description: 查询中的字段对象转换器
 * @since 1.0.0
 */
public class SelectColumnAdapter {

    public static SelectColumn SqlSelectColumn2ApiSelectColumn(com.dtstack.sqlparser.common.client.domain.SelectColumn selectColumn){

        if(null == selectColumn){
            return null;
        }
        SelectColumn column = new SelectColumn();
        column.setAlias(selectColumn.getAlias());
        column.setName(selectColumn.getName());

        return column;
    }


}
