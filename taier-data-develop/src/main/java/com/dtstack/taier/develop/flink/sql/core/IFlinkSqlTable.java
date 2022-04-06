package com.dtstack.taier.develop.flink.sql.core;

import java.io.Serializable;

public interface IFlinkSqlTable extends Serializable {

    /**
     * 获取建表 sql
     *
     * @return get flink sql create table sql
     */
    String getCreateSql();
}
