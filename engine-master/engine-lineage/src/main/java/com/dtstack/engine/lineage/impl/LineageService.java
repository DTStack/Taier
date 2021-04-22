package com.dtstack.engine.lineage.impl;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.pojo.LevelAndCount;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.*;
import com.dtstack.engine.api.vo.lineage.param.*;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.lineage.adapter.*;
import com.dtstack.engine.lineage.enums.SourceType2TableType;
import com.dtstack.engine.lineage.util.SqlParserClientOperator;
import com.dtstack.engine.dao.LineageColumnColumnUniqueKeyRefDao;
import com.dtstack.engine.dao.LineageTableTableUniqueKeyRefDao;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.sqlparser.common.client.ISqlParserClient;
import com.dtstack.sqlparser.common.client.domain.AlterResult;
import com.dtstack.sqlparser.common.client.domain.ColumnLineage;
import com.dtstack.sqlparser.common.client.domain.ParseResult;
import com.dtstack.sqlparser.common.client.domain.Table;
import com.dtstack.sqlparser.common.client.domain.TableLineage;
import com.dtstack.sqlparser.common.client.enums.SqlType;
import com.dtstack.sqlparser.common.client.enums.TableOperateEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chener
 * @Classname LineageService
 * @Description 血缘解析、存储service
 * @Date 2020/10/23 14:43
 * @Created chener@dtstack.com
 */
@Service
public class LineageService {
    private static final Logger logger = LoggerFactory.getLogger(LineageService.class);

    private static final Integer HUNDRED = 100;

    @Autowired
    private LineageTableTableService lineageTableTableService;

    @Autowired
    private SqlParserClientOperator sqlParserClientOperator;

    @Autowired
    private LineageTableTableUniqueKeyRefDao  tableUniqueKeyRefDao;

    @Autowired
    private LineageColumnColumnService lineageColumnColumnService;

    @Autowired
    private LineageDataSourceService lineageDataSourceService;

    @Autowired
    private LineageDataSetInfoService lineageDataSetInfoService;

    @Autowired
    private LineageColumnColumnUniqueKeyRefDao columnUniqueKeyRefDao;


    /**
     * 解析sql基本信息
     *
     * @param sql 单条sql
     * @return
     */
    public SqlParseInfo parseSql(String sql, String defaultDb, Integer dataSourceType) {
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(dataSourceType);
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + dataSourceType + "不支持");
        }
        SqlParseInfo parseInfo = new SqlParseInfo();
//        ISqlParserClient sqlParserClient = getSqlParserClient();
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlParser");
        parseInfo.setOriginSql(sql);
        try {
            ParseResult parseResult = null;
            try {
                parseResult = sqlParserClient.parseSql(sql, defaultDb, new HashMap<>(),sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            parseInfo.setMainDb(parseResult.getCurrentDb());
            Table mainTable = parseResult.getMainTable();
            parseInfo.setMainTable(TableAdapter.sqlTable2ApiTable(mainTable));
            parseInfo.setCurrentDb(parseResult.getCurrentDb());
            parseInfo.setFailedMsg(parseResult.getFailedMsg());
            parseInfo.setParseSuccess(parseResult.isParseSuccess());
            parseInfo.setSqlType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getSqlType()));
            parseInfo.setExtraType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getExtraSqlType()));
            parseInfo.setStandardSql(parseResult.getStandardSql());
            parseInfo.setOriginSql(parseResult.getOriginSql());
            parseInfo.setAlterResult(AlterResultAdapter.sqlAlterResult2ApiResult(parseResult.getAlterResult()));
            parseInfo.setRoot(QueryTableTreeAdapter.sqlQueryTableTree2ApiQueryTableTree(parseResult.getRoot()));
        } catch (Exception e) {
            logger.error("sql解析失败：{}", e);
            parseInfo.setFailedMsg(e.getMessage());
            parseInfo.setParseSuccess(false);
        }
        return parseInfo;
    }

    public Set<String> parseFunction(String sql){

        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlParser");
        Set<String> functions  = null;
        try {
            functions = sqlParserClient.parseFunction(sql);
        } catch (Exception e) {
            logger.error("parseFunction error:{}",e);
            throw new RdosDefineException("sql解析异常，请检查语法");
        }
        return functions;
    }


    /**
     * 解析表血缘
     *
     * @param sql       单条sql
     * @param defaultDb 默认数据库
     * @return
     */
    public TableLineageParseInfo parseTableLineage(String sql, String defaultDb, Integer dataSourceType) {
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(dataSourceType);
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + dataSourceType + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlParser");
        TableLineageParseInfo parseInfo = new TableLineageParseInfo();
        try {
            ParseResult parseResult = null;
            try {
                parseResult = sqlParserClient.parseTableLineage(sql, defaultDb,sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            parseInfo.setMainDb(parseResult.getCurrentDb());
            Table mainTable = parseResult.getMainTable();
            parseInfo.setMainTable(TableAdapter.sqlTable2ApiTable(mainTable));
            parseInfo.setCurrentDb(parseResult.getCurrentDb());
            parseInfo.setFailedMsg(parseResult.getFailedMsg());
            parseInfo.setParseSuccess(parseResult.isParseSuccess());
            parseInfo.setSqlType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getSqlType()));
            parseInfo.setExtraType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getExtraSqlType()));
            parseInfo.setStandardSql(parseResult.getStandardSql());
            parseInfo.setOriginSql(parseResult.getOriginSql());
            List<TableLineage> tableLineages = parseResult.getTableLineages();
            if (CollectionUtils.isNotEmpty(tableLineages)) {
                parseInfo.setTableLineages(tableLineages.stream().map(TableLineageAdapter::sqlTableLineage2ApiTableLineage).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            logger.error("sql解析失败：{}", e);
            parseInfo.setFailedMsg(e.getMessage());
            parseInfo.setParseSuccess(false);
        }

        return parseInfo;
    }

    /**
     * 解析并存储表血缘
     *
     * @param appType        应用类型
     * @param sql            单条sql
     * @param defaultDb      默认数据库
     * @param engineSourceId 数据源id
     * @param sourceType
     */
    @Async
    public void parseAndSaveTableLineage(Long dtUicTenantId, Integer appType, String sql, String defaultDb, Long engineSourceId, Integer sourceType, String unionKey) {
        LineageDataSource lineageDataSource = null;
        Map<String,LineageDataSource> dataSourceMap = new HashMap<>();
        if (AppType.RDOS.getType().equals(appType)) {
            //离线根据uic租户id和sourceType查询数据源
            try {
                List<LineageDataSource> dataSourceList = lineageDataSourceService.getDataSourceByParams(sourceType, null, dtUicTenantId, AppType.RDOS.getType());
                if(CollectionUtils.isEmpty(dataSourceList)){
                    logger.error("do not find need ");
                    throw new RdosDefineException("没有可用的数据源");
                }
                lineageDataSource = dataSourceList.get(0);
                for (LineageDataSource dataSource : dataSourceList) {
                    dataSourceMap.put(dataSource.getSchemaName(),dataSource);
                }
            } catch (Exception e) {
                logger.error("",e);
            }
        } else {
            //资产通过数据源id查询数据源
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        if (Objects.isNull(lineageDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(lineageDataSource.getSourceType());
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + lineageDataSource.getSourceType() + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlParser");
        try {
            ParseResult parseResult = null;
            try {
                parseResult = sqlParserClient.parseTableLineage(sql, defaultDb,sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            //3.根据表名和dbName，schemaName查询表,sourceId。表不存在则需要插入表
            List<Table> tables = null;
            try {
                tables = sqlParserClient.parseTables(defaultDb, sql,sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            Map<String, LineageDataSetInfo> tableRef = getTableRef(appType, defaultDb, lineageDataSource, dataSourceMap, tables);
            saveTableLineage(dtUicTenantId, appType, unionKey, parseResult, tableRef);

        } catch (Exception e) {
            logger.error("解析保存表血缘失败：{}", e);
            throw new RdosDefineException("解析保存表血缘失败");
        }
    }

    public void saveTableLineage(Long dtUicTenantId, Integer appType, String unionKey, ParseResult parseResult, Map<String, LineageDataSetInfo> tableRef) {
        List<TableLineage> tableLineages = parseResult.getTableLineages();
        if (CollectionUtils.isNotEmpty(tableLineages)) {
            List<LineageTableTable> lineageTableTables = tableLineages.stream().map(l -> {
                LineageTableTable tableTable = TableLineageAdapter.sqlTableLineage2DbTableLineage(l, tableRef, LineageOriginType.SQL_PARSE);
                tableTable.setDtUicTenantId(dtUicTenantId);
                tableTable.setAppType(appType);
                return tableTable;
            }).collect(Collectors.toList());
            //如果uniqueKey不为空，则删除相同uniqueKey的血缘
            lineageTableTableService.saveTableLineage(null,lineageTableTables, unionKey);
        }
    }

    public Map<String, LineageDataSetInfo> getTableRef(Integer appType, String defaultDb, LineageDataSource lineageDataSource, Map<String, LineageDataSource> dataSourceMap, List<Table> tables) {
        Map<String, LineageDataSetInfo> tableRef = new HashMap<>();
        String tableKey = "%s.%s";
        LineageDataSource defaultDataSource = dataSourceMap.get(defaultDb);
        List<com.dtstack.engine.api.pojo.lineage.Table> tableList = tables.stream().map(TableAdapter::sqlTable2ApiTable).collect(Collectors.toList());
        Map<String, List<Column>> columns = lineageDataSetInfoService.getColumnsBySourceIdAndListTable(defaultDataSource.getId(), tableList);
        for (int i = 0; i < tables.size(); i++) {
            Table ta = tables.get(i);
            LineageDataSetInfo dataSet = null;
            if(!AppType.RDOS.getType().equals(appType)) {
                dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), ta.getDb(), ta.getName(), ta.getDb());
            }else{
                //离线
                String db = ta.getDb();
                LineageDataSource dataSource = dataSourceMap.get(db);
                if(null!= dataSource) {
                    dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSource.getId(), ta.getDb(), ta.getName(), ta.getDb());
                }else{
                    //该db对应的数据源不存在，需要添加数据源
                    long id = addDataSource(defaultDb, defaultDataSource, db);
                    dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(id,ta.getDb(),ta.getName(),ta.getDb());
                }
            }
            tableRef.put(String.format(tableKey, ta.getDb(), ta.getName()), dataSet);
        }
        return tableRef;
    }

    private long addDataSource(String defaultDb, LineageDataSource defaultDataSource, String db) {
        String dataJson = defaultDataSource.getDataJson();
        String newDataJson = dataJson.replace(defaultDb, db);
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        BeanUtils.copyProperties(defaultDataSource,dataSourceDTO);
        dataSourceDTO.setDataSourceId(null);
        dataSourceDTO.setDataJson(newDataJson);
        dataSourceDTO.setSchemaName(db);
        return lineageDataSourceService.addOrUpdateDataSource(dataSourceDTO);

    }

    /**
     * 解析字段级血缘
     *
     * @param sql             单条sql
     * @param defaultDb       默认数据库
     * @param tableColumnsMap 表字段map
     * @return
     */
    public ColumnLineageParseInfo parseColumnLineage(String sql, Integer dataSourceType, String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(dataSourceType);
        if (Objects.isNull(sourceType2TableType)) {
            throw new RdosDefineException("数据源类型" + dataSourceType + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlParser");
        ColumnLineageParseInfo parseInfo = new ColumnLineageParseInfo();
        try {
            Map<String, List<com.dtstack.sqlparser.common.client.domain.Column>> sqlColumnMap = new HashMap<>();
            for (Map.Entry<String, List<Column>> entry : tableColumnsMap.entrySet()) {
                String key = entry.getKey();
                List<Column> value = entry.getValue();
                sqlColumnMap.put(key, value.stream().map(ColumnAdapter::apiColumn2SqlColumn).collect(Collectors.toList()));
            }
            ParseResult parseResult = null;
            try {
                parseResult = sqlParserClient.parseSql(sql, defaultDb, sqlColumnMap,sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            parseInfo.setMainDb(parseResult.getCurrentDb());
            Table mainTable = parseResult.getMainTable();
            parseInfo.setMainTable(TableAdapter.sqlTable2ApiTable(mainTable));
            parseInfo.setCurrentDb(parseResult.getCurrentDb());
            parseInfo.setFailedMsg(parseResult.getFailedMsg());
            parseInfo.setParseSuccess(parseResult.isParseSuccess());
            parseInfo.setSqlType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getSqlType()));
            parseInfo.setExtraType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getExtraSqlType()));
            parseInfo.setStandardSql(parseResult.getStandardSql());
            parseInfo.setOriginSql(parseResult.getOriginSql());
            AlterResult alterResult = parseResult.getAlterResult();
            if(alterResult != null){
                parseInfo.setAlterResult(AlterResultAdapter.sqlAlterResult2ApiResult(parseResult.getAlterResult()));
            }
            if(CollectionUtils.isNotEmpty(parseResult.getTables())){
                List<Table> tables = parseResult.getTables();
                List<com.dtstack.engine.api.pojo.lineage.Table> apiTables = tables.stream().map(TableAdapter::sqlTable2ApiTable).collect(Collectors.toList());
                parseInfo.setTables(apiTables);
            }
            List<TableLineage> tableLineages = parseResult.getTableLineages();
            if (CollectionUtils.isNotEmpty(tableLineages)) {
                parseInfo.setTableLineages(tableLineages.stream().map(TableLineageAdapter::sqlTableLineage2ApiTableLineage).collect(Collectors.toList()));
            }
            List<ColumnLineage> columnLineages = parseResult.getColumnLineages();
            if (CollectionUtils.isNotEmpty(columnLineages)) {
                parseInfo.setColumnLineages(columnLineages.stream().map(ColumnLineageAdapter::sqlColumnLineage2ApiColumnLineage).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            logger.error("sql解析失败：{}", e);
            parseInfo.setFailedMsg(e.getMessage());
            parseInfo.setParseSuccess(false);
        }

        return parseInfo;
    }

    /**
     * 解析并存储字段级血缘
     */
    @Async
    public void parseAndSaveColumnLineage(ParseColumnLineageParam parseColumnLineageParam) {

        logger.info("进入parseAndSaveColumnLineage方法:{}",JSON.toJSON(parseColumnLineageParam));
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        //3.根据表名和数据库名，数据库id查询表。表不存在则需要插入表
        //4.获取表中的字段列表
        //5.解析字段级血缘关系
        //6.存储字段级血缘关系
        LineageDataSource lineageDataSource;
        Map<String,LineageDataSource> dataSourceMap = new HashMap<>();
        if (AppType.RDOS.getType().equals(parseColumnLineageParam.getAppType())) {
            List<LineageDataSource> dataSourceList = lineageDataSourceService.getDataSourceByParams(parseColumnLineageParam.getDataSourceType(), null, parseColumnLineageParam.getDtUicTenantId(), AppType.RDOS.getType());
            if(CollectionUtils.isEmpty(dataSourceList)){
                logger.error("do not find need ");
                throw new RdosDefineException("没有可用的数据源");
            }
            for (LineageDataSource dataSource : dataSourceList) {
                dataSourceMap.put(dataSource.getSchemaName(),dataSource);
            }
            lineageDataSource = dataSourceMap.get(parseColumnLineageParam.getDefaultDb());
        } else {
            //资产通过数据源id查询数据源
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(parseColumnLineageParam.getEngineDataSourceId(), parseColumnLineageParam.getAppType());
        }
        LineageDataSource defaultDataSource = dataSourceMap.get(parseColumnLineageParam.getDefaultDb());
        if (Objects.isNull(lineageDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(lineageDataSource.getSourceType());
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + lineageDataSource.getSourceType() + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlParser");
        try {
            List<Table> resTables = null;
            try {
                resTables = sqlParserClient.parseTables(parseColumnLineageParam.getDefaultDb(),parseColumnLineageParam.getSql(),sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            //去除主表，主表需要创建，还未存在，查不到字段信息，需要过滤掉
            List<Table> subTables = resTables.stream().filter(table->
                    table.getOperate() != TableOperateEnum.CREATE ).collect(Collectors.toList());
            Set<com.dtstack.engine.api.pojo.lineage.Table> tables = subTables.stream().map(TableAdapter::sqlTable2ApiTable).collect(Collectors.toSet());
            //TODO 获取表字段信息
            Map<String, List<Column>> tableColumnMap = lineageDataSetInfoService.getColumnsBySourceIdAndListTable(lineageDataSource.getId(), Lists.newArrayList(tables));
            Map<String, List<com.dtstack.sqlparser.common.client.domain.Column>> sqlTableColumnMap = new HashMap<>();
            for (Map.Entry<String,List<Column>> entry:tableColumnMap.entrySet()){
                String dbName = entry.getKey();
                List<Column> columns = entry.getValue();
                if (Objects.isNull(columns)){
                    throw new RdosDefineException("表字段获取失败");
                }
                sqlTableColumnMap.put(dbName,entry.getValue().stream().map(ColumnAdapter::apiColumn2SqlColumn).collect(Collectors.toList()));
            }
            if(AppType.RDOS.getType().equals(parseColumnLineageParam.getAppType())) {
                for (Table resTable : resTables) {
                    //校验db对应的数据源是否存在，不存在需要新增
                    if(!dataSourceMap.containsKey(resTable.getDb())){
                        long dataSourceId = addDataSource(parseColumnLineageParam.getDefaultDb(), defaultDataSource, resTable.getDb());
                        LineageDataSource dataSource = new LineageDataSource();
                        dataSource.setId(dataSourceId);
                        dataSourceMap.put(resTable.getDb(),dataSource);
                    }
                }
            }
            ParseResult parseResult = null;
            try {
                parseResult = sqlParserClient.parseSql(parseColumnLineageParam.getSql(), parseColumnLineageParam.getDefaultDb(), sqlTableColumnMap,sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            if(handleDropTableAndAlterRename(dataSourceMap, parseResult)){
                return;
            }
            //3.根据表名和数dbName，schemaName查询表,sourceId。表不存在则需要插入表
            Map<String, LineageDataSetInfo> tableRef = new HashMap<>();
            String tableKey = "%s.%s";
            for (int i = 0; i < resTables.size(); i++) {
                Table ta = resTables.get(i);
                LineageDataSetInfo dataSet = null;
                if(!AppType.RDOS.getType().equals(parseColumnLineageParam.getAppType())) {
                    dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), ta.getDb(), ta.getName(), ta.getDb());
                }else{
                    dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSourceMap.get(ta.getDb()).getId(), ta.getDb(), ta.getName(), ta.getDb());
                }
                tableRef.put(String.format(tableKey, ta.getDb(), ta.getName()), dataSet);
            }
            try {
                ParseResult parseTableLineage = sqlParserClient.parseTableLineage(parseColumnLineageParam.getSql(), parseColumnLineageParam.getDefaultDb(),sourceType2TableType.getTableType());
                List<TableLineage> tableLineages = parseTableLineage.getTableLineages();
                if (CollectionUtils.isNotEmpty(tableLineages)) {
                    List<LineageTableTable> lineageTableTables = tableLineages.stream().map(l -> TableLineageAdapter.sqlTableLineage2DbTableLineage(l, tableRef, LineageOriginType.SQL_PARSE)).collect(Collectors.toList());
                    logger.info("lineageTableTables为:{}",JSON.toJSON(lineageTableTables));
                    //如果uniqueKey不为空，则删除相同uniqueKey的血缘
                    lineageTableTableService.saveTableLineage(parseColumnLineageParam.getType(),lineageTableTables,parseColumnLineageParam.getUniqueKey());
                }
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            List<ColumnLineage> columnLineages = parseResult.getColumnLineages();
            if (CollectionUtils.isNotEmpty(columnLineages)) {
                lineageColumnColumnService.saveColumnLineage(parseColumnLineageParam.getType(),columnLineages.stream().map(cl -> ColumnLineageAdapter.sqlColumnLineage2ColumnColumn(cl, parseColumnLineageParam.getAppType(), tableRef)).collect(Collectors.toList()),parseColumnLineageParam.getUniqueKey());
            }

        } catch (Exception e) {
            logger.error("解析保存表血缘失败：{}", e);
            throw new RdosDefineException("解析保存字段血缘失败");
        }
    }

    public Boolean handleDropTableAndAlterRename(Map<String, LineageDataSource> dataSourceMap, ParseResult parseResult) {
        SqlType sqlType = parseResult.getSqlType();
        if(sqlType.getType().equals(com.dtstack.engine.api.vo.lineage.SqlType.DROP.getType())){
            //drop表操作，需要删除对应的血缘
            Table mainTable = parseResult.getMainTable();
            String db = mainTable.getDb();
            String tableName = mainTable.getName();
            LineageDataSetInfo dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSourceMap.get(db).getId(), db, tableName, db);
            //根据表id查询表血缘
            List<LineageTableTable> tableTableList = lineageTableTableService.queryTableTableByTableAndAppId(dataSet.getAppType(),dataSet.getId(),1);
            List<Long> idList = tableTableList.stream().map(LineageTableTable::getId).collect(Collectors.toList());
            //删除表血缘的关联关系
            if(CollectionUtils.isNotEmpty(idList)) {
                tableUniqueKeyRefDao.deleteByLineageTableIdList(idList, dataSet.getAppType());
            }
            //根据表id查询字段血缘
            List<LineageColumnColumn> columnColumnList = lineageColumnColumnService.queryColumnLineages(dataSet.getAppType(), dataSet.getId(), null, 1);
            List<Long> columnColumnIdList = columnColumnList.stream().map(LineageColumnColumn::getId).collect(Collectors.toList());
            //删除字段血缘的关联关系
            if(CollectionUtils.isNotEmpty(columnColumnList)) {
                columnUniqueKeyRefDao.deleteByLineageColumnIdList(columnColumnIdList, dataSet.getAppType());
            }
            return true;
        }else if(sqlType.getType().equals(com.dtstack.engine.api.vo.lineage.SqlType.ALTER.getType()) &&
         parseResult.getAlterResult().getAlterType().getVal().equals(TableOperateEnum.ALTERTABLE_RENAME.getVal())){
            // alterRename类型，需要修改表名
            AlterResult alterResult = parseResult.getAlterResult();
            String oldDB = alterResult.getOldDB();
            String oldTableName = alterResult.getOldTableName();
            String newTableName = alterResult.getNewTableName();
            LineageDataSource dataSource = dataSourceMap.get(oldDB);
            lineageDataSetInfoService.updateTableNameByTableNameAndSourceId(oldTableName,newTableName,dataSource);
            return true;
        }else{
            return false;
        }
    }

    private LineageDataSource getDataSource(QueryTableLineageParam queryTableLineageParam){
        Integer appType = queryTableLineageParam.getAppType();
        LineageDataSource lineageDataSource = null;
        if(AppType.DATAASSETS.getType().equals(appType)) {
            //TODO 资产维护了engine的sourceId，其他平台sourceId交给engine维护，后续资产需要修改
            Long dtUicTenantId = queryTableLineageParam.getDtUicTenantId();
            Long engineSourceId = queryTableLineageParam.getEngineSourceId();
            Integer sourceType = queryTableLineageParam.getSourceType();
            if (null == engineSourceId ) {
                List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(sourceType, queryTableLineageParam.getSourceName(), dtUicTenantId, queryTableLineageParam.getAppType());
                if(CollectionUtils.isEmpty(dataSources)){
                    logger.error("do not find need dataSource");
                    throw new RdosDefineException("没有可用的数据源");
                }
                lineageDataSource = dataSources.get(0);
            } else {
                lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
            }
        }else{
            lineageDataSource = lineageDataSourceService.getDataSourceBySourceIdAndAppType(queryTableLineageParam.getSourceId(),appType);
        }
        return lineageDataSource;
    }

    /**
     * 查询表上游表血缘数量和层数
     *
     * @return
     */
    public LevelAndCount queryTableInputLineageCountAndLevel(QueryTableLineageParam queryTableLineageParam) {

        Integer appType = queryTableLineageParam.getAppType();
        LineageDataSource lineageDataSource = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName() != null ? queryTableLineageParam.getSchemaName() : lineageDataSource.getSchemaName();
        String dbName = queryTableLineageParam.getDbName() != null ? queryTableLineageParam.getDbName() : lineageDataSource.getSchemaName();
        String tableName = queryTableLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        LevelAndCount lc = new LevelAndCount();
        lc.setLevelCount(HUNDRED);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableInputLineageByAppType(dataSetInfo.getId(), appType, new HashSet<>(), lc);
        Integer directCount = lineageTableTableService.queryTableInputLineageDirectCount(dataSetInfo.getId(), appType);
        LevelAndCount levelAndCount = new LevelAndCount();
        levelAndCount.setCount(lineageTableTables.size());
        levelAndCount.setLevelCount(HUNDRED - lc.getLevelCount());
        levelAndCount.setDirectCount(directCount);
        return levelAndCount;
    }

    /**
     * 查询表上游表血缘
     *
     * @return
     */
    public List<LineageTableTableVO> queryTableInputLineage(QueryTableLineageParam queryTableLineageParam) {

        Integer appType = queryTableLineageParam.getAppType();
        LineageDataSource lineageDataSource = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName() !=null ? queryTableLineageParam.getSchemaName():lineageDataSource.getSchemaName();
        String dbName = queryTableLineageParam.getDbName() !=null ? queryTableLineageParam.getDbName() : lineageDataSource.getSchemaName();
        String tableName = queryTableLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        LevelAndCount levelAndCount = new LevelAndCount();
        levelAndCount.setLevelCount(queryTableLineageParam.getLevel());
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableInputLineageByAppType(dataSetInfo.getId(), appType,new HashSet<>(),levelAndCount);
        if(CollectionUtils.isEmpty(lineageTableTables)){
            return Lists.newArrayList();
        }
        Set<Long> tableIds = Sets.newHashSet();
        for (LineageTableTable ltt : lineageTableTables) {
            tableIds.add(ltt.getInputTableId());
            tableIds.add(ltt.getResultTableId());
        }
        List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
        Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getSourceId).collect(Collectors.toSet());
        Map<Long, LineageDataSource> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getId(), ds2 -> ds2));
        List<LineageTableTableVO> res = Lists.newArrayList();
        for (LineageTableTable tt : lineageTableTables) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(tt.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(tt.getResultTableId());
            LineageTableTableVO lineageTableTableVO = TableLineageAdapter.tableTable2TableTableVO(tt, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getSourceId()), dataSourceMap.get(resultTableInfo.getSourceId()));
            res.add(lineageTableTableVO);
        }
        return res;
    }


    /**
     * 查询表下游表血缘数量和层数
     *
     * @return
     */
    public LevelAndCount queryTableResultLineageCountAndLevel(QueryTableLineageParam queryTableLineageParam) {

        Integer appType = queryTableLineageParam.getAppType();
        LineageDataSource lineageDataSource = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName() !=null ? queryTableLineageParam.getSchemaName():lineageDataSource.getSchemaName();
        String dbName = queryTableLineageParam.getDbName() !=null ? queryTableLineageParam.getDbName() : lineageDataSource.getSchemaName();
        String tableName = queryTableLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        LevelAndCount lv = new LevelAndCount();
        lv.setLevelCount(HUNDRED);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableResultLineageByAppType(dataSetInfo.getId(), appType,new HashSet<>(),lv);
        LevelAndCount levelAndCount = new LevelAndCount();
        levelAndCount.setCount(lineageTableTables.size());
        levelAndCount.setLevelCount(HUNDRED - lv.getLevelCount());
        //查询直接下游表数量
        Integer directCount = lineageTableTableService.queryTableResultLineageDirectCount(dataSetInfo.getId(), appType);
        levelAndCount.setDirectCount(directCount);
        return levelAndCount;
    }

    /**
     * 查询表下游表血缘
     *
     * @return
     */
    public List<LineageTableTableVO> queryTableResultLineage(QueryTableLineageParam queryTableLineageParam) {

        Integer appType = queryTableLineageParam.getAppType();
        LineageDataSource lineageDataSource = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName() !=null ? queryTableLineageParam.getSchemaName():lineageDataSource.getSchemaName();
        String dbName = queryTableLineageParam.getDbName() !=null ? queryTableLineageParam.getDbName() : lineageDataSource.getSchemaName();
        String tableName = queryTableLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LevelAndCount lv = new LevelAndCount();
        lv.setLevelCount(queryTableLineageParam.getLevel());
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableResultLineageByAppType(dataSetInfo.getId(), appType,new HashSet<>(),lv);
        if(CollectionUtils.isEmpty(lineageTableTables)){
            return Lists.newArrayList();
        }
        Set<Long> tableIds = Sets.newHashSet();
        for (LineageTableTable ltt : lineageTableTables) {
            tableIds.add(ltt.getInputTableId());
            tableIds.add(ltt.getResultTableId());
        }
        List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
        Map<Long, LineageDataSetInfo> dataSetMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getSourceId).collect(Collectors.toSet());
        Map<Long, LineageDataSource> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getId(), ds2 -> ds2));
        List<LineageTableTableVO> res = Lists.newArrayList();
        for (LineageTableTable tt : lineageTableTables) {
            LineageDataSetInfo inputTableInfo = dataSetMap.get(tt.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetMap.get(tt.getResultTableId());
            LineageTableTableVO lineageTableTableVO = TableLineageAdapter.tableTable2TableTableVO(tt, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getSourceId()), dataSourceMap.get(resultTableInfo.getSourceId()));
            res.add(lineageTableTableVO);
        }
        return res;
    }

    /**
     * 查询表级血缘关系
     *
     * @return
     */
    public List<LineageTableTableVO> queryTableLineages(QueryTableLineageParam queryTableLineageParam) {

        Integer appType = queryTableLineageParam.getAppType();
        LineageDataSource lineageDataSource = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName() !=null ? queryTableLineageParam.getSchemaName():lineageDataSource.getSchemaName();
        String dbName = queryTableLineageParam.getDbName() !=null ? queryTableLineageParam.getDbName() : lineageDataSource.getSchemaName();
        String tableName = queryTableLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableTableByTableAndAppId(appType, dataSetInfo.getId(),queryTableLineageParam.getLevel());
        if(CollectionUtils.isEmpty(lineageTableTables)){
            return Lists.newArrayList();
        }
        Set<Long> tableIds = Sets.newHashSet();
        for (LineageTableTable ltt : lineageTableTables) {
            tableIds.add(ltt.getInputTableId());
            tableIds.add(ltt.getResultTableId());
        }
        List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
        Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getSourceId).collect(Collectors.toSet());
        Map<Long, LineageDataSource> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getId(), ds2 -> ds2));
        List<LineageTableTableVO> res = Lists.newArrayList();
        for (LineageTableTable tt : lineageTableTables) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(tt.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(tt.getResultTableId());
            LineageTableTableVO lineageTableTableVO = TableLineageAdapter.tableTable2TableTableVO(tt, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getSourceId()), dataSourceMap.get(resultTableInfo.getSourceId()));
            res.add(lineageTableTableVO);
        }
        return res;
    }

    /**
     * 手动添加表级血缘
     *
     * @param tableTableVOs
     */
    public void manualAddTableLineage(List<LineageTableTableVO> tableTableVOs) {
        for (LineageTableTableVO tableTableVO : tableTableVOs){
            LineageTableTable lineageTableTable = null;
            LineageTableVO inputTableInfoVo = tableTableVO.getInputTableInfo();
            LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
            Integer appType = tableTableVO.getAppType();
            LineageDataSource dataSource = null;
            if (Objects.isNull(inputDataSourceVO.getSourceId())) {
                List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), tableTableVO.getDtUicTenantId(), tableTableVO.getAppType());
                if(CollectionUtils.isEmpty(dataSources)){
                    logger.error("do not find need dataSource");
                    throw new RdosDefineException("没有可用的数据源");
                }
                dataSource = dataSources.get(0);
            }else {
                dataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(),appType);
            }
            if (Objects.isNull(dataSource)){
                throw new RdosDefineException("数据源不存在");
            }
            LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSource.getId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
            LineageTableVO resultTableInfoVO = tableTableVO.getResultTableInfo();
            LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
            LineageDataSource resultDataSource = null;
            if (Objects.isNull(resultDataSourceVO.getSourceId())) {
                List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), tableTableVO.getDtUicTenantId(), tableTableVO.getAppType());
                if(CollectionUtils.isEmpty(dataSources)){
                    logger.error("do not find need dataSource");
                    throw new RdosDefineException("没有可用的数据源");
                }
                resultDataSource = dataSources.get(0);
            }else {
                resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getSourceId(),appType);
            }
            if (Objects.isNull(resultDataSource)){
                throw new RdosDefineException("数据源不存在");
            }
            LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName());
            lineageTableTable = new LineageTableTable();
            lineageTableTable.setResultTableId(resultTableInfo.getId());
            lineageTableTable.setResultTableKey(resultTableInfo.getTableKey());
            lineageTableTable.setInputTableId(inputTableInfo.getId());
            lineageTableTable.setInputTableKey(inputTableInfo.getTableKey());
            lineageTableTable.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
            lineageTableTable.setAppType(tableTableVO.getAppType());
            lineageTableTable.setDtUicTenantId(tableTableVO.getDtUicTenantId());
            lineageTableTableService.manualAddTableLineage(tableTableVO.getAppType(), lineageTableTable,tableTableVO.getUniqueKey(),LineageOriginType.MANUAL_ADD.getType());
        }
    }

    public void acquireOldTableTable(List<LineageTableTableVO> lineageTableTableVOs){
        //最大批次200
        if (lineageTableTableVOs.size()>200){
            throw new RdosDefineException("请分批执行");
        }
        logger.info("start acquireOld tableLineage,count:{}",lineageTableTableVOs.size());
        //FIXME 暂未考虑性能
        Integer errorCount = 0;
        for (LineageTableTableVO tableTableVO:lineageTableTableVOs){
            try {
                LineageTableTable lineageTableTable = null;
                LineageTableVO inputTableInfoVo = tableTableVO.getInputTableInfo();
                LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
                Integer appType = tableTableVO.getAppType();
                LineageDataSource dataSource = null;
                if (Objects.isNull(inputDataSourceVO.getSourceId())) {
                    List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), tableTableVO.getDtUicTenantId(), tableTableVO.getAppType());
                    if(CollectionUtils.isEmpty(dataSources)){
                        logger.error("do not find need dataSource");
                        throw new RdosDefineException("没有可用的数据源");
                    }
                    dataSource = dataSources.get(0);
                }else {
                    if(AppType.DATAASSETS.getType().equals(appType)) {
                        //资产的sourceId是engine的sourceId
                        dataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(), appType);
                    }else{
                        //离线的传的sourceId是离线自身的sourceId
                        dataSource = lineageDataSourceService.getDataSourceBySourceIdAndAppType(inputDataSourceVO.getSourceId(), appType);
                    }
                }
                if (Objects.isNull(dataSource)){
                    continue;
    //                throw new RdosDefineException("数据源不存在");
                }
                String inputDbName = inputTableInfoVo.getDbName() !=null ? inputTableInfoVo.getDbName() : dataSource.getSchemaName();
                LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName( dataSource.getId(), inputDbName, inputTableInfoVo.getTableName(), inputDbName);
                LineageTableVO resultTableInfoVO = tableTableVO.getResultTableInfo();
                LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
                LineageDataSource resultDataSource = null;
                if (Objects.isNull(resultDataSourceVO.getSourceId())) {
                    List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(resultDataSourceVO.getSourceType(), resultDataSource.getSourceName(), tableTableVO.getDtUicTenantId(), tableTableVO.getAppType());
                    if(CollectionUtils.isEmpty(dataSources)){
                        logger.error("do not find need dataSource");
                        throw new RdosDefineException("没有可用的数据源");
                    }
                    resultDataSource = dataSources.get(0);
                }else {
                    if(AppType.DATAASSETS.getType().equals(appType)) {
                        //资产的sourceId是engine的sourceId
                        resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getSourceId(), appType);
                    }else{
                        //离线的传的sourceId是离线自身的sourceId
                        resultDataSource = lineageDataSourceService.getDataSourceBySourceIdAndAppType(resultDataSourceVO.getSourceId(), appType);
                    }
                }
                if (Objects.isNull(resultDataSource)){
                    throw new RdosDefineException("数据源不存在");
                }
                String resultDbName = resultTableInfoVO.getDbName() !=null ? resultTableInfoVO.getDbName() : resultDataSource.getSchemaName();
                LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getId(), resultDbName, resultTableInfoVO.getTableName(),resultDbName);
                lineageTableTable = new LineageTableTable();
                lineageTableTable.setResultTableId(resultTableInfo.getId());
                lineageTableTable.setResultTableKey(resultTableInfo.getTableKey());
                lineageTableTable.setInputTableId(inputTableInfo.getId());
                lineageTableTable.setInputTableKey(inputTableInfo.getTableKey());
                lineageTableTable.setLineageSource(LineageOriginType.SQL_PARSE.getType());
                lineageTableTable.setAppType(tableTableVO.getAppType());
                lineageTableTable.setDtUicTenantId(tableTableVO.getDtUicTenantId());
                Integer isManual = null;
                if (Objects.nonNull(tableTableVO.getManual()) && tableTableVO.getManual()){
                    isManual = LineageOriginType.MANUAL_ADD.getType();
                }
                lineageTableTableService.manualAddTableLineage(tableTableVO.getAppType(), lineageTableTable,tableTableVO.getUniqueKey(),isManual);
            } catch (Exception e) {
                errorCount ++;
                logger.error("acquire old tableLineage error:{},errorCount:{},tableTableVO:{}",e,errorCount, JSON.toJSON(tableTableVO));
            }
        }
    }

    /**
     * 手动删除表级血缘
     *
     * @param tableTableVO
     */
    public void manualDeleteTableLineage(LineageTableTableVO tableTableVO) {
        LineageTableTable lineageTableTable = null;
        LineageTableVO inputTableInfoVo = tableTableVO.getInputTableInfo();
        LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
        Integer appType = tableTableVO.getAppType();
        LineageDataSource inputDataSource = null;
        if (Objects.isNull(inputDataSourceVO.getSourceId())) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), tableTableVO.getDtUicTenantId(), tableTableVO.getAppType());
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            inputDataSource = dataSources.get(0);
        }else {
            inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(),appType);
        }
        if (Objects.isNull(inputDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputDataSource.getId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
        LineageTableVO resultTableInfoVO = tableTableVO.getResultTableInfo();
        LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
        LineageDataSource resultDataSource = null;
        if (Objects.isNull(inputDataSourceVO.getSourceId())) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), tableTableVO.getDtUicTenantId(), tableTableVO.getAppType());
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            resultDataSource = dataSources.get(0);
        }else {
            resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getSourceId(),appType);
        }
        if (Objects.isNull(resultDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName());
        lineageTableTable = new LineageTableTable();
        lineageTableTable.setResultTableId(resultTableInfo.getId());
        lineageTableTable.setResultTableKey(resultTableInfo.getTableKey());
        lineageTableTable.setInputTableId(inputTableInfo.getId());
        lineageTableTable.setInputTableKey(inputTableInfo.getTableKey());
        lineageTableTable.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
        lineageTableTable.setDtUicTenantId(tableTableVO.getDtUicTenantId());
        lineageTableTable.setAppType(tableTableVO.getAppType());
        lineageTableTableService.manualDeleteTableLineage(tableTableVO.getAppType(), lineageTableTable,tableTableVO.getUniqueKey());
    }

    private LineageDataSource getDataSourceByColumnParam(QueryColumnLineageParam queryColumnLineageParam){

        Integer appType = queryColumnLineageParam.getAppType();
        Long engineSourceId = queryColumnLineageParam.getEngineSourceId();
        LineageDataSource lineageDataSource = null;
        if(AppType.DATAASSETS.getType().equals(appType)) {
            if (Objects.isNull(engineSourceId)) {
                List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(queryColumnLineageParam.getSourceType(), queryColumnLineageParam.getSourceName(), queryColumnLineageParam.getDtUicTenantId(), queryColumnLineageParam.getAppType());
                if (CollectionUtils.isEmpty(dataSources)) {
                    logger.error("do not find need dataSource");
                    throw new RdosDefineException("没有可用的数据源");
                }
                lineageDataSource = dataSources.get(0);
            } else {
                lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
            }
        }else{
            //其他平台engineSourceId代表的是各平台的sourceId
            lineageDataSource = lineageDataSourceService.getDataSourceBySourceIdAndAppType(queryColumnLineageParam.getSourceId(),appType);
        }
        return lineageDataSource;
    }


    /**
     * 查询字段上游字段血缘
     * @return
     */
    public List<LineageColumnColumnVO> queryColumnInputLineage(QueryColumnLineageParam queryColumnLineageParam) {

        Integer appType = queryColumnLineageParam.getAppType();
        LineageDataSource lineageDataSource = getDataSourceByColumnParam(queryColumnLineageParam);
        String schemaName = queryColumnLineageParam.getSchemaName() !=null ? queryColumnLineageParam.getSchemaName():lineageDataSource.getSchemaName();
        String dbName = queryColumnLineageParam.getDbName() !=null ? queryColumnLineageParam.getDbName() : lineageDataSource.getSchemaName();
        String columnName = queryColumnLineageParam.getColumnName();
        String tableName = queryColumnLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnInputLineageByAppType(appType, dataSetInfo.getId(), columnName,new HashSet<>(),queryColumnLineageParam.getLevel());
        if(CollectionUtils.isEmpty(lineageColumnColumns)){
            return Lists.newArrayList();
        }
        Set<Long> tableIds = Sets.newHashSet();
        for (LineageColumnColumn lcc : lineageColumnColumns) {
            tableIds.add(lcc.getInputTableId());
            tableIds.add(lcc.getResultTableId());
        }
        List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
        Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getSourceId).collect(Collectors.toSet());
        Map<Long, LineageDataSource> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getId(), ds2 -> ds2));
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageColumnColumn cc : lineageColumnColumns) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(cc.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(cc.getResultTableId());
            LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(cc, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getSourceId()), dataSourceMap.get(resultTableInfo.getSourceId()));
            res.add(columnColumnVO);
        }
        return res;
    }


    /**
     * 查询字段下游字段血缘
     *
     * @return
     */
    public List<LineageColumnColumnVO> queryColumnResultLineage(QueryColumnLineageParam queryColumnLineageParam) {

        Integer appType = queryColumnLineageParam.getAppType();
        LineageDataSource lineageDataSource = getDataSourceByColumnParam(queryColumnLineageParam);
        String schemaName = queryColumnLineageParam.getSchemaName() !=null ? queryColumnLineageParam.getSchemaName():lineageDataSource.getSchemaName();
        String dbName = queryColumnLineageParam.getDbName() !=null ? queryColumnLineageParam.getDbName() : lineageDataSource.getSchemaName();
        String columnName = queryColumnLineageParam.getColumnName();
        String tableName = queryColumnLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnResultLineageByAppType(appType, dataSetInfo.getId(), columnName,new HashSet<>(),queryColumnLineageParam.getLevel());
        if(CollectionUtils.isEmpty(lineageColumnColumns)){
            return Lists.newArrayList();
        }
        Set<Long> tableIds = Sets.newHashSet();
        for (LineageColumnColumn lcc : lineageColumnColumns) {
            tableIds.add(lcc.getInputTableId());
            tableIds.add(lcc.getResultTableId());
        }
        List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
        Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getSourceId).collect(Collectors.toSet());
        Map<Long, LineageDataSource> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getId(), ds2 -> ds2));
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageColumnColumn cc : lineageColumnColumns) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(cc.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(cc.getResultTableId());
            LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(cc, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getSourceId()), dataSourceMap.get(resultTableInfo.getSourceId()));
            res.add(columnColumnVO);
        }
        return res;
    }

    /**
     * 查询字段级血缘关系
     *
     * @return
     */
    public List<LineageColumnColumnVO> queryColumnLineages(QueryColumnLineageParam queryColumnLineageParam) {

        Integer appType = queryColumnLineageParam.getAppType();
        LineageDataSource lineageDataSource = getDataSourceByColumnParam(queryColumnLineageParam);
        String schemaName = queryColumnLineageParam.getSchemaName() !=null ? queryColumnLineageParam.getSchemaName():lineageDataSource.getSchemaName();
        String dbName = queryColumnLineageParam.getDbName() !=null ? queryColumnLineageParam.getDbName() : lineageDataSource.getSchemaName();
        String columnName = queryColumnLineageParam.getColumnName();
        String tableName = queryColumnLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnLineages(appType, dataSetInfo.getId(), columnName,queryColumnLineageParam.getLevel());
        if(CollectionUtils.isEmpty(lineageColumnColumns)){
            return Lists.newArrayList();
        }
        Set<Long> tableIds = Sets.newHashSet();
        for (LineageColumnColumn lcc : lineageColumnColumns) {
            tableIds.add(lcc.getInputTableId());
            tableIds.add(lcc.getResultTableId());
        }
        List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
        Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getSourceId).collect(Collectors.toSet());
        Map<Long, LineageDataSource> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getId(), ds2 -> ds2));
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageColumnColumn cc : lineageColumnColumns) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(cc.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(cc.getResultTableId());
            LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(cc, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getSourceId()), dataSourceMap.get(resultTableInfo.getSourceId()));
            res.add(columnColumnVO);
        }
        return res;
    }

    /**
     * 手动添加字级血缘
     *
     * @param lineageColumnColumnVOs
     */
    public void manualAddColumnLineage(List<LineageColumnColumnVO> lineageColumnColumnVOs) {
        for (LineageColumnColumnVO lineageColumnColumnVO:lineageColumnColumnVOs){
            Integer appType = lineageColumnColumnVO.getAppType();
            Long dtUicTenantId = lineageColumnColumnVO.getDtUicTenantId();
            LineageTableVO inputTableInfoVo = lineageColumnColumnVO.getInputTableInfo();
            LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
            LineageDataSource inputDataSource = null;
            if (Objects.isNull(inputDataSourceVO.getSourceId())) {
                List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), dtUicTenantId, inputDataSourceVO.getAppType());
                if(CollectionUtils.isEmpty(dataSources)){
                    logger.error("do not find need dataSource");
                    throw new RdosDefineException("没有可用的数据源");
                }
                inputDataSource = dataSources.get(0);
            }else {
                inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(),appType);
            }
            if (Objects.isNull(inputDataSource)){
                throw new RdosDefineException("数据源不存在");
            }
            LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputDataSource.getId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
            LineageTableVO resultTableInfoVO = lineageColumnColumnVO.getResultTableInfo();
            LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
            LineageDataSource resultDataSource = null;
            if (Objects.isNull(inputDataSourceVO.getSourceId())) {
                List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), dtUicTenantId, appType);
                if(CollectionUtils.isEmpty(dataSources)){
                    logger.error("do not find need dataSource");
                    throw new RdosDefineException("没有可用的数据源");
                }
                resultDataSource = dataSources.get(0);
            }else {
                resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(),appType);
            }
            if (Objects.isNull(resultDataSource)){
                throw new RdosDefineException("数据源不存在");
            }
            LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName());
            LineageTableTable lineageTableTable = new LineageTableTable();
            lineageTableTable.setResultTableId(resultTableInfo.getId());
            lineageTableTable.setResultTableKey(resultTableInfo.getTableKey());
            lineageTableTable.setInputTableId(inputTableInfo.getId());
            lineageTableTable.setInputTableKey(inputTableInfo.getTableKey());
            lineageTableTable.setLineageSource(LineageOriginType.SQL_PARSE.getType());
            lineageTableTable.setAppType(lineageColumnColumnVO.getAppType());
            lineageTableTable.setDtUicTenantId(lineageColumnColumnVO.getDtUicTenantId());
            Integer lineageSource = null;
            if (Objects.nonNull(lineageColumnColumnVO.getManual()) && !lineageColumnColumnVO.getManual()){
                lineageTableTable.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
                lineageSource = LineageOriginType.SQL_PARSE.getType();
            }
            lineageTableTableService.manualAddTableLineage(lineageColumnColumnVO.getAppType(),lineageTableTable,lineageColumnColumnVO.getUniqueKey(),lineageSource);
            LineageColumnColumn lineageColumnColumn = new LineageColumnColumn();
            lineageColumnColumn.setResultTableId(resultTableInfo.getId());
            lineageColumnColumn.setResultTableKey(resultTableInfo.getTableKey());
            lineageColumnColumn.setResultColumnName(lineageColumnColumnVO.getResultColumnName());
            lineageColumnColumn.setInputTableId(inputTableInfo.getId());
            lineageColumnColumn.setInputTableKey(inputTableInfo.getTableKey());
            lineageColumnColumn.setInputColumnName(lineageColumnColumnVO.getInputColumnName());
            lineageColumnColumn.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
            lineageColumnColumn.setAppType(lineageColumnColumnVO.getAppType());
            lineageColumnColumn.setDtUicTenantId(lineageColumnColumnVO.getDtUicTenantId());
            lineageColumnColumnService.manualAddColumnLineage(lineageColumnColumnVO.getAppType(), lineageColumnColumn,lineageColumnColumnVO.getUniqueKey(),null);
        }
    }

    public void acquireOldColumnColumn(List<LineageColumnColumnVO> lineageColumnColumnVOS){
        if (lineageColumnColumnVOS.size()>200){
            throw new RdosDefineException("请分批执行");
        }
        logger.info("start acquire oldColumnLineage,count:{}",lineageColumnColumnVOS.size());
        Integer errorCount = 0;
        //FIXME 优化性能
        for (LineageColumnColumnVO lineageColumnColumnVO:lineageColumnColumnVOS){
            try {
                Integer appType = lineageColumnColumnVO.getAppType();
                Long dtUicTenantId = lineageColumnColumnVO.getDtUicTenantId();
                LineageTableVO inputTableInfoVo = lineageColumnColumnVO.getInputTableInfo();
                LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
                LineageDataSource inputDataSource = null;
                if (Objects.isNull(inputDataSourceVO.getSourceId())) {
                    List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(),dtUicTenantId, appType);
                    if(CollectionUtils.isEmpty(dataSources)){
                        logger.error("do not find need dataSource");
                        throw new RdosDefineException("没有可用的数据源");
                    }
                    inputDataSource = dataSources.get(0);
                }else {
                    if(AppType.DATAASSETS.getType().equals(appType)) {
                        //资产的sourceId是engine的sourceId
                        inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(), appType);
                    }else{
                        //离线的传的sourceId是离线自身的sourceId
                        inputDataSource = lineageDataSourceService.getDataSourceBySourceIdAndAppType(inputDataSourceVO.getSourceId(), appType);
                    }
                }
                if (Objects.isNull(inputDataSource)){
                    continue;
    //                throw new RdosDefineException("数据源不存在");
                }
                String inputDbName = inputTableInfoVo.getDbName() !=null ? inputTableInfoVo.getDbName() : inputDataSource.getSchemaName();
                LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputDataSource.getId(), inputDbName, inputTableInfoVo.getTableName(), inputDbName);
                LineageTableVO resultTableInfoVO = lineageColumnColumnVO.getResultTableInfo();
                LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
                LineageDataSource resultDataSource = null;
                if (Objects.isNull(resultDataSourceVO.getSourceId())) {
                    List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(resultDataSourceVO.getSourceType(), resultDataSourceVO.getSourceName(), dtUicTenantId, appType);
                    if(CollectionUtils.isEmpty(dataSources)){
                        logger.error("do not find need dataSource");
                        throw new RdosDefineException("没有可用的数据源");
                    }
                    resultDataSource = dataSources.get(0);
                }else {
                    if(AppType.DATAASSETS.getType().equals(appType)) {
                        //资产的sourceId是engine的sourceId
                        resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getSourceId(), appType);
                    }else{
                        //离线的传的sourceId是离线自身的sourceId
                        resultDataSource = lineageDataSourceService.getDataSourceBySourceIdAndAppType(resultDataSourceVO.getSourceId(), appType);
                    }
                }
                if (Objects.isNull(resultDataSource)){
                    throw new RdosDefineException("数据源不存在");
                }
                String resultDbName = resultTableInfoVO.getDbName() !=null ? resultTableInfoVO.getDbName() : resultDataSource.getSchemaName();
                LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getId(), resultDbName, resultTableInfoVO.getTableName(), resultDbName);
                LineageTableTable lineageTableTable = new LineageTableTable();
                lineageTableTable.setResultTableId(resultTableInfo.getId());
                lineageTableTable.setResultTableKey(resultTableInfo.getTableKey());
                lineageTableTable.setInputTableId(inputTableInfo.getId());
                lineageTableTable.setInputTableKey(inputTableInfo.getTableKey());
                lineageTableTable.setLineageSource(LineageOriginType.SQL_PARSE.getType());
                lineageTableTable.setAppType(lineageColumnColumnVO.getAppType());
                lineageTableTable.setDtUicTenantId(lineageColumnColumnVO.getDtUicTenantId());
                Integer lineageSource = null;
                if (Objects.nonNull(lineageColumnColumnVO.getManual()) && !lineageColumnColumnVO.getManual()){
                    lineageTableTable.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
                    lineageSource = LineageOriginType.SQL_PARSE.getType();
                }
                lineageTableTableService.manualAddTableLineage(lineageColumnColumnVO.getAppType(),lineageTableTable,lineageColumnColumnVO.getUniqueKey(),lineageSource);
                LineageColumnColumn lineageColumnColumn = new LineageColumnColumn();
                lineageColumnColumn.setResultTableId(resultTableInfo.getId());
                lineageColumnColumn.setResultTableKey(resultTableInfo.getTableKey());
                lineageColumnColumn.setResultColumnName(lineageColumnColumnVO.getResultColumnName());
                lineageColumnColumn.setInputTableId(inputTableInfo.getId());
                lineageColumnColumn.setInputTableKey(inputTableInfo.getTableKey());
                lineageColumnColumn.setInputColumnName(lineageColumnColumnVO.getInputColumnName());
                lineageColumnColumn.setLineageSource(LineageOriginType.SQL_PARSE.getType());
                lineageColumnColumn.setAppType(lineageColumnColumnVO.getAppType());
                lineageColumnColumn.setDtUicTenantId(lineageColumnColumnVO.getDtUicTenantId());
                if (Objects.nonNull(lineageColumnColumnVO.getManual()) && !lineageColumnColumnVO.getManual()){
                    lineageColumnColumn.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
                    lineageSource = LineageOriginType.SQL_PARSE.getType();
                }
                lineageColumnColumnService.manualAddColumnLineage(lineageColumnColumnVO.getAppType(), lineageColumnColumn,lineageColumnColumnVO.getUniqueKey(),lineageSource);
            } catch (Exception e) {
                errorCount ++;
                logger.error("acquire old columnLineage error:{},errorCount:{},lineageColumnColumnVO:{}",e,errorCount, JSON.toJSON(lineageColumnColumnVO));
            }
        }
    }

    /**
     * 手动删除字段级级血缘
     *
     * @param lineageColumnColumnVO
     */
    public void manualDeleteColumnLineage(LineageColumnColumnVO lineageColumnColumnVO) {
        Integer appType = lineageColumnColumnVO.getAppType();
        Long dtUicTenantId = lineageColumnColumnVO.getDtUicTenantId();
        LineageTableVO inputTableInfoVo = lineageColumnColumnVO.getInputTableInfo();
        LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
        LineageDataSource inputDataSource = null;
        if (Objects.isNull(inputDataSourceVO.getSourceId())) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), lineageColumnColumnVO.getDtUicTenantId(),appType);
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            inputDataSource = dataSources.get(0);
        }else {
            inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(),appType);
        }
        if (Objects.isNull(inputDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputDataSource.getId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
        LineageTableVO resultTableInfoVO = lineageColumnColumnVO.getResultTableInfo();
        LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
        LineageDataSource resultDataSource = null;
        if (Objects.isNull(inputDataSourceVO.getSourceId())) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(inputDataSourceVO.getSourceType(), inputDataSourceVO.getSourceName(), lineageColumnColumnVO.getDtUicTenantId(), appType);
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            resultDataSource = dataSources.get(0);
        }else {
            resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getSourceId(),appType);
        }
        if (Objects.isNull(resultDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName());
        LineageColumnColumn lineageColumnColumn = new LineageColumnColumn();
        lineageColumnColumn.setResultTableId(resultTableInfo.getId());
        lineageColumnColumn.setResultTableKey(resultTableInfo.getTableKey());
        lineageColumnColumn.setResultColumnName(lineageColumnColumnVO.getResultColumnName());
        lineageColumnColumn.setInputTableId(inputTableInfo.getId());
        lineageColumnColumn.setInputTableKey(inputTableInfo.getTableKey());
        lineageColumnColumn.setInputColumnName(lineageColumnColumnVO.getInputColumnName());
        lineageColumnColumn.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
        lineageColumnColumn.setAppType(lineageColumnColumnVO.getAppType());
        lineageColumnColumn.setDtUicTenantId(lineageColumnColumnVO.getDtUicTenantId());
        lineageColumnColumnService.manualDeleteColumnLineage(lineageColumnColumnVO.getAppType(), lineageColumnColumn,lineageColumnColumnVO.getUniqueKey());
    }

    /**
     * 查询表血缘上游字段列表
     * @param queryTableLineageColumnParam
     * @return
     */
    public List<String> queryTableInputLineageColumns(QueryTableLineageColumnParam queryTableLineageColumnParam){
        Long tableId = checkAndGetTableId(queryTableLineageColumnParam);
        return lineageColumnColumnService.queryTableInputLineageColumns(tableId);
    }

    /**
     * 查询表血缘下游字段列表
     * @param queryTableLineageColumnParam
     * @return
     */
    public List<String> queryTableResultLineageColumns(QueryTableLineageColumnParam queryTableLineageColumnParam){
        Long tableId = checkAndGetTableId(queryTableLineageColumnParam);
        return lineageColumnColumnService.queryTableResultLineageColumns(tableId);
    }

    private Long checkAndGetTableId(QueryTableLineageColumnParam queryTableLineageColumnParam){
        Long dtUicTenantId = queryTableLineageColumnParam.getDtUicTenantId();
        Integer appType = queryTableLineageColumnParam.getAppType();
        Integer sourceType = queryTableLineageColumnParam.getSourceType();
        String dbName = queryTableLineageColumnParam.getDbName();
        String tableName = queryTableLineageColumnParam.getTableName();
        List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(sourceType, queryTableLineageColumnParam.getSourceName(), dtUicTenantId, appType);
        if(CollectionUtils.isEmpty(dataSources)){
            logger.error("do not find need dataSource");
            throw new RdosDefineException("没有可用的数据源");
        }
        LineageDataSource lineageDataSource = dataSources.get(0);
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, dbName);
        if (Objects.isNull(dataSetInfo)){
            throw new RdosDefineException("数据集不存在");
        }
        return dataSetInfo.getId();
    }

    public List<com.dtstack.engine.api.pojo.lineage.Table> parseTables(String sql, String defaultDb, Integer sourceType) {

        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(sourceType);
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + sourceType + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlParser");
        List<com.dtstack.engine.api.pojo.lineage.Table> tableList = new ArrayList<>();
        try {
            List<Table> tables = null;
            try {
                tables = sqlParserClient.parseTables(defaultDb,sql, sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            tableList = tables.stream().map(TableAdapter::sqlTable2ApiTable).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("sql解析表失败,e:{}",e);
            return tableList;
        }
        return tableList;
    }

    /**
     * 根据taskId和appType查询表级血缘
     * @param taskId
     * @param appType
     * @return
     */
    public List<LineageTableTableVO> queryTableLineageByTaskIdAndAppType(Long taskId, Integer appType) {

        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableLineageByTaskIdAndAppType(taskId,appType);
        if(CollectionUtils.isEmpty(lineageTableTables)){
            return Lists.newArrayList();
        }
        Set<Long> tableIds = Sets.newHashSet();
        for (LineageTableTable ltt : lineageTableTables) {
            tableIds.add(ltt.getInputTableId());
            tableIds.add(ltt.getResultTableId());
        }
        List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
        Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getSourceId).collect(Collectors.toSet());
        Map<Long, LineageDataSource> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getId(), ds2 -> ds2));
        List<LineageTableTableVO> res = Lists.newArrayList();
        for (LineageTableTable tt : lineageTableTables) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(tt.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(tt.getResultTableId());
            LineageTableTableVO lineageTableTableVO = TableLineageAdapter.tableTable2TableTableVO(tt, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getSourceId()), dataSourceMap.get(resultTableInfo.getSourceId()));
            res.add(lineageTableTableVO);
        }
        return res;
    }

    public List<LineageColumnColumnVO> queryColumnLineageByTaskIdAndAppType(Long taskId, Integer appType) {

       List<LineageColumnColumn> lineageColumnColumns= lineageColumnColumnService.queryColumnLineageByTaskIdAndAppType(taskId,appType);
        if(CollectionUtils.isEmpty(lineageColumnColumns)){
            return Lists.newArrayList();
        }
        Set<Long> tableIds = Sets.newHashSet();
        for (LineageColumnColumn lcc : lineageColumnColumns) {
            tableIds.add(lcc.getInputTableId());
            tableIds.add(lcc.getResultTableId());
        }
        List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
        Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getSourceId).collect(Collectors.toSet());
        Map<Long, LineageDataSource> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getId(), ds2 -> ds2));
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageColumnColumn cc : lineageColumnColumns) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(cc.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(cc.getResultTableId());
            LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(cc, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getSourceId()), dataSourceMap.get(resultTableInfo.getSourceId()));
            res.add(columnColumnVO);
        }
        return res;
    }


    /**
     * 根据任务id和appType删除血缘
     * @param deleteLineageParam
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteLineageByTaskIdAndAppType(DeleteLineageParam deleteLineageParam) {

        try {
            lineageTableTableService.deleteLineageByTaskIdAndAppType(deleteLineageParam);
            lineageColumnColumnService.deleteLineageByTaskIdAndAppType(deleteLineageParam);
        } catch (Exception e) {
            throw new RdosDefineException("deleteLineageByTaskIdAndAppType error");
        }

    }
}
