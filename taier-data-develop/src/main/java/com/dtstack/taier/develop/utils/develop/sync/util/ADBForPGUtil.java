package com.dtstack.taier.develop.utils.develop.sync.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.util.SqlFormatUtil;
import com.dtstack.taier.develop.utils.develop.sync.format.ColumnType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ADBForPGUtil {

    private static String TABLE_TEMPLATE = "create table %s ( %s ); %s";

    // 表备注 举例：COMMENT ON TABLE schema.mytable IS 'This is my table.';
    private static String ADD_TABLE_COMMENT = "COMMENT ON TABLE %s.%s IS '%s';";

    // 字段注释 举例：COMMENT ON COLUMN mytable.a IS '字段a';
    private static String ADD_COLUMN_COMMENT = "COMMENT ON COLUMN %s.%s.%s IS '%s';";

    /**
     * 源表中的字段 转换成ADB For PG 字段
     *
     * @param dbColumns 源表中的字段
     * @return
     * @apiNote ：目前字段类型统一转换成String，
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
                columnObj.getString("key") + " " + ColumnType.VARCHAR.name() + "(255)"
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
        tableAndColumnComments.add(String.format(ADD_TABLE_COMMENT, schema, tableName,
                StringUtils.isBlank(tableComment) ? "" : tableComment));
        columns.forEach(writerColumn -> {
            tableAndColumnComments.add(String.format(ADD_COLUMN_COMMENT, schema, tableName,writerColumn.get("key"),
                    StringUtils.isBlank(writerColumn.getString("comment")) ? "" : writerColumn.getString("comment")));
        });
        return StringUtils.join(tableAndColumnComments, "\n");
    }

}
