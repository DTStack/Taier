package com.dtstack.taier.develop.flink.sql.sink;

import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;
import com.dtstack.taier.develop.flink.sql.sink.param.MySqlSinkParamEnum;

/**
 * mysql 结果表
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public class MySqlSinkTable extends AbstractSinkTable {

    @Override
    public ISqlParamEnum[] getSqlParamEnums() {
        return MySqlSinkParamEnum.values();
    }

    @Override
    protected String getTypeBeforeVersion112() {
        return "mysql";
    }

    @Override
    protected String getTypeVersion112() {
        return "mysql-x";
    }
}
