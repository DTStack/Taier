package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.vo.lineage.SqlType;

import java.util.Objects;

/**
 * @author chener
 * @Classname SqlTypeAdapter
 * @Description TODO
 * @Date 2020/11/4 11:15
 * @Created chener@dtstack.com
 */
public class SqlTypeAdapter {
    public static com.dtstack.engine.api.vo.lineage.SqlType sqlType2ApiSqlType(com.dtstack.engine.sql.SqlType sqlType){
        if (Objects.isNull(sqlType)){
            return null;
        }
        return SqlType.valueOf(sqlType.name());
    }
}
