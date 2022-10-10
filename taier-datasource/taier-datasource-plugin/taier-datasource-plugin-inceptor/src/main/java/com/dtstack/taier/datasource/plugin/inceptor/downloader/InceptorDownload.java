package com.dtstack.taier.datasource.plugin.inceptor.downloader;

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.Column;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * inceptor 表数据下载器
 *
 * @author ：wangchuan
 * date：Created in 上午9:53 2021/4/6
 * company: www.dtstack.com
 */
@Slf4j
public class InceptorDownload implements IDownloader {

    /**
     * 执行的 sql
     */
    private final String sql;

    /**
     * 数据库连接
     */
    private final Connection connection;

    /**
     * 连接操作对象
     */
    private Statement statement;

    /**
     * 当前读取页码
     */
    private int pageNum = 1;

    /**
     * 每页数据条数
     */
    private int pageSize = 100;

    /**
     * 总页数
     */
    private int pageAll;

    /**
     * 总数据条数
     */
    private int totalLine = 0;

    /**
     * 列字段
     */
    private List<Column> columnNames;

    /**
     * 表字段条数
     */
    private int columnCount;

    /**
     * 是否已经调用 configure 方法
     */
    private boolean isConfigure = false;

    public InceptorDownload(Connection connection, String sql, Integer pageSize) {
        this.connection = connection;
        this.sql = sql;
        if (Objects.nonNull(pageSize)) {
            this.pageSize = pageSize;
        }
    }

    @Override
    public boolean configure() throws Exception {
        if (BooleanUtils.isTrue(isConfigure)) {
            // 避免 configure 方法重复调用
            return true;
        }
        if (null == connection || StringUtils.isEmpty(sql)) {
            throw new SourceException("inceptor connection acquisition failed or execution SQL is empty");
        }
        statement = connection.createStatement();

        String countSQL = String.format("SELECT COUNT(*) FROM (%s) temp", sql);
        try (ResultSet resultSet = statement.executeQuery(countSQL);) {
            while (resultSet.next()) {
                // 获取总行数
                totalLine = resultSet.getInt(1);
            }
        }
        // 获取列信息
        String showColumns = String.format("SELECT * FROM (%s) t LIMIT 1", sql);
        try (ResultSet resultSet = statement.executeQuery(showColumns)) {
            columnNames = new ArrayList<>();
            columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Column column = new Column();
                column.setName(resultSet.getMetaData().getColumnName(i));
                column.setType(resultSet.getMetaData().getColumnTypeName(i));
                column.setIndex(i);
                columnNames.add(column);
            }
            //获取总页数
            pageAll = (int) Math.ceil(totalLine / (double) pageSize);
        }
        isConfigure = true;
        log.info("inceptorDownload: executed SQL:{}, totalLine:{}, columnNames:{}, columnCount{}", sql, totalLine, columnNames, columnCount);
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
        String limitSQL = String.format("SELECT * FROM (%s) t limit %s,%s", sql, pageSize * (pageNum - 1), pageSize);
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
            throw new SourceException("read inceptor message exception : " + e.getMessage(), e);
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
