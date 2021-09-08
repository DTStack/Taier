package com.dtstack.batch.utils;

import com.dtstack.batch.domain.BatchTableColumn;
import com.dtstack.batch.mapstruct.vo.TableTransfer;
import com.dtstack.batch.vo.BatchTableColumnMetaInfoDTO;
import com.dtstack.batch.vo.BatchTableMetaInfoDTO;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.lineage.pojo.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对数据源插件化返回的table类 做一些转化操作
 */
public class TableOperateUtils {

    /**
     * 把table中的columns 转化一下 BatchTableColumn
     *
     * @param baseInfo
     * @return
     */
    public static List<BatchTableColumn> getTableColumns(Table baseInfo) {
        List<BatchTableColumn> tableColumn = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(baseInfo.getColumns())) {
            tableColumn = baseInfo.getColumns().stream().map(column -> {
                BatchTableColumn col = new BatchTableColumn();
                col.setColumnName(column.getName());
                col.setColumnType(column.getType().toLowerCase());
                col.setColumnIndex(column.getIndex());
                return col;
            }).collect(Collectors.toList());
        }
        return tableColumn;
    }

    /**
     * 更改表字段属性对象
     *
     * @param columnMetaData
     * @return
     */
    public static List<Column> getColumns(String tableName, List<ColumnMetaDTO> columnMetaData) {
        List<Column> columns = new ArrayList<>();
        for (ColumnMetaDTO dto : columnMetaData) {
            Column column = new Column();
            column.setComment(dto.getComment());
            column.setType(dto.getType());
            column.setName(dto.getKey());
            column.setAlias(dto.getKey());
            column.setTable(tableName);
            columns.add(column);
        }
        return columns;
    }

    /**
     * 只获取分区字段
     *
     * @param tableName
     * @param columnMetaData
     * @return
     */
    public static List<Column> getPartitionColumns(String tableName, List<ColumnMetaDTO> columnMetaData) {
        return getColumns(tableName, columnMetaData.stream().filter(columnMetaDTO -> columnMetaDTO.getPart()).collect(Collectors.toList()));
    }

    /**
     * 只获取非分区字段
     *
     * @param tableName
     * @param columnMetaData
     * @return
     */
    public static List<Column> getWithoutPartitionColumns(String tableName, List<ColumnMetaDTO> columnMetaData) {
        return getColumns(tableName, columnMetaData.stream().filter(columnMetaDTO -> !columnMetaDTO.getPart()).collect(Collectors.toList()));
    }

    /**
     * 得到表基本信息
     *
     * @param iSourceDTO
     * @param tableName
     * @return
     */
    public static BatchTableMetaInfoDTO getTableMetaInfo(ISourceDTO iSourceDTO, String tableName){
        return getTableMetaInfo(iSourceDTO, tableName, null);
    }

    /**
     * 得到表基本信息
     *
     * @param iSourceDTO
     * @param tableName
     * @param schema
     * @return
     */
    public static BatchTableMetaInfoDTO getTableMetaInfo(ISourceDTO iSourceDTO, String tableName, String schema){
        IClient iClient = ClientCache.getClient(iSourceDTO.getSourceType());
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().schema(schema).tableName(tableName).build();
        com.dtstack.dtcenter.loader.dto.Table table = iClient.getTable(iSourceDTO, sqlQueryDTO);
        BatchTableMetaInfoDTO tableMetaInfoDTO = TableTransfer.INSTANCE.tableToBatchTableMetaInfoDTO(table);
        fillColumnInfo(table, tableMetaInfoDTO);
        return tableMetaInfoDTO;
    }

    /**
     * 填充字段信息
     *
     * @param table
     * @param metaInfoDTO
     * @return
     */
    private static BatchTableMetaInfoDTO fillColumnInfo(com.dtstack.dtcenter.loader.dto.Table table, BatchTableMetaInfoDTO metaInfoDTO){
        List<ColumnMetaDTO> columns = table.getColumns();
        List<BatchTableColumnMetaInfoDTO> columnList = new ArrayList<>();
        List<BatchTableColumnMetaInfoDTO> partColumnList = new ArrayList<>();
        for (ColumnMetaDTO columnMetaDTO : columns) {
            BatchTableColumnMetaInfoDTO columnMetaInfoDTO = TableTransfer.INSTANCE.columnMetaDTOToBatchTableColumnMetaInfoDTO(columnMetaDTO);
            if (BooleanUtils.isTrue(columnMetaDTO.getPart())) {
                partColumnList.add(columnMetaInfoDTO);
            } else {
                columnList.add(columnMetaInfoDTO);
            }
        }
        metaInfoDTO.setColumns(columnList);
        metaInfoDTO.setPartColumns(partColumnList);
        return metaInfoDTO;
    }

}
