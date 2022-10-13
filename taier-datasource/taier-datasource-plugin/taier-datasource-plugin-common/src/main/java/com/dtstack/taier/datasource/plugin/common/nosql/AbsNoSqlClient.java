package com.dtstack.taier.datasource.plugin.common.nosql;

import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.Database;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.TableInfo;
import com.dtstack.taier.datasource.api.dto.WriteFileDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 非关系型数据库抽象类，用于屏蔽一些不需要实现的方法
 *
 * @author ：wangchuan
 * date：Created in 下午4:19 2021/3/23
 * company: www.dtstack.com
 */
public abstract class AbsNoSqlClient implements IClient {

    @Override
    public Connection getCon(ISourceDTO source) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<Map<String, Object>> executeQuery(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Map<String, List<Map<String, Object>>> executeMultiQuery(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Boolean executeBatchQuery(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Boolean executeSqlWithoutResultSet(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<String> getColumnClassInfo(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaDataWithSql(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public String getTableMetaComment(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, String sql, Integer pageSize) throws Exception {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public String getCreateTableSql(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Table getTable(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public String getCurrentDatabase(ISourceDTO source) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Boolean createDatabase(ISourceDTO source, String dbName, String comment) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Boolean isDatabaseExists(ISourceDTO source, String dbName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<String> getCatalogs(ISourceDTO source) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<String> getRootDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<ColumnMetaDTO> getFlinkColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO) {
        return getColumnMetaData(source, queryDTO);
    }

    @Override
    public List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public String getVersion(ISourceDTO source) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public List<String> listFileNames(ISourceDTO sourceDTO, String path, Boolean includeDir, Boolean recursive, Integer maxNum, String regexStr) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Database getDatabase(ISourceDTO sourceDTO, String dbName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }


    @Override
    public Integer executeUpdate(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public Boolean writeByFile(ISourceDTO sourceDTO, WriteFileDTO writeFileDTO) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    public TableInfo getTableInfo(ISourceDTO sourceDTO, String tableName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }
}
