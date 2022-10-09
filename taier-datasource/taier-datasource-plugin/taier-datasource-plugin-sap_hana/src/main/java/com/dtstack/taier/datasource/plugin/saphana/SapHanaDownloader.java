package com.dtstack.taier.datasource.plugin.saphana;

import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SqlFormatUtil;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.Column;
import com.dtstack.taier.datasource.api.exception.SourceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * sap hana 下载器
 *
 * @author ：wangchuan
 * date：Created in 上午10:13 2021/12/30
 * company: www.dtstack.com
 */
public class SapHanaDownloader implements IDownloader {

    private int pageNum;

    private int pageAll;

    private List<Column> columnNames;

    private int totalLine;

    private Connection connection;

    private String sql;

    private String schema;

    private Statement statement;

    private int pageSize;

    private int columnCount;

    public SapHanaDownloader(Connection connection, String sql, String schema) {
        this.connection = connection;
        this.sql = SqlFormatUtil.formatSql(sql);
        this.schema = schema;
    }

    @Override
    public boolean configure() throws Exception {
        if (null == connection || StringUtils.isEmpty(sql)) {
            throw new SourceException("file is not exist");
        }
        totalLine = 0;
        pageSize = 100;
        pageNum = 1;
        statement = connection.createStatement();

        if (StringUtils.isNotEmpty(schema)) {
            //选择schema
            String useSchema = String.format("SET SCHEMA %s", schema);
            statement.execute(useSchema);
        }
        String countSQL = String.format("SELECT COUNT(*) FROM (%s) temp", sql);
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
            columnNames = new ArrayList<>();
            columnCount = columnsResultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Column column = new Column();
                column.setName(columnsResultSet.getMetaData().getColumnName(i));
                column.setType(columnsResultSet.getMetaData().getColumnTypeName(i));
                column.setIndex(i);
                columnNames.add(column);
            }
            //获取总页数
            pageAll = (int) Math.ceil(totalLine / (double) pageSize);
        } catch (Exception e) {
            throw new SourceException("build sap downloader message exception : " + e.getMessage(), e);
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
        if (CollectionUtils.isNotEmpty(columnNames)) {
            return columnNames.stream().map(Column::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
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
            throw new SourceException("read sap message exception : " + e.getMessage(), e);
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
