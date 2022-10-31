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

package com.dtstack.taier.datasource.plugin.dorisrestful.request;

public interface HttpAPI {

    /**
     * 获取所有的库 cluster
     */
    String ALL_DATABASE = "/api/meta/namespaces/%s/databases";

    /**
     * 获取库下的表 cluster schema tableName
     */
    String ALL_TABLES = "/api/meta/namespaces/%s/databases/%s:%s/tables";

    /**
     * 获取元数据 cluster cluster schema
     */
    String COLUMN_METADATA = "/api/meta/namespaces/%s/databases/%s/tables/%s/schema";

    /**
     * 数据预览 cluster schema
     */
    String QUERY_DATA = "/api/query/%s/%s";

    /**
     * 获取指定数据库中，指定表的表结构信息，也可用来判断表是否存在
     */
    String QUERY_TABLE_STRUCTURE = "/api/meta/namespaces/%s/databases/%s:%s/tables/%s/schema";
}