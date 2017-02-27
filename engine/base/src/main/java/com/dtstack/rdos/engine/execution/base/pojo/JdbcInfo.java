package com.dtstack.rdos.engine.execution.base.pojo;

/**
 * 数据库连接配置相关
 * Date: 2017/2/27
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class JdbcInfo {

    protected String driverName;

    protected String dbURL;

    protected String userName;

    protected String password;

    /**batch 刷新间隔 ms, 默认是1s*/
    protected int batchInterval = 1000;

    protected int[] types;

    protected String tableName;

    protected String sql;
}
