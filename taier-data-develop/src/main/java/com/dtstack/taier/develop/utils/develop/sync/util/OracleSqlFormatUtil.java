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

package com.dtstack.taier.develop.utils.develop.sync.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.SqlFormatUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OracleSqlFormatUtil {

    private static String TABLE_TEMPLATE = "create table %s ( %s ); %s";

    // 表备注 举例：COMMENT ON TABLE schema.mytable IS 'This is my table.';
    private static String ADD_TABLE_COMMENT = "COMMENT ON TABLE %s.%s IS '%s';";

    private static String ADD_TABLE_COMMENT_NO_SHMEA = "COMMENT ON TABLE %s IS '%s';";

    // 字段注释 举例：COMMENT ON COLUMN mytable.a IS '字段a';
    private static String ADD_COLUMN_COMMENT = "COMMENT ON COLUMN %s.%s.%s IS '%s';";

    private static String ADD_COLUMN_COMMENT_NO_SHMEA = "COMMENT ON COLUMN %s.%s IS '%s';";


    /**
     * 构建建表语句
     *
     * @param schema
     * @param tableName
     * @param columns
     * @return
     */
    public static String generalCreateSql(String schema, String tableName, List<JSONObject> columns, String tableComment) {
        String tablePath = buildTablePath(schema, tableName);
        String columnInfo = buildColumnInfo(columns);
        String tableAndColumnComment = createTableAndColumnComment(columns, tableName, tableComment, schema);
        return SqlFormatUtil.formatSql(String.format(TABLE_TEMPLATE, tablePath, columnInfo, tableAndColumnComment));
    }

    /**
     * 组装表信息（指定schema时拼接schema名）
     *
     * @param schema
     * @param tableName
     * @return
     */
    private static String buildTablePath(String schema, String tableName) {
        if (StringUtils.isBlank(schema)) {
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
    private static String buildColumnInfo(List<JSONObject> columns) {
        List<String> columnInfoList = columns.stream().map(columnObj ->
                columnObj.getString("key") + " " + columnObj.getString("type")
        ).collect(Collectors.toList());
        return StringUtils.join(columnInfoList, ",");
    }

    /**
     * 拼接表注释
     * 拼接字段注释
     *
     * @param columns 字段信息
     * @return
     */
    private static String createTableAndColumnComment(List<JSONObject> columns, String tableName, String tableComment, String schema) {
        List<String> tableAndColumnComments = new ArrayList<>();
        if (StringUtils.isBlank(schema)) {
            tableAndColumnComments.add(
                    String.format(ADD_TABLE_COMMENT_NO_SHMEA, tableName, StringUtils.isBlank(tableComment) ? "" : tableComment));
            columns.forEach(writerColumn -> {
                tableAndColumnComments.add(String.format(ADD_COLUMN_COMMENT_NO_SHMEA, tableName, writerColumn.get("key"),
                        StringUtils.isBlank(writerColumn.getString("comment")) ? "" : writerColumn.getString("comment")));
            });
        } else {
            tableAndColumnComments.add(
                    String.format(ADD_TABLE_COMMENT, schema, tableName, StringUtils.isBlank(tableComment) ? "" : tableComment));
            columns.forEach(writerColumn -> {
                tableAndColumnComments.add(String.format(ADD_COLUMN_COMMENT, schema, tableName, writerColumn.get("key"),
                        StringUtils.isBlank(writerColumn.getString("comment")) ? "" : writerColumn.getString("comment")));
            });
        }
        return StringUtils.join(tableAndColumnComments, "\n");
    }

}
