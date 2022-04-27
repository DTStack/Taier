package com.dtstack.taier.develop.enums.develop;

/**
 * @author zhiChen
 * https://dtstack.yuque.com/rd-center/sm6war/lvgh5o
 * @date 2021/9/17 10:16
 */
public enum TaskDirtyDataManageParamEnum {


    /**
     * 输出类型1.log2.jdbc
     */
    OUTPUT_TYPE("flinkx.dirty-data.output-type", TaskDirtyOutPutTypeEnum.LOG.getValue()),

    /**
     * 日志打印频率
     */
    LOG_PRINT_INTERVAL("flinkx.dirty-data.log.print-interval", "1"),

    /**
     * 脏数据最大值  total limit
     */
    MAX_ROWS("flinkx.dirty-data.max-rows", "-1"),

    /**
     * 失败条数 error limit
     */
    MAX_COLLECT_FAILED_ROWS("flinkx.dirty-data.max-collect-failed-rows", "-1"),

    /**
     * url
     */
    URL("flinkx.dirty-data.jdbc.url", null),

    /**
     * 用户名
     */
    USERNAME("flinkx.dirty-data.jdbc.username", null),

    /**
     * 密码
     */
    PASSWORD("flinkx.dirty-data.jdbc.password", null),

    /**
     * 数据库 可写在url后面
     */
    DATABASE("flinkx.dirty-data.jdbc.database", null),

    /**
     * 表
     */
    TABLE("flinkx.dirty-data.jdbc.table", null),

    /**
     * 脏数据建表 sql
     */
    CREATE_TABLE_SQL("CREATE TABLE IF NOT EXISTS %s (  job_id        VARCHAR(32)                               NOT NULL COMMENT 'Flink Job Id',\n" +
            "    job_name      VARCHAR(255)                              NOT NULL COMMENT 'Flink Job Name',\n" +
            "    operator_name VARCHAR(255)                              NOT NULL COMMENT '出现异常数据的算子名，包含表名',\n" +
            "    dirty_data    TEXT                                      NOT NULL COMMENT '脏数据的异常数据',\n" +
            "    error_message TEXT COMMENT '脏数据中异常原因',\n" +
            "    field_name    VARCHAR(255) COMMENT '脏数据中异常字段名',\n" +
            "    create_time   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '脏数据出现的时间点'\n" +
            ")  COMMENT '存储脏数据';\n" +
            "CREATE INDEX idx_job_id ON %s (job_id);\n" +
            "CREATE INDEX idx_operator_name ON %s (operator_name);\n" +
            "CREATE INDEX idx_create_time ON %s (create_time);", null);

    private final String param;
    private final String defaultValue;

    TaskDirtyDataManageParamEnum(String param, String defaultValue) {
        this.param = param;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getParam() {
        return param;
    }
}
