package com.dtstack.rdos.engine.execution.base.pojo;

import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.streaming.api.functions.sink.OutputFormatSinkFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * 数据库连接配置相关
 * Date: 2017/2/27
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public abstract class JdbcInfo {

    protected String driverName;

    protected String dbURL;

    protected String userName;

    protected String password;

    /**batch 刷新间隔 ms, 默认是1s*/
    protected int batchInterval = 1000;

    protected int[] types;

    protected String tableName;

    protected String sql;

    public RichSinkFunction createJdbc(){
        JDBCOutputFormat.JDBCOutputFormatBuilder jdbcFormatBuild = JDBCOutputFormat.buildJDBCOutputFormat();
        jdbcFormatBuild.setDBUrl(dbURL);
        jdbcFormatBuild.setDrivername(driverName);
        jdbcFormatBuild.setUsername(userName);
        jdbcFormatBuild.setPassword(password);
        jdbcFormatBuild.setQuery(sql);
        jdbcFormatBuild.setBatchInterval(batchInterval);//默认5s
        jdbcFormatBuild.setSqlTypes(types);
        JDBCOutputFormat outputFormat = jdbcFormatBuild.finish();

        OutputFormatSinkFunction outputFormatSinkFunc = new OutputFormatSinkFunction(outputFormat);
        return outputFormatSinkFunc;
    }
}
