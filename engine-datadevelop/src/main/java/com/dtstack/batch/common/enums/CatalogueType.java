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

package com.dtstack.batch.common.enums;

/**
 * @author jiangbo
 * @time 2017/12/20
 */
public enum CatalogueType {

    /**
     * 任务开发
     */
    TASK_DEVELOP("TaskDevelop"),

    /**
     * 资源管理
     */
    RESOURCE_MANAGER("ResourceManager"),

    /**
     * 脚本管理
     */
    SCRIPT_MANAGER("ScriptManager"),

    /**
     * 自定义函数
     */
    CUSTOM_FUNCTION("CustomFunction"),

    /**
     * 系统函数
     */
    SYSTEM_FUNCTION("SystemFunction"),

    /**
     * 生产函数
     */
    PROCEDURE_FUNCTION("ProcedureFunction"),

    /**
     * gp数据源自定义函数
     */
    GREENPLUM_CUSTOM_FUNCTION("GreenPlumCustomFunction"),

    /**
     * libra系统函数
     */
    LIBRASQL_FUNCTION("LibraSQLFunction"),

    /**
     * spark系统函数
     */
    SPARKSQL_FUNCTION("SparkSQLFunction"),

    /**
     * tidb系统函数
     */
    TIDBSQL_FUNCTION("TiDBSQLFunction"),

    /**
     * oracle系统函数
     */
    ORACLE_FUNCTION("OracleSQLFunction"),

    /**
     * GP系统函数
     */
    GREENPLUM_FUNCTION("GreenPlumSQLFunction"),

    /**
     * 函数管理
     */
    FUNCTION_MANAGER("FunctionManager");


    private String type;

    CatalogueType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
