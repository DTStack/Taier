package com.dtstack.batch.engine.rdbms.service.impl;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.utils.TableOperateUtils;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.ITable;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TableServiceImpl implements ITableService {
    @Override
    public String showCreateTable(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        return client.getCreateTableSql(iSourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
    }

    @Override
    public void createDatabase(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String comment) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, "");
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        client.createDatabase(iSourceDTO, dbName, comment);
        log.info("集群创建数据库操作，dtUicTenantId:{}，dtUicUserId:{}，sourceType:{}，dbName:{}", dtUicTenantId, dtUicUserId, iSourceDTO.getSourceType(), dbName);
    }

    @Override
    public Boolean isDatabaseExist(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        return client.isDatabaseExists(iSourceDTO, dbName);
    }

    @Override
    public Boolean isTableExistInDatabase(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        return client.isTableExistsInDatabase(iSourceDTO, tableName, dbName);
    }

    @Override
    public Boolean isPartitionTable(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        List<ColumnMetaDTO> partitionColumn = client.getPartitionColumn(iSourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
        return CollectionUtils.isNotEmpty(partitionColumn);
    }

    @Override
    public Map<String, List<Column>> getTablesColumns(Long dtUicTenantId, Long dtUicUserId, ETableType tableType, List<Table> tables) {
        Map<String, List<Column>> map = new HashMap<>();
        for (Table table : tables) {
            try {
                if (StringUtils.isNotBlank(table.getDb())) {
                    ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, table.getDb());
                    IClient iClient = ClientCache.getClient(iSourceDTO.getSourceType());
                    List<ColumnMetaDTO> columnMetaData = iClient.getColumnMetaData(iSourceDTO, SqlQueryDTO.builder().tableName(table.getName()).build());
                    List<Column> columns = TableOperateUtils.getColumns(table.getName(), columnMetaData);
                    map.put(String.format("%s.%s", table.getDb(), table.getName()), columns);
                }
            }catch (Exception e){
                log.error("获取表信息失败", e);
            }

        }
        return map;
    }

    @Override
    public List<Column> getColumns(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        List<ColumnMetaDTO> columnMetaData = client.getColumnMetaData(iSourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
        return TableOperateUtils.getColumns(tableName, columnMetaData);
    }

    @Override
    public List<Column> getPartitionColumns(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        IClient iClient = ClientCache.getClient(iSourceDTO.getSourceType());
        List<ColumnMetaDTO> columnMetaData = iClient.getPartitionColumn(iSourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
        return TableOperateUtils.getColumns(tableName, columnMetaData);
    }

    @Override
    public List<String> showPartitions(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String table) {
        Boolean partitionTable = isPartitionTable(dtUicTenantId, dtUicUserId, dbName, tableType, table);
        if (!partitionTable){
            return Lists.newArrayList();
        }
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        ITable iTable = ClientCache.getTable(iSourceDTO.getSourceType());
        return iTable.showPartitions(iSourceDTO, table);
    }

    @Override
    public void alterTableParams(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName, Map<String, String> params) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        ITable iTable = ClientCache.getTable(iSourceDTO.getSourceType());
        iTable.alterTableParams(iSourceDTO, tableName, params);
        log.info("集群改变表属性，dtUicTenantId:{}，dtUicUserId:{}，sourceType:{}，dbName:{}，tableName:{}，params:{}", dtUicTenantId, dtUicUserId, iSourceDTO.getSourceType(), dbName, tableName, params);
    }

    @Override
    public void dropTable(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        ITable iTable = ClientCache.getTable(iSourceDTO.getSourceType());
        iTable.dropTable(iSourceDTO, tableName);
        log.info("集群删除表，dtUicTenantId:{}，dtUicUserId:{}，sourceType:{}，dbName:{}，tableName:{}", dtUicTenantId, dtUicUserId, iSourceDTO.getSourceType(), dbName, tableName);
    }

    @Override
    public void renameTable(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName, String newTableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        ITable iTable = ClientCache.getTable(iSourceDTO.getSourceType());
        iTable.renameTable(iSourceDTO, tableName, newTableName);
        log.info("集群改变表名称，dtUicTenantId:{}，dtUicUserId:{}，sourceType:{}，dbName:{}，tableName:{}，newTableName:{}", dtUicTenantId, dtUicUserId, iSourceDTO.getSourceType(), dbName, tableName, newTableName);
    }

    @Override
    public Table getTableInfo(Long dtUicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, dbName);
        IClient iClient = ClientCache.getClient(iSourceDTO.getSourceType());
        com.dtstack.dtcenter.loader.dto.Table iClientTable = iClient.getTable(iSourceDTO, SqlQueryDTO.builder().tableName(tableName).build());
        Table table = new Table();
        BeanUtils.copyProperties(iClientTable, table);
        List<ColumnMetaDTO> columnMetaDTOS = iClientTable.getColumns();
        table.setColumns(TableOperateUtils.getWithoutPartitionColumns(tableName, columnMetaDTOS));
        table.setPartitions(TableOperateUtils.getPartitionColumns(tableName, columnMetaDTOS));
        // 如果存在分区字段，则该表为分区表
        table.setPartitionTable(CollectionUtils.isNotEmpty(table.getPartitions()));
        return table;
    }

    @Override
    public Boolean isPartitionExist(Long dtUicTenantId, Long dtUicUserId, String partitionVal, String db, ETableType tableType, String tableName) {
        List<String> partitionVOS = showPartitions(dtUicTenantId, dtUicUserId, db, tableType, tableName);
        return partitionVOS.stream().filter(vo -> vo.equalsIgnoreCase(partitionVal)).findFirst().isPresent();
    }

    @Override
    public Boolean isView(Long dtUicTenantId, Long dtUicUserId, String db, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, db);
        ITable table = ClientCache.getTable(iSourceDTO.getSourceType());
        return table.isView(iSourceDTO, db, tableName);
    }

    @Override
    public Long getTableSize(Long dtUicTenantId, Long dtUicUserId, String db, ETableType tableType, String tableName) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtUicTenantId, dtUicUserId, tableType, db);
        ITable table = ClientCache.getTable(iSourceDTO.getSourceType());
        return table.getTableSize(iSourceDTO, db, tableName);
    }
}
