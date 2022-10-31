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

package com.dtstack.taier.develop.sql;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * sql解析器接口
 *
 * @author jiangbo
 * @date 2019/5/18
 */
public interface SqlParserImpl {

    /**
     * 解析给定的sql
     *
     * @param originSql 输入的sql
     * @param currentDb 当前的数据库
     * @param tableColumnsMap sql中表的字段信息
     * @return 解析的结果封装到 ParseResult 类里
     * @throws Exception 解析过程中会抛出异常，需要使用自己处理
     */
    ParseResult parseSql(String originSql, String currentDb, Map<String, List<Column>> tableColumnsMap) throws Exception;

    /**
     * 解析出sql语句中包含的表，一般用于带有查询语句的sql
     * @param currentDb
     * @param sql
     * @return
     * @throws Exception
     */
    List<Table> parseTables(String currentDb, String sql) throws Exception;


    /**
     * 解析 给定sql中的表级血缘
     * @param originSql 输入的sql
     * @param currentDb 默认db
     * @return
     * @throws Exception
     */
    ParseResult parseTableLineage(String originSql, String currentDb)throws Exception;

    /**
     * 解析生命周期和类目，填充到结果类中
     *
     * @param parseResult
     */
    void parseLifecycleAndCatalogue(ParseResult parseResult);

    /**
     * 解析sql中的自定义函数
     *
     * @param sql
     * @return
     */
    Set<String> parseFunction(String sql);

}
