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

package com.dtstack.taier.develop.flink.sql.core;

/**
 * flink sql 中用到的常量
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public interface SqlConstant {

    /**
     * 数据源类型对应 key
     */
    String TYPE_KEY = "type";

    /**
     * connect 对应 key
     */
    String CONNECTOR_KEY = "connector";

    /**
     * 自定义参数中的 key
     */
    String CUSTOM_PARAM_KEY = "key";

    /**
     * 自定义参数中的 value
     */
    String CUSTOM_PARAM_VALUE = "value";

    /**
     * 映射表名
     */
    String TABLE_NAME_KEY = "tableName";

    /**
     * 表名&映射表名
     */
    String TABLE_KEY = "table";

    /**
     * 参数分隔符
     */
    String PARAM_SEPARATOR = ",";

    /**
     * 分区参数分隔符
     */
    String PARTITION_SEPARATOR = ";";

    /**
     * 字段名对应 key
     */
    String COLUMN_KEY = "column";

    /**
     * 字段集合对应 key
     */
    String COLUMNS_KEY = "columns";

    /**
     * 字段 - 类型分隔符
     */
    String COLUMN_SEPARATOR = " ";

    /**
     * 字段类型
     */
    String COLUMN_TYPE = "type";

    /**
     * 字段别名
     */
    String COLUMN_ALIAS = "targetCol";

    /**
     * 字段别名分割使用 as
     */
    String COLUMN_ALIAS_SEPARATOR = "as";

    /**
     * 并行度信息
     */
    String PARALLELISM = "parallelism";

    /**
     * 用户自定义参数 key
     */
    String CUSTOM_PARAMS = "customParams";

    /**
     * properties 前缀
     */
    String PROPERTIES_PREFIX = "properties.";

    /**
     * other
     */
    String OTHER = "other";

    /**
     * hdfs-site
     */
    String HDFS_SITE = "hdfs-site.xml";

    /**
     * core-site
     */
    String CORE_SITE = "core-site.xml";

    /**
     * hive-site
     */
    String HIVE_SITE = "hive-site.xml";

    interface SideTable {

        /**
         * 主键信息
         */
        String PRIMARY_KEY = "primaryKey";

        /**
         * 缓存策略 KEY [NONE、LRU、ALL]，默认为 LRU
         */
        String CACHE = "cache";
    }
}
