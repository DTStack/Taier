package com.dtstack.engine.lineage.adapter;

import com.dtstack.engine.lineage.vo.SqlType;

import java.util.Objects;

/**
 * @author chener
 * @Classname SqlTypeAdapter
 * @Description TODO
 * @Date 2020/11/4 11:15
 * @Created chener@dtstack.com
 */
public class SqlTypeAdapter {
    public static SqlType sqlType2ApiSqlType(com.dtstack.sqlparser.common.client.enums.SqlType sqlType){
        if (Objects.isNull(sqlType)){
            return null;
        }
        return SqlType.valueOf(sqlType.name());
    }
}
