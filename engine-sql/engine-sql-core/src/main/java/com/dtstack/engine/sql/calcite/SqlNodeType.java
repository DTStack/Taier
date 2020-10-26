package com.dtstack.engine.sql.calcite;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/26 10:17
 * @Description:
 */
public enum SqlNodeType {

    /**
     * set @@name = 0
     */
    SQL_SET(),
    /**
     * alter语法
     */
    SQL_ALTER(),
    /**
     * create
     */
    SQL_CREATE(),
    /**
     * drop
     */
    SQL_DROP(),
    /**
     * select 语法。
     * order by语法
     * limit语法 略微有点小差别
     */
    SQL_QUERY(),
    /**
     * calcite并不能解析
     */
    SQL_EXPLAIN(),
    /**
     * libra不支持
     */
    SQL_DESCRIBE(),
    /**
     * insert 语法有两种：
     * 1.常规insert
     * 2.insert on key conflict update
     */
    SQL_INSERT(),
    /**
     * delete
     */
    SQL_DELETE(),
    /**
     * update
     */
    SQL_UPDATE(),
    /**
     * libra不支持
     */
    SQL_MERGE(),
    /**
     * 存储过程调用
     */
    SQL_PROCEDURE_CALL(),
}
