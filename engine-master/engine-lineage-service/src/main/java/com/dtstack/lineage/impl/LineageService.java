package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.dto.DataSourceDTO;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.*;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryTableLineageColumnParam;
import com.dtstack.engine.api.vo.lineage.param.QueryTableLineageParam;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.lineage.adapter.*;
import com.dtstack.lineage.enums.SourceType2TableType;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.sqlparser.common.client.ISqlParserClient;
import com.dtstack.sqlparser.common.client.SqlParserClientCache;
import com.dtstack.sqlparser.common.client.domain.ColumnLineage;
import com.dtstack.sqlparser.common.client.domain.ParseResult;
import com.dtstack.sqlparser.common.client.domain.Table;
import com.dtstack.sqlparser.common.client.domain.TableLineage;
import com.dtstack.sqlparser.common.client.enums.TableOperateEnum;
import com.dtstack.sqlparser.common.client.exception.ClientAccessException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    @Autowired
    private LineageTableTableService lineageTableTableService;

    @Autowired
    private LineageColumnColumnService lineageColumnColumnService;

    @Autowired
    private LineageDataSourceService lineageDataSourceService;

    @Autowired
    private LineageDataSetInfoService lineageDataSetInfoService;

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
        ISqlParserClient sqlParserClient = getSqlParserClient();
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

        ISqlParserClient sqlParserClient = getSqlParserClient();
        Set<String> functions  = null;
        try {
            functions = sqlParserClient.parseFunction(sql);
        } catch (Exception e) {
            logger.error("parseFunction error:{}",e);
            throw new RdosDefineException("sql解析异常，请检查语法");
        }
        return functions;
    }

    private ISqlParserClient getSqlParserClient() {
        ISqlParserClient sqlParserClient = null;
        try {
            sqlParserClient = SqlParserClientCache.getInstance().getClient("sqlparser");
        } catch (ClientAccessException e) {
            throw new RdosDefineException("get sqlParserClient error");
        }
        if(null == sqlParserClient){
            throw new RdosDefineException("get sqlParserClient error");
        }
        return sqlParserClient;
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
        ISqlParserClient sqlParserClient = getSqlParserClient();
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
        List<LineageDataSource> dataSourceList = new ArrayList<>();
        Map<String,LineageDataSource> dataSourceMap = new HashMap<>();
        if (AppType.RDOS.getType() == appType) {
            //离线根据uic租户id和sourceType查询数据源
            try {
                dataSourceList = lineageDataSourceService.getDataSourceByParams(sourceType, null, dtUicTenantId, AppType.RDOS.getType());
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
        ISqlParserClient sqlParserClient = getSqlParserClient();
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
            Map<String, LineageDataSetInfo> tableRef = new HashMap<>();
            String tableKey = "%s.%s";
            LineageDataSource defaultDataSource = dataSourceMap.get(defaultDb);
            List<com.dtstack.engine.api.pojo.lineage.Table> tableList = tables.stream().map(TableAdapter::sqlTable2ApiTable).collect(Collectors.toList());
            Map<String, List<Column>> columns = lineageDataSetInfoService.getColumnsBySourceIdAndListTable(defaultDataSource.getId(), tableList);
            for (int i = 0; i < tables.size(); i++) {
                Table ta = tables.get(i);
                LineageDataSetInfo dataSet = null;
                if(AppType.RDOS.getType() !=appType) {
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
            List<TableLineage> tableLineages = parseResult.getTableLineages();
            if (CollectionUtils.isNotEmpty(tableLineages)) {
                List<LineageTableTable> lineageTableTables = tableLineages.stream().map(l -> {
                    LineageTableTable tableTable = TableLineageAdapter.sqlTableLineage2DbTableLineage(l, tableRef, LineageOriginType.SQL_PARSE);
                    tableTable.setDtUicTenantId(dtUicTenantId);
                    tableTable.setAppType(appType);
                    return tableTable;
                }).collect(Collectors.toList());
                //如果uniqueKey不为空，则删除相同uniqueKey的血缘
                lineageTableTableService.saveTableLineage(lineageTableTables,unionKey);
            }

        } catch (Exception e) {
            logger.error("解析保存表血缘失败：{}", e);
            throw new RdosDefineException("解析保存表血缘失败");
        }
    }

    private long addDataSource(String defaultDb, LineageDataSource defaultDataSource, String db) {
        defaultDataSource.setId(null);
        defaultDataSource.setSchemaName(db);
        String dataJson = defaultDataSource.getDataJson();
        String newDataJson = dataJson.replace(defaultDb, db);
        defaultDataSource.setDataJson(newDataJson);
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        BeanUtils.copyProperties(defaultDataSource,dataSourceDTO);
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
        ISqlParserClient sqlParserClient = getSqlParserClient();
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
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        //3.根据表名和数据库名，数据库id查询表。表不存在则需要插入表
        //4.获取表中的字段列表
        //5.解析字段级血缘关系
        //6.存储字段级血缘关系
        LineageDataSource lineageDataSource = null;
        List<LineageDataSource> dataSourceList = new ArrayList<>();
        Map<String,LineageDataSource> dataSourceMap = new HashMap<>();
        if (AppType.RDOS.getType() == parseColumnLineageParam.getAppType()) {
            dataSourceList = lineageDataSourceService.getDataSourceByParams(parseColumnLineageParam.getDataSourceType(), null, parseColumnLineageParam.getDtUicTenantId(), AppType.RDOS.getType());
            if(CollectionUtils.isEmpty(dataSourceList)){
                logger.error("do not find need ");
                throw new RdosDefineException("没有可用的数据源");
            }
            lineageDataSource = dataSourceList.get(0);
            for (LineageDataSource dataSource : dataSourceList) {
                dataSourceMap.put(dataSource.getSchemaName(),dataSource);
            }
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
        ISqlParserClient sqlParserClient = getSqlParserClient();
        try {
            List<Table> resTables = null;
            try {
                resTables = sqlParserClient.parseTables(parseColumnLineageParam.getDefaultDb(),parseColumnLineageParam.getSql(),sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            if(AppType.RDOS.getType() == parseColumnLineageParam.getAppType()) {
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
            ParseResult parseResult = null;
            try {
                parseResult = sqlParserClient.parseSql(parseColumnLineageParam.getSql(), parseColumnLineageParam.getDefaultDb(), sqlTableColumnMap,sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            //3.根据表名和数dbName，schemaName查询表,sourceId。表不存在则需要插入表
            Map<String, LineageDataSetInfo> tableRef = new HashMap<>();
            String tableKey = "%s.%s";
            for (int i = 0; i < resTables.size(); i++) {
                Table ta = resTables.get(i);
                LineageDataSetInfo dataSet = null;
                if( AppType.RDOS.getType() != parseColumnLineageParam.getAppType()) {
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
                    //如果uniqueKey不为空，则删除相同uniqueKey的血缘
                    lineageTableTableService.saveTableLineage(lineageTableTables,parseColumnLineageParam.getUniqueKey());
                }
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            List<ColumnLineage> columnLineages = parseResult.getColumnLineages();
            if (CollectionUtils.isNotEmpty(columnLineages)) {
                lineageColumnColumnService.saveColumnLineage(columnLineages.stream().map(cl -> ColumnLineageAdapter.sqlColumnLineage2ColumnColumn(cl, parseColumnLineageParam.getAppType(), tableRef)).collect(Collectors.toList()),parseColumnLineageParam.getUniqueKey());
            }

        } catch (Exception e) {
            logger.error("解析保存表血缘失败：{}", e);
            throw new RdosDefineException("解析保存字段血缘失败");
        }
    }

    /**
     * 查询表上游表血缘
     *
     * @return
     */
    public List<LineageTableTableVO> queryTableInputLineage(QueryTableLineageParam queryTableLineageParam) {
        Long dtUicTenantId = queryTableLineageParam.getDtUicTenantId();
        Integer appType = queryTableLineageParam.getAppType();
        Long engineSourceId = queryTableLineageParam.getEngineSourceId();
        String schemaName = queryTableLineageParam.getSchemaName();
        Integer sourceType = queryTableLineageParam.getSourceType();
        String dbName = queryTableLineageParam.getDbName();
        String tableName = queryTableLineageParam.getTableName();
        LineageDataSource lineageDataSource = null;
        if (Objects.isNull(engineSourceId)) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(sourceType, queryTableLineageParam.getSourceName(), dtUicTenantId, queryTableLineageParam.getAppType());
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            lineageDataSource = dataSources.get(0);
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableInputLineageByAppType(dataSetInfo.getId(), appType,new HashSet<>());
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
     * 查询表下游表血缘
     *
     * @return
     */
    public List<LineageTableTableVO> queryTableResultLineage(QueryTableLineageParam queryTableLineageParam) {
        Long dtUicTenantId = queryTableLineageParam.getDtUicTenantId();
        Integer appType = queryTableLineageParam.getAppType();
        Long engineSourceId = queryTableLineageParam.getEngineSourceId();
        String schemaName = queryTableLineageParam.getSchemaName();
        Integer sourceType = queryTableLineageParam.getSourceType();
        String dbName = queryTableLineageParam.getDbName();
        String tableName = queryTableLineageParam.getTableName();
        LineageDataSource lineageDataSource = null;
        if (Objects.isNull(engineSourceId)) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(sourceType, queryTableLineageParam.getSourceName(), dtUicTenantId, queryTableLineageParam.getAppType());
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            lineageDataSource = dataSources.get(0);
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableResultLineageByAppType(dataSetInfo.getId(), appType,new HashSet<>());
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
        Long dtUicTenantId = queryTableLineageParam.getDtUicTenantId();
        Integer appType = queryTableLineageParam.getAppType();
        Long engineSourceId = queryTableLineageParam.getEngineSourceId();
        String schemaName = queryTableLineageParam.getSchemaName();
        Integer sourceType = queryTableLineageParam.getSourceType();
        String dbName = queryTableLineageParam.getDbName();
        String tableName = queryTableLineageParam.getTableName();
        LineageDataSource lineageDataSource = null;
        if (Objects.isNull(engineSourceId)) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(sourceType, queryTableLineageParam.getSourceName(), dtUicTenantId, queryTableLineageParam.getAppType());
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            lineageDataSource = dataSources.get(0);
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableTableByTableAndAppId(appType, dataSetInfo.getId());
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
        //FIXME 暂未考虑性能
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
                    dataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(),appType);
                }
                if (Objects.isNull(dataSource)){
                    continue;
    //                throw new RdosDefineException("数据源不存在");
                }
                LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputTableInfoVo.getDataSourceVO().getSourceId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
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
                lineageTableTable.setLineageSource(LineageOriginType.SQL_PARSE.getType());
                lineageTableTable.setAppType(tableTableVO.getAppType());
                lineageTableTable.setDtUicTenantId(tableTableVO.getDtUicTenantId());
                Integer isManual = null;
                if (Objects.nonNull(tableTableVO.getManual()) && tableTableVO.getManual()){
                    isManual = LineageOriginType.MANUAL_ADD.getType();
                }
                lineageTableTableService.manualAddTableLineage(tableTableVO.getAppType(), lineageTableTable,tableTableVO.getUniqueKey(),isManual);
            } catch (Exception e) {
                logger.error("",e);
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

    /**
     * 查询字段上游字段血缘
     *
     * @return
     */
    public List<LineageColumnColumnVO> queryColumnInputLineage(QueryColumnLineageParam queryColumnLineageParam) {
        Long dtUicTenantId = queryColumnLineageParam.getDtUicTenantId();
        Integer appType = queryColumnLineageParam.getAppType();
        Long engineSourceId = queryColumnLineageParam.getEngineSourceId();
        String schemaName = queryColumnLineageParam.getSchemaName();
        Integer sourceType = queryColumnLineageParam.getSourceType();
        String dbName = queryColumnLineageParam.getDbName();
        String tableName = queryColumnLineageParam.getTableName();
        String columnName = queryColumnLineageParam.getColumnName();
        LineageDataSource lineageDataSource = null;
        if (Objects.isNull(engineSourceId)) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(queryColumnLineageParam.getSourceType(), queryColumnLineageParam.getSourceName(), queryColumnLineageParam.getDtUicTenantId(), queryColumnLineageParam.getAppType());
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            lineageDataSource = dataSources.get(0);
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnInputLineageByAppType(appType, dataSetInfo.getId(), columnName,new HashSet<>());
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
        Long dtUicTenantId = queryColumnLineageParam.getDtUicTenantId();
        Integer appType = queryColumnLineageParam.getAppType();
        Long engineSourceId = queryColumnLineageParam.getEngineSourceId();
        String schemaName = queryColumnLineageParam.getSchemaName();
        Integer sourceType = queryColumnLineageParam.getSourceType();
        String dbName = queryColumnLineageParam.getDbName();
        String tableName = queryColumnLineageParam.getTableName();
        String columnName = queryColumnLineageParam.getColumnName();
        LineageDataSource lineageDataSource = null;
        if (Objects.isNull(engineSourceId)) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(queryColumnLineageParam.getSourceType(), queryColumnLineageParam.getSourceName(), queryColumnLineageParam.getDtUicTenantId(), queryColumnLineageParam.getAppType());
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            lineageDataSource = dataSources.get(0);
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnResultLineageByAppType(appType, dataSetInfo.getId(), columnName,new HashSet<>());
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
        Long dtUicTenantId = queryColumnLineageParam.getDtUicTenantId();
        Integer appType = queryColumnLineageParam.getAppType();
        Long engineSourceId = queryColumnLineageParam.getEngineSourceId();
        String schemaName = queryColumnLineageParam.getSchemaName();
        Integer sourceType = queryColumnLineageParam.getSourceType();
        String dbName = queryColumnLineageParam.getDbName();
        String tableName = queryColumnLineageParam.getTableName();
        String columnName = queryColumnLineageParam.getColumnName();
        LineageDataSource lineageDataSource = null;
        if (Objects.isNull(engineSourceId)) {
            List<LineageDataSource> dataSources = lineageDataSourceService.getDataSourceByParams(queryColumnLineageParam.getSourceType(), queryColumnLineageParam.getSourceName(), queryColumnLineageParam.getDtUicTenantId(), queryColumnLineageParam.getAppType());
            if(CollectionUtils.isEmpty(dataSources)){
                logger.error("do not find need dataSource");
                throw new RdosDefineException("没有可用的数据源");
            }
            lineageDataSource = dataSources.get(0);
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        if (Objects.isNull(lineageDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnLineages(appType, dataSetInfo.getId(), columnName);
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

    public void acquireOldColumnColumn(List<LineageColumnColumnVO> lineageTableTableVOs){
        if (lineageTableTableVOs.size()>200){
            throw new RdosDefineException("请分批执行");
        }
        //FIXME 优化性能
        for (LineageColumnColumnVO lineageColumnColumnVO:lineageTableTableVOs){
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
                    inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getSourceId(),appType);
                }
                if (Objects.isNull(inputDataSource)){
                    continue;
    //                throw new RdosDefineException("数据源不存在");
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
                lineageColumnColumn.setLineageSource(LineageOriginType.SQL_PARSE.getType());
                lineageColumnColumn.setAppType(lineageColumnColumnVO.getAppType());
                lineageColumnColumn.setDtUicTenantId(lineageColumnColumnVO.getDtUicTenantId());
                if (Objects.nonNull(lineageColumnColumnVO.getManual()) && !lineageColumnColumnVO.getManual()){
                    lineageColumnColumn.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
                    lineageSource = LineageOriginType.SQL_PARSE.getType();
                }
                lineageColumnColumnService.manualAddColumnLineage(lineageColumnColumnVO.getAppType(), lineageColumnColumn,lineageColumnColumnVO.getUniqueKey(),lineageSource);
            } catch (Exception e) {
                logger.error("",e);
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
        ISqlParserClient sqlParserClient = getSqlParserClient();
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


    public void deleteTableLineageByTaskId(Long taskId,Integer appType){

    }
}
