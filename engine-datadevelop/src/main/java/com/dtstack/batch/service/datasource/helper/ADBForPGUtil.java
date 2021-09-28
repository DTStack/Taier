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

package com.dtstack.batch.service.datasource.helper;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.sync.format.ColumnType;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ADB For PG 工具类
 *
 * date: 2021/6/10 11:35 上午
 * author: zhaiyue
 */
public class ADBForPGUtil {

    private static String tableTemplate = "create table %s ( %s );";

    /**
     *  源表中的字段 转换成ADB For PG 字段
     *
     * @apiNote ：目前字段类型统一转换成String，
     *
     * @param dbColumns 源表中的字段
     * @return
     */
    public static List convertADBForPGWriterColumns(List<JSONObject> dbColumns) {
        List<JSONObject> hiveColumns = new ArrayList<>(dbColumns.size());
        for (int i = 0; i < dbColumns.size(); i++) {
            JSONObject hiveColumn = new JSONObject(4);
            JSONObject dbColumn = dbColumns.get(i);
            hiveColumn.put("key", dbColumn.getString("key"));
            hiveColumn.put("index", i);
            hiveColumn.put("comment", dbColumn.getString("comment"));
            hiveColumn.put("type", ColumnType.VARCHAR.name());
            hiveColumns.add(hiveColumn);
        }
        return hiveColumns;
    }

    /**
     * 构建建表语句
     *
     * @param schema
     * @param tableName
     * @param columns
     * @return
     */
     public static String generalCreateSql(String schema, String tableName, List<JSONObject> columns) {
        String tablePath = buildTablePath(schema, tableName);
        String columnInfo = buildColumnInfo(columns);
        return SqlFormatUtil.formatSql(String.format(tableTemplate, tablePath, columnInfo));
    }

    /**
     * 组装表信息（指定schema时拼接schema名）
     *
     * @param schema
     * @param tableName
     * @return
     */
    private static String buildTablePath(String schema, String tableName){
        if (StringUtils.isBlank(schema)){
            return tableName;
        }
        return schema + "." + tableName;
    }

    /**
     * 组装字段信息
     *
     * @param columns
     * @return
     */
    private static String buildColumnInfo(List<JSONObject> columns){
        List<String> columnInfoList = columns.stream().map(columnObj ->
                columnObj.getString("key") + " " + ColumnType.VARCHAR.name() + "(255)"
        ).collect(Collectors.toList());
        return StringUtils.join(columnInfoList, ",");
    }

}
