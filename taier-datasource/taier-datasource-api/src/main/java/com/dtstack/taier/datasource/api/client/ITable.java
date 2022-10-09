package com.dtstack.taier.datasource.api.client;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>提供表相关操作接口</>
 *
 * @author ：wangchuan
 * date：Created in 10:17 上午 2020/11/12
 * company: www.dtstack.com
 */
public interface ITable extends Client {

    /**
     * 执行sql查询
     *
     * @param source 数据源信息
     * @param sql    查询sql
     * @return 查询结果
     */
    List<Map<String, Object>> executeQuery(ISourceDTO source, String sql);

    /**
     * 执行sql，不需要结果
     *
     * @param source 数据源信息
     * @param sql    查询sql
     * @return 执行成功与否
     */
    Boolean executeSqlWithoutResultSet(ISourceDTO source, String sql);

    /**
     * 获取所有分区，格式同sql返回值，不错额外处理，如：pt1=name1/pt2=name2/pt3=name3
     * 非分区表返回null
     *
     * @param source    数据源信息
     * @param tableName 表名
     * @return 所有分区
     */
    List<String> showPartitions(ISourceDTO source, String tableName);

    /**
     * 删除表，成功返回true，失败返回false
     *
     * @param source    数据源信息
     * @param tableName 表名
     * @return 删除结果
     */
    Boolean dropTable(ISourceDTO source, String tableName);

    /**
     * 重命名表，成功返回true，失败返回false
     *
     * @param source       数据源信息
     * @param oldTableName 旧表名
     * @param newTableName 新表名
     * @return 重命名结果
     */
    Boolean renameTable(ISourceDTO source, String oldTableName, String newTableName);

    /**
     * 修改表参数
     *
     * @param source    数据源信息
     * @param tableName 表名
     * @param params    修改的参数，map集合
     * @return 修改结果
     */
    Boolean alterTableParams(ISourceDTO source, String tableName, Map<String, String> params);

    /**
     * 获取表占用存储
     *
     * @param source    数据源信息
     * @param schema    schema信息
     * @param tableName 表名
     * @return 占用存储
     */
    Long getTableSize(ISourceDTO source, String schema, String tableName);

    /**
     * 判断表是否是分区表
     *
     * @param source    数据源信息
     * @param schema    schema名称
     * @param tableName 表名
     * @return 是否是分区表
     */
    @Deprecated
    Boolean isView(ISourceDTO source, String schema, String tableName);
}
