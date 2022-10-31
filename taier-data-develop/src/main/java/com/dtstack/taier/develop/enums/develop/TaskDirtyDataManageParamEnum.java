/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    OUTPUT_TYPE("chunjun.dirty-data.output-type", TaskDirtyOutPutTypeEnum.LOG.getValue()),

    /**
     * 日志打印频率
     */
    LOG_PRINT_INTERVAL("chunjun.dirty-data.log.print-interval", "1"),

    /**
     * 脏数据最大值  total limit
     */
    MAX_ROWS("chunjun.dirty-data.max-rows", "-1"),

    /**
     * 失败条数 error limit
     */
    MAX_COLLECT_FAILED_ROWS("chunjun.dirty-data.max-collect-failed-rows", "-1"),

    /**
     * url
     */
    URL("chunjun.dirty-data.jdbc.url", null),

    /**
     * 用户名
     */
    USERNAME("chunjun.dirty-data.jdbc.username", null),

    /**
     * 密码
     */
    PASSWORD("chunjun.dirty-data.jdbc.password", null),

    /**
     * 数据库 可写在url后面
     */
    DATABASE("chunjun.dirty-data.jdbc.database", null),

    /**
     * 表
     */
    TABLE("chunjun.dirty-data.jdbc.table", "chunjun_dirty_data"),

    /**
     * 脏数据建表 sql
     */
    CREATE_TABLE_SQL("CREATE TABLE IF NOT EXISTS chunjun_dirty_data (  job_id        VARCHAR(32)                               NOT NULL COMMENT 'Flink Job Id',\n" +
            "    job_name      VARCHAR(255)                              NOT NULL COMMENT 'Flink Job Name',\n" +
            "    operator_name VARCHAR(255)                              NOT NULL COMMENT '出现异常数据的算子名，包含表名',\n" +
            "    dirty_data    TEXT                                      NOT NULL COMMENT '脏数据的异常数据',\n" +
            "    error_message TEXT COMMENT '脏数据中异常原因',\n" +
            "    field_name    VARCHAR(255) COMMENT '脏数据中异常字段名',\n" +
            "    create_time   TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '脏数据出现的时间点'\n" +
            ")  COMMENT '存储脏数据';\n" +
            "CREATE INDEX idx_job_id ON chunjun_dirty_data (job_id);\n" +
            "CREATE INDEX idx_operator_name ON chunjun_dirty_data (operator_name);\n" +
            "CREATE INDEX idx_create_time ON chunjun_dirty_data (create_time);", null);

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
