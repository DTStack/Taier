package com.dtstack.taier.datasource.plugin.iceberg.client;

import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.IcebergSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.iceberg.pool.IcebergHiveCatalogManager;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.iceberg.PartitionField;
import org.apache.iceberg.Table;
import org.apache.iceberg.catalog.Namespace;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.hive.HiveCatalog;
import org.apache.iceberg.types.Types;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Iceberg 连接客户端
 *
 * @author ：wangchuan
 * date：Created in 下午9:37 2021/11/9
 * company: www.dtstack.com
 */
@Slf4j
public class IcebergClient extends AbsRdbmsClient {

    /**
     * iceberg hive catalog 管理类
     */
    private static final IcebergHiveCatalogManager CATALOG_MANAGER = IcebergHiveCatalogManager.getInstance();

    @Override
    protected ConnFactory getConnFactory() {
        log.warn("The method [getConnFactory] is not supported by this data source...");
        return null;
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.ICEBERG;
    }

    @Override
    public Boolean testCon(ISourceDTO sourceDTO) {
        try {
            HiveCatalog hiveCatalog = CATALOG_MANAGER.getHiveCatalog(sourceDTO);
            hiveCatalog.listNamespaces();
            return true;
        } catch (Exception e) {
            throw new SourceException(String.format("test conn error, msg: %s", e.getMessage()), e);
        }
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        IcebergSourceDTO icebergSourceDTO = (IcebergSourceDTO) sourceDTO;
        HiveCatalog hiveCatalog = CATALOG_MANAGER.getHiveCatalog(sourceDTO);
        String schema = getSchema(icebergSourceDTO, queryDTO);
        // schema 不为空则查询当前 schema 下的表
        if (StringUtils.isNotBlank(schema)) {
            Namespace namespace = Namespace.of(schema);
            return SearchUtil.handleSearchAndLimit(hiveCatalog.listTables(namespace).stream().map(TableIdentifier::name).collect(Collectors.toList()), queryDTO);
        }
        // schema 不为空查询所有数据库下面的表, 先查询出所有 namespace
        List<Namespace> namespaces = hiveCatalog.listNamespaces();
        List<String> tables = Lists.newArrayList();
        for (Namespace namespace : namespaces) {
            tables.addAll(hiveCatalog.listTables(namespace).stream().map(t -> namespace.level(0) + "." + t.name()).collect(Collectors.toList()));
        }
        return SearchUtil.handleSearchAndLimit(tables, queryDTO);
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        return getTableList(source, queryDTO);
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO source, SqlQueryDTO queryDTO) {
        HiveCatalog hiveCatalog = CATALOG_MANAGER.getHiveCatalog(source);
        String schema = getSchema(source, queryDTO);
        AssertUtils.notBlank(queryDTO.getTableName(), "tableName cannot be null");
        Table table;
        if (StringUtils.isNotBlank(schema)) {
            table = hiveCatalog.loadTable(TableIdentifier.of(Namespace.of(schema), queryDTO.getTableName()));
        } else {
            table = hiveCatalog.loadTable(TableIdentifier.of(queryDTO.getTableName()));
        }
        List<ColumnMetaDTO> columnMetaDTOS = Lists.newArrayList();
        // 获取普通字段
        for (Types.NestedField column : table.schema().columns()) {
            ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
            columnMetaDTO.setKey(column.name());
            columnMetaDTO.setType(column.type().toString());
            columnMetaDTO.setComment(column.doc());
            columnMetaDTOS.add(columnMetaDTO);
        }
        // 获取分区字段
        List<String> partitionColumn = Lists.newArrayList();
        for (PartitionField field : table.spec().fields()) {
            partitionColumn.add(field.name());
        }
        if (CollectionUtils.isNotEmpty(partitionColumn)) {
            columnMetaDTOS.forEach(column -> {
                if (partitionColumn.contains(column.getKey())) {
                    column.setPart(true);
                }
            });
        }
        return columnMetaDTOS;
    }

    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        HiveCatalog hiveCatalog = CATALOG_MANAGER.getHiveCatalog(source);
        return hiveCatalog
                .listNamespaces()
                .stream()
                .map(namespace -> namespace.level(0))
                .collect(Collectors.toList());
    }
}
