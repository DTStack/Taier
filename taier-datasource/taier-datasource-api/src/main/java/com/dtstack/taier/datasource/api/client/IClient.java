package com.dtstack.taier.datasource.api.client;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.Database;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.TableInfo;
import com.dtstack.taier.datasource.api.dto.WriteFileDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * 通用 IClient 客户端接口
 *
 * @author ：wangchuan
 * date：Created in 下午3:38 2021/12/17
 * company: www.dtstack.com
 */
public interface IClient extends Client {

    /**
     * 获取数据源连接
     *
     * @param source 数据源信息
     * @return 数据源连接
     */
    Connection getCon(ISourceDTO source);

    /**
     * 校验数据源连通性
     *
     * @param source 数据源信息
     * @return 是否连通
     */
    Boolean testCon(ISourceDTO source);

    /**
     * 执行 sql
     * <p>sql: 需要执行的 sql {@link SqlQueryDTO#setSql}
     *
     * @param source   数据源信息
     * @param queryDTO sql 执行条件
     * @return sql 执行结果
     */
    List<Map<String, Object>> executeQuery(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 执行查询,engine plugin迁移至此
     *
     * @param source   数据源信息
     * @param queryDTO 必填项 sql
     * @return 执行结果
     */
    Boolean executeBatchQuery(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 多条 sql 一起执行, 返回多结果集
     * <p>sqlMultiDTOList: 多条 sql 集合 {@link SqlQueryDTO#setSqlMultiDTOList}
     *
     * @param source   数据源连接信息
     * @param queryDTO 执行条件
     * @return 结果集
     */
    Map<String, List<Map<String, Object>>> executeMultiQuery(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 执行 dml
     *
     * @param source   数据源信息
     * @param queryDTO 执行条件
     * @return 修改条数
     */
    Integer executeUpdate(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 执行 sql，无需结果集
     * <p>sql: 需要执行的 sql {@link SqlQueryDTO#setSql}
     *
     * @param source   数据源信息
     * @param queryDTO 执行条件
     * @return 是否执行成功
     */
    Boolean executeSqlWithoutResultSet(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取全部表或者指定 schema 下的表
     * <p>view: 是否过滤试图, 默认不过滤{@link SqlQueryDTO#setView}}
     * <p>schema: 查询指定 schema 下面的表{@link SqlQueryDTO#setSchema}}
     *
     * @param source   数据源信息
     * @param queryDTO 查询条件
     * @return 表集合
     */
    List<String> getTableList(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取全部表或者指定 schema 下的表
     * <p>view: 是否查询视图, 默认不查询{@link SqlQueryDTO#setView}}
     * <p>schema: 查询指定 schema 下面的表{@link SqlQueryDTO#setSchema}}
     *
     * @param source   数据源信息
     * @param queryDTO 查询条件
     * @return 表集合
     */
    List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 返回字段 Java 类的标准名称, 字段名若不填则默认全部
     * <p>tableName: 指定表名 {@link SqlQueryDTO#setTableName}
     * <p>columns: 指定查询的字段名 {@link SqlQueryDTO#setColumns}
     *
     * @param source   数据源信息
     * @param queryDTO 必填项 表名
     * @return java 类标准名称集合
     */
    List<String> getColumnClassInfo(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取表字段属性, 字段名若不填则默认全部
     * <p>tableName: 指定表名 {@link SqlQueryDTO#setTableName}
     * <p>columns: 指定查询的字段名 {@link SqlQueryDTO#setColumns}
     * <p>filterPartitionColumns: 是否过滤分区字段 {@link SqlQueryDTO#setFilterPartitionColumns}
     *
     * @param source   数据源信息
     * @param queryDTO 必填项 表名
     * @return java 类标准名称集合
     */
    List<ColumnMetaDTO> getColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 根据自定义查询 sql 获取结果字段属性
     * <p>sql: 设置执行的 sql {@link SqlQueryDTO#setSql}
     * <p>filterPartitionColumns: 是否过滤分区字段 {@link SqlQueryDTO#setFilterPartitionColumns}
     *
     * @param source   数据源信息
     * @param queryDTO 查询条件
     * @return 结果字段属性
     */
    List<ColumnMetaDTO> getColumnMetaDataWithSql(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取 flinkSql 需要的字段名称(做了部分转换, 为实时计算中特殊使用)
     * <p>tableName: 指定表名 {@link SqlQueryDTO#setTableName}
     * <p>columns: 指定查询的字段名 {@link SqlQueryDTO#setColumns}
     *
     * @param source   数据源信息
     * @param queryDTO 查询条件
     * @return b表字段属性
     */
    List<ColumnMetaDTO> getFlinkColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取 flinkSql 需要的字段名称(做了部分转换, 为实时计算中特殊使用)
     * <p>tableName: 指定表名 {@link SqlQueryDTO#setTableName}
     * <p>columns: 指定查询的字段名 {@link SqlQueryDTO#setColumns}
     *
     * @param source   数据源信息
     * @param queryDTO 查询条件
     * @return 结果字段属性
     */
    String getTableMetaComment(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取预览数据
     *
     * @param source   数据源信息
     * @param queryDTO 预览条件
     * @return 预览数据
     */
    List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取对应的downloader
     *
     * @param source   数据源信息
     * @param queryDTO sql 执行条件
     * @return 数据下载 downloader
     * @throws Exception
     */
    IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception;

    /**
     * 根据执行 sql 下载数据
     *
     * @param source   数据源信息
     * @param sql      执行 sql
     * @param pageSize 每页的数量
     * @return 数据下载 downloader
     * @throws Exception 异常
     */
    IDownloader getDownloader(ISourceDTO source, String sql, Integer pageSize) throws Exception;

    /**
     * 获取所有的 DB
     *
     * @param source   数据源信息
     * @param queryDTO 查询信息
     * @return 所有的DB
     */
    List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取所有的db，此方法目的为获取所有的 database，为了解决一些遗留问题。例如 oracle 12 后支持 cdb
     * 模式，此时 oracle 可以包含多个 pdb，每个 pdb 下面可以有多个 schema，但是getAllDatabases 返回
     * 的是 schema 列表
     *
     * @param source   数据源信息
     * @param queryDTO 查询信息
     * @return db 列表
     * @see IClient#getAllDatabases 该方法对于有 database 概念的数据源返回的是 database，否则返回 schema
     */
    List<String> getRootDatabases(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取建表语句
     *
     * @param source   数据源信息
     * @param queryDTO 查询信息
     * @return 建表sql
     */
    String getCreateTableSql(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取分区字段
     *
     * @param source   数据源信息
     * @param queryDTO 查询信息
     * @return 分区字段
     */
    List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取表信息
     *
     * @param source   数据源信息
     * @param queryDTO 查询信息
     * @return 表信息
     */
    Table getTable(ISourceDTO source, SqlQueryDTO queryDTO);

    /**
     * 获取当前使用的数据库
     *
     * @param source 数据源信息
     * @return 当前使用的数据库
     */
    String getCurrentDatabase(ISourceDTO source);

    /**
     * 创建数据库
     *
     * @param source  数据源信息
     * @param dbName  需要创建的数据库
     * @param comment 库注释信息
     * @return 创建结果
     */
    Boolean createDatabase(ISourceDTO source, String dbName, String comment);

    /**
     * 判断数据库是否存在
     *
     * @param source 数据源信息
     * @param dbName 数据库名称
     * @return 判断结果
     */
    Boolean isDatabaseExists(ISourceDTO source, String dbName);

    /**
     * 判断该数据库中是否存在该表
     *
     * @param source    数据源信息
     * @param tableName 表名
     * @param dbName    库名
     * @return 判断结果
     */
    Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName);

    /**
     * 获取数据源/数据库目录列表，目前presto使用，后续pgSql等可以实现该方法用于获取所有库
     *
     * @param source 数据源信息
     * @return 数据源目录
     */
    List<String> getCatalogs(ISourceDTO source);

    /**
     * 获取当前数据源的版本
     *
     * @param source 数据源信息
     * @return 数据源版本
     */
    String getVersion(ISourceDTO source);

    /**
     * 列出指定路径下的文件
     *
     * @param sourceDTO  数据源信息
     * @param path       路径
     * @param includeDir 是否包含文件夹
     * @param recursive  是否递归
     * @param maxNum     最大返回条数
     * @param regexStr   正则匹配
     * @return 文件集合
     */
    List<String> listFileNames(ISourceDTO sourceDTO, String path, Boolean includeDir, Boolean recursive, Integer maxNum, String regexStr);

    /**
     * 获取数据库信息
     *
     * @param sourceDTO 数据源信息
     * @param dbName    数据库名称
     * @return 数据库详细信息
     */
    Database getDatabase(ISourceDTO sourceDTO, String dbName);

    /**
     * 读取本地文件并批量写入数据库
     *
     * @param sourceDTO    数据源信息
     * @param writeFileDTO 写入条件参数
     * @return 是否写入成功
     */
    Boolean writeByFile(ISourceDTO sourceDTO, WriteFileDTO writeFileDTO);

    /**
     * 获取 table 信息
     *
     * @param sourceDTO 数据源信息
     * @param tableName 表名
     * @return table 信息
     */
    TableInfo getTableInfo(ISourceDTO sourceDTO, String tableName);
}
