package com.dtstack.engine.sql.handler;

/**
 * @author yuebai
 * @date 2019-12-13
 */
public interface IUglySqlHandler {

    int CREATE_TEMP = 0b00000001;

    /**
     * 是否为临时表
     *
     * @return
     */
    boolean isTemp();

    /**
     * 格式化sql 为正常sql
     *
     * @param sql
     * @return
     */
    String parseUglySql(String sql);
}
