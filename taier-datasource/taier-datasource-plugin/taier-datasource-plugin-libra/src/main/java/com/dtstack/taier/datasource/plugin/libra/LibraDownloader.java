package com.dtstack.taier.datasource.plugin.libra;

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SqlFormatUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Libra download
 *
 * @author ：wangchuan
 * date：Created in 下午2:42 2021/6/10
 * company: www.dtstack.com
 */
@Slf4j
public class LibraDownloader implements IDownloader {

    private final List<String> columnNames = Lists.newArrayList();

    private final Connection connection;

    private final String sql;

    private final String schema;

    private Statement statement;

    private int pageNum = 1;

    private final int pageSize = 100;

    private int pageAll;

    private int columnCount;

    // 切换 schema 命令
    private static final String SWITCH_SCHEMA = "set search_path to %s";

    public LibraDownloader(Connection connection, String sql, String schema) {
        this.connection = connection;
        this.sql = SqlFormatUtil.formatSql(sql);
        this.schema = schema;
    }

    @Override
    public boolean configure() throws Exception {
        if (Objects.isNull(connection) || StringUtils.isEmpty(sql)) {
            throw new SourceException("connection is close or sql is null");
        }
        int totalLine = 0;
        statement = connection.createStatement();
        if (StringUtils.isNotBlank(schema)) {
            try {
                // 切换 schema
                statement.execute(String.format(SWITCH_SCHEMA, schema));
            } catch (Exception e) {
                log.error("switch schema to {} error", schema);
            }
        }

        String countSQL = String.format("SELECT COUNT(1) FROM (%s) temp", sql);
        String showColumns = String.format("SELECT * FROM (%s) t limit 1", sql);
        ResultSet totalResultSet = null;
        ResultSet columnsResultSet = null;
        try {
            totalResultSet = statement.executeQuery(countSQL);
            while (totalResultSet.next()) {
                //获取总行数
                totalLine = totalResultSet.getInt(1);
            }
            columnsResultSet = statement.executeQuery(showColumns);
            //获取列信息
            columnCount = columnsResultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(columnsResultSet.getMetaData().getColumnLabel(i));
            }
            // 获取总页数
            pageAll = (int) Math.ceil(totalLine / (double) pageSize);
        } catch (Exception e) {
            throw new SourceException("build Libra downloader message exception : " + e.getMessage(), e);
        } finally {
            if (totalResultSet != null) {
                totalResultSet.close();
            }
            if (columnsResultSet != null) {
                columnsResultSet.close();
            }
        }
        return true;
    }

    @Override
    public List<String> getMetaInfo() {
        return columnNames;
    }

    @Override
    public List<List<String>> readNext() {
        //分页查询，一次一百条
        String limitSQL = String.format("SELECT * FROM (%s) t limit %s offset %s", sql, pageSize, pageSize * (pageNum - 1));
        List<List<String>> pageTemp = new ArrayList<>(100);

        try (ResultSet resultSet = statement.executeQuery(limitSQL)) {
            while (resultSet.next()) {
                List<String> columns = new ArrayList<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(resultSet.getString(i));
                }
                pageTemp.add(columns);
            }
        } catch (Exception e) {
            throw new SourceException("read Libra message exception : " + e.getMessage(), e);
        }

        pageNum++;
        return pageTemp;
    }

    @Override
    public boolean reachedEnd() {
        return pageAll < pageNum;
    }

    @Override
    public boolean close() throws Exception {
        DBUtil.closeDBResources(null, statement, connection);
        return true;
    }
}
