package com.dtstack.taier.develop.flink.sql.side;


import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;
import com.dtstack.taier.develop.flink.sql.side.param.MySqlSideParamEnum;
import lombok.Data;

/**
 * mysql 维表
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
@Data
public class MySqlSideTable extends AbstractSideTable {

    @Override
    public ISqlParamEnum[] getSqlParamEnums() {
        return MySqlSideParamEnum.values();
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
