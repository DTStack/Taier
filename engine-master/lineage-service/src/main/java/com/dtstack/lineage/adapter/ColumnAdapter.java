package com.dtstack.lineage.adapter;


import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;

import java.util.List;

/**
 * @author chener
 * @Classname ColumnAdapter
 * @Description TODO
 * @Date 2020/11/4 10:53
 * @Created chener@dtstack.com
 */
public class ColumnAdapter {
    public static com.dtstack.engine.api.pojo.lineage.Column sqlColumn2ApiColumn(com.dtstack.engine.sql.Column sqlColumn){
        com.dtstack.engine.api.pojo.lineage.Column column = new com.dtstack.engine.api.pojo.lineage.Column();
        column.setAlias(sqlColumn.getAlias());
        column.setComment(sqlColumn.getComment());
        column.setIndex(sqlColumn.getIndex());
        column.setName(sqlColumn.getName());
        column.setTable(sqlColumn.getTable());
        column.setType(sqlColumn.getType());
        return column;
    }

    public static com.dtstack.engine.sql.Column apiColumn2SqlColumn(com.dtstack.engine.api.pojo.lineage.Column apiColumn){
        com.dtstack.engine.sql.Column sqlColumn = new com.dtstack.engine.sql.Column();
        sqlColumn.setAlias(apiColumn.getAlias());
        sqlColumn.setComment(apiColumn.getComment());
        sqlColumn.setIndex(apiColumn.getIndex());
        sqlColumn.setName(apiColumn.getName());
        sqlColumn.setTable(apiColumn.getTable());
        sqlColumn.setType(apiColumn.getType());
        return sqlColumn;
    }
}
