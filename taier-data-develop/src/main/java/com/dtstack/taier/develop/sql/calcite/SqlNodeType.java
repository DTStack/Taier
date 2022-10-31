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

package com.dtstack.taier.develop.sql.calcite;

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
