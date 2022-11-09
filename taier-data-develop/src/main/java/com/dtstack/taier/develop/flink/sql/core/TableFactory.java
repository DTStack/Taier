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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.flink.sql.side.AbstractSideTable;
import com.dtstack.taier.develop.flink.sql.side.MySqlSideTable;
import com.dtstack.taier.develop.flink.sql.sink.AbstractSinkTable;
import com.dtstack.taier.develop.flink.sql.sink.ES7SinkTable;
import com.dtstack.taier.develop.flink.sql.sink.HbaseSinkTable;
import com.dtstack.taier.develop.flink.sql.sink.MySqlSinkTable;
import com.dtstack.taier.develop.flink.sql.source.AbstractSourceTable;
import com.dtstack.taier.develop.flink.sql.source.KafkaSourceTable;


/**
 * flink sql table 创建工厂
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public class TableFactory {

    /**
     * flinkSql 获取源表 sql
     *
     * @param dataSourceType   数据源类型
     * @param dataJson         数据源信息
     * @param paramJson        参数信息
     * @param componentVersion 组建版本
     * @return flinkSql 源表 sql
     */
    public static AbstractSourceTable getSourceTable(Integer dataSourceType, JSONObject dataJson, JSONObject paramJson, FlinkVersion componentVersion) {
        DataSourceType sourceType = DataSourceType.getSourceType(dataSourceType);
        AbstractSourceTable sourceTable;
        switch (sourceType) {
            case KAFKA:
            case KAFKA_2X:
                sourceTable = new KafkaSourceTable();
                break;
            default:
                throw new DtCenterDefException(String.format("数据源类型: %s 不支持作为维表", sourceType.getName()));
        }
        // 自动填充参数
        sourceTable.fillParam(dataJson, paramJson, componentVersion);
        return sourceTable;
    }

    /**
     * flinkSql 获取结果表 sql
     *
     * @param dataSourceType   数据源类型
     * @param dataJson         数据源信息
     * @param paramJson        参数信息
     * @param componentVersion 组建版本
     * @return flinkSql 结果表 sql
     */
    public static AbstractSinkTable getSinkTable(Integer dataSourceType, JSONObject dataJson, JSONObject paramJson, FlinkVersion componentVersion) {
        DataSourceType sourceType = DataSourceType.getSourceType(dataSourceType);
        AbstractSinkTable sinkTable;
        switch (sourceType) {
            case MySQL:
                sinkTable = new MySqlSinkTable();
                break;
            case ES7:
                sinkTable = new ES7SinkTable();
                break;
            case HBASE:
                sinkTable = new HbaseSinkTable();
                break;
            default:
                throw new DtCenterDefException(String.format("数据源类型: %s 不支持作为结果表", sourceType.getName()));
        }
        // 自动填充参数
        sinkTable.fillParam(dataJson, paramJson, componentVersion);
        return sinkTable;
    }

    /**
     * flinkSql 获取维表 sql
     *
     * @param dataSourceType   数据源类型
     * @param dataJson         数据源信息
     * @param paramJson        参数信息
     * @param componentVersion 组建版本
     * @return flinkSql 维表 sql
     */
    public static AbstractSideTable getSideTable(Integer dataSourceType, JSONObject dataJson, JSONObject paramJson, FlinkVersion componentVersion) {
        DataSourceType sourceType = DataSourceType.getSourceType(dataSourceType);
        AbstractSideTable sideTable;
        switch (sourceType) {
            case MySQL:
                sideTable = new MySqlSideTable();
                break;
            default:
                throw new DtCenterDefException(String.format("数据源类型: %s 不支持作为维表", sourceType.getName()));
        }
        // 自动填充参数
        sideTable.fillParam(dataJson, paramJson, componentVersion);
        return sideTable;
    }
}
