package com.dtstack.engine.lineage.impl;

import com.alibaba.fastjson.JSON;
import com.dtstack.dtcenter.common.pager.PageResult;
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
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.lineage.adapter.*;
import com.dtstack.engine.lineage.enums.SourceType2TableType;
import com.dtstack.engine.lineage.util.SqlParserClientOperator;
import com.dtstack.engine.dao.LineageColumnColumnUniqueKeyRefDao;
import com.dtstack.engine.dao.LineageTableTableUniqueKeyRefDao;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.datasource.DsServiceListParam;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceListDTO;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sqlparser.common.client.ISqlParserClient;
import com.dtstack.sqlparser.common.client.domain.AlterResult;
import com.dtstack.sqlparser.common.client.domain.ColumnLineage;
import com.dtstack.sqlparser.common.client.domain.ParseResult;
import com.dtstack.sqlparser.common.client.domain.Table;
import com.dtstack.sqlparser.common.client.domain.TableLineage;
import com.dtstack.sqlparser.common.client.enums.SqlType;
import com.dtstack.sqlparser.common.client.enums.TableOperateEnum;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;


    @PostConstruct
    public void setUp(){
        System.setProperty("sqlParser.dir",env.getSqlParserDir());
    }


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
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
        parseInfo.setOriginSql(sql);
        try {
            ParseResult parseResult = null;
            try {
                parseResult = sqlParserClient.parseSql(sql, defaultDb, new HashMap<>(),sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException(e);
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
            throw new RdosDefineException(e.getMessage(),ErrorCode.SQLPARSE_ERROR);
        }
        return parseInfo;
    }

    public Set<String> parseFunction(String sql){
        List<String> sqlList = SqlFormatUtil.splitSqlWithoutSemi(sql);
        Set<String> functionList = new HashSet<>();
        for (String s : sqlList) {
            ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
            try {
                Set<String> functions = sqlParserClient.parseFunction(s);
                functionList.addAll(functions);
            } catch (Exception e) {
                logger.error("parseFunction error:{}",e);
            }
        }
        return functionList;
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
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
        TableLineageParseInfo parseInfo = new TableLineageParseInfo();
        try {
            ParseResult parseResult = null;
            try {
                parseResult = sqlParserClient.parseTableLineage(sql, defaultDb,sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("解析sql异常:{}",e);
                throw new RdosDefineException(e);
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
            throw new RdosDefineException(e.getMessage(),ErrorCode.SQLPARSE_ERROR);
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
        DsServiceListDTO dsServiceListDTO = null;
        DsServiceInfoDTO dsServiceInfoDTO = new DsServiceInfoDTO();
        if (AppType.RDOS.getType().equals(appType)) {
            //从数据源中心查询meta数据源 todo
            DsServiceListParam dsServiceListParam = getDsServiceListParam(dtUicTenantId, sourceType,-1L);
            ApiResponse<PageResult<List<DsServiceListDTO>>> pageResultApiResponse = dataSourceAPIClient.appDsPage(dsServiceListParam);
            if(pageResultApiResponse.getCode() !=1 ){
                logger.error("appDsPage query failed,param:{}",JSON.toJSONString(dsServiceListParam));
                throw new RdosDefineException("调用数据源中心查询已引入接口失败");
            }
            List<DsServiceListDTO> data = pageResultApiResponse.getData().getData();
            if(data.size()<1){
                logger.error("do not find need dataSource,param:{}",JSON.toJSONString(dsServiceListParam));
                throw new RdosDefineException("没有可用的数据源");
            }
             dsServiceListDTO = data.get(0);
             BeanUtils.copyProperties(dsServiceListDTO,dsServiceInfoDTO);
        } else {
            //资产通过数据源中心id查询数据源
            ApiResponse<DsServiceInfoDTO> dsInfoById = dataSourceAPIClient.getDsInfoById(engineSourceId);
            if(dsInfoById.getCode() != 1){
                logger.error("getDsInfoById query failed,param:{}",JSON.toJSONString(engineSourceId));
                throw new RdosDefineException("调用数据源中心根据id查询数据源接口失败");
            }
            dsServiceInfoDTO = dsInfoById.getData();
        }
        if(null == dsServiceInfoDTO){
            throw new RdosDefineException("dataSource center do not have this dataSource}");
        }
        //解析出sql中的表
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(sourceType);
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + sourceType + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
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
            Map<String, LineageDataSetInfo> tableRef = getTableRef(appType, defaultDb, dsServiceInfoDTO, tables);
            saveTableLineage(dtUicTenantId, appType, unionKey, parseResult, tableRef);

        } catch (Exception e) {
            logger.error("解析保存表血缘失败：{}", e);
            throw new RdosDefineException("解析保存表血缘失败");
        }
    }

    private DsServiceListParam getDsServiceListParam(Long dtUicTenantId, Integer sourceType,Long projectId) {
        DsServiceListParam dsServiceListParam = new DsServiceListParam();
        dsServiceListParam.setDsDtuicTenantId(dtUicTenantId);
        dsServiceListParam.setAppType(AppType.RDOS.getType());
        dsServiceListParam.setIsMeta(1);
        List<Integer> dataTypeCodeList = new ArrayList<>();
        dataTypeCodeList.add(sourceType);
        dsServiceListParam.setDataTypeCodeList(dataTypeCodeList);
        dsServiceListParam.setProjectId(projectId);
        return dsServiceListParam;
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
            lineageTableTableService.saveTableLineage(null,null,lineageTableTables, unionKey);
        }
    }

    public Map<String, LineageDataSetInfo> getTableRef(Integer appType, String defaultDb, DsServiceInfoDTO dsServiceInfoDTO, List<Table> tables) {
        Map<String, LineageDataSetInfo> tableRef = new HashMap<>();
        String tableKey = "%s.%s";
        List<com.dtstack.engine.api.pojo.lineage.Table> tableList = tables.stream().map(TableAdapter::sqlTable2ApiTable).collect(Collectors.toList());
        Map<String, List<Column>> columns = lineageDataSetInfoService.getColumnsBySourceIdAndListTable(dsServiceInfoDTO.getDataInfoId(), tableList);
        for (int i = 0; i < tables.size(); i++) {
            Table ta = tables.get(i);
            LineageDataSetInfo dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dsServiceInfoDTO.getDataInfoId(), ta.getDb(), ta.getName(), ta.getDb(),appType);
            tableRef.put(String.format(tableKey, ta.getDb(), ta.getName()), dataSet);
        }
        return tableRef;
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
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
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
                throw new RdosDefineException(e);
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
            throw new RdosDefineException(e.getMessage(), ErrorCode.SQLPARSE_ERROR);
        }

        return parseInfo;
    }

    /**
     * 解析并存储字段级血缘
     */
    @Async
    public void parseAndSaveColumnLineage(ParseColumnLineageParam parseColumnLineageParam) {

        logger.info("into parseAndSaveColumnLineage method:{}",JSON.toJSON(parseColumnLineageParam));
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        //3.根据表名和数据库名，数据库id查询表。表不存在则需要插入表
        //4.获取表中的字段列表
        //5.解析字段级血缘关系
        //6.存储字段级血缘关系
        DsServiceListDTO dsServiceListDTO = null;
        DsServiceInfoDTO dsServiceInfoDTO = new DsServiceInfoDTO();
        if (AppType.RDOS.getType().equals(parseColumnLineageParam.getAppType())) {
            //从数据源中心查询meta数据源 todo
            DsServiceListParam dsServiceListParam = getDsServiceListParam(parseColumnLineageParam.getDtUicTenantId(), parseColumnLineageParam.getDataSourceType(),parseColumnLineageParam.getProjectId());
            ApiResponse<PageResult<List<DsServiceListDTO>>> pageResultApiResponse = dataSourceAPIClient.appDsPage(dsServiceListParam);
            if(pageResultApiResponse.getCode() !=1 ){
                logger.error("appDsPage query failed,param:{}",JSON.toJSONString(dsServiceListParam));
                throw new RdosDefineException("调用数据源中心查询已引入接口失败");
            }
            List<DsServiceListDTO> data = pageResultApiResponse.getData().getData();
            if(data.size()<1){
                logger.error("do not find need dataSource,param:{}",JSON.toJSONString(dsServiceListParam));
                throw new RdosDefineException("没有可用的数据源");
            }
            dsServiceListDTO = data.get(0);
            BeanUtils.copyProperties(dsServiceListDTO,dsServiceInfoDTO);
        } else {
            //资产通过数据源中心id查询数据源
            ApiResponse<DsServiceInfoDTO> dsInfoById = dataSourceAPIClient.getDsInfoById(parseColumnLineageParam.getDataInfoId());
            if(dsInfoById.getCode() != 1){
                logger.error("getDsInfoById query failed,param:{}",JSON.toJSONString(parseColumnLineageParam.getDataInfoId()));
                throw new RdosDefineException("调用数据源中心根据id查询数据源接口失败");
            }
            dsServiceInfoDTO = dsInfoById.getData();
        }
        if(null == dsServiceInfoDTO){
            throw new RdosDefineException("dataSource center do not have this dataSource}");
        }
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(dsServiceInfoDTO.getType());
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + dsServiceInfoDTO.getType() + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
        try {
            List<Table> resTables = null;
            try {
                resTables = sqlParserClient.parseTables(parseColumnLineageParam.getDefaultDb(),parseColumnLineageParam.getSql(),sourceType2TableType.getTableType());
            } catch (Exception e) {
                logger.error("parse sql error",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            //去除主表，主表需要创建，还未存在，查不到字段信息，需要过滤掉
            List<Table> subTables = resTables.stream().filter(table->
                    table.getOperate() != TableOperateEnum.CREATE ).collect(Collectors.toList());
            Set<com.dtstack.engine.api.pojo.lineage.Table> tables = subTables.stream().map(TableAdapter::sqlTable2ApiTable).collect(Collectors.toSet());
            Set<String> dbSets = new HashSet<>();
            resTables.stream().forEach(table -> {
                String db = table.getDb();
                dbSets.add(db);
            });
            if(dbSets.size()>1){
                //跨schema查询
                return;
            }
            //获取表字段信息
            Map<String, List<Column>> tableColumnMap = lineageDataSetInfoService.getColumnsBySourceIdAndListTable(dsServiceInfoDTO.getDataInfoId(), Lists.newArrayList(tables));
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
                logger.error("parse sql error",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            if(handleDropTableAndAlterRename(dsServiceInfoDTO, parseResult,parseColumnLineageParam.getAppType())){
                return;
            }
            //3.根据表名和数dbName，schemaName查询表,sourceId。表不存在则需要插入表
            Map<String, LineageDataSetInfo> tableRef = new HashMap<>();
            String tableKey = "%s.%s";
            for (int i = 0; i < resTables.size(); i++) {
                Table ta = resTables.get(i);
                LineageDataSetInfo dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dsServiceInfoDTO.getDataInfoId(), ta.getDb(), ta.getName(), ta.getDb(),parseColumnLineageParam.getAppType());
                tableRef.put(String.format(tableKey, ta.getDb(), ta.getName()), dataSet);
            }
            try {
                ParseResult parseTableLineage = sqlParserClient.parseTableLineage(parseColumnLineageParam.getSql(), parseColumnLineageParam.getDefaultDb(),sourceType2TableType.getTableType());
                List<TableLineage> tableLineages = parseTableLineage.getTableLineages();
                if (CollectionUtils.isNotEmpty(tableLineages)) {
                    List<LineageTableTable> lineageTableTables = tableLineages.stream().map(l -> TableLineageAdapter.sqlTableLineage2DbTableLineage(l, tableRef, LineageOriginType.SQL_PARSE)).collect(Collectors.toList());
                    logger.info("lineageTableTables为:{}",JSON.toJSON(lineageTableTables));
                    //如果uniqueKey不为空，则删除相同uniqueKey的血缘
                    lineageTableTableService.saveTableLineage(parseColumnLineageParam.getVersionId(),parseColumnLineageParam.getType(),lineageTableTables,parseColumnLineageParam.getUniqueKey());
                }
            } catch (Exception e) {
                logger.error("parse parseTableLineage error",e);
                throw new RdosDefineException("sql解析异常，请检查语法");
            }
            List<ColumnLineage> columnLineages = parseResult.getColumnLineages();
            if (CollectionUtils.isNotEmpty(columnLineages)) {
                lineageColumnColumnService.saveColumnLineage(parseColumnLineageParam.getVersionId(),parseColumnLineageParam.getType(),columnLineages.stream().map(cl -> ColumnLineageAdapter.sqlColumnLineage2ColumnColumn(cl, parseColumnLineageParam.getAppType(), tableRef)).collect(Collectors.toList()),parseColumnLineageParam.getUniqueKey());
            }

        } catch (Exception e) {
            logger.error("parseAndSaveColumnLineage error", e);
            throw new RdosDefineException("解析保存字段血缘失败");
        }
    }

    public Boolean handleDropTableAndAlterRename(DsServiceInfoDTO dsServiceInfoDTO, ParseResult parseResult,Integer appType) {
        SqlType sqlType = parseResult.getSqlType();
        if(sqlType.getType().equals(com.dtstack.engine.api.vo.lineage.SqlType.DROP.getType())){
            //drop表操作，需要删除对应的血缘
            Table mainTable = parseResult.getMainTable();
            String db = mainTable.getDb();
            String tableName = mainTable.getName();
            LineageDataSetInfo dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dsServiceInfoDTO.getDataInfoId(), db, tableName, db,appType);
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
            lineageDataSetInfoService.updateTableNameByTableNameAndSourceId(oldTableName,newTableName,oldDB,dsServiceInfoDTO.getDataInfoId());
            return true;
        }else{
            return false;
        }
    }

    private DsServiceInfoDTO getDataSource(QueryTableLineageParam queryTableLineageParam){
        Integer appType = queryTableLineageParam.getAppType();
        ApiResponse<DsServiceInfoDTO> dsInfoById = dataSourceAPIClient.getDsInfoById(queryTableLineageParam.getDataInfoId());
        if(dsInfoById.getCode() != 1){
            logger.error("getDsInfoById query failed,param:{}",JSON.toJSONString(queryTableLineageParam.getDataInfoId()));
            throw new RdosDefineException("调用数据源中心根据id查询数据源接口失败");
        }
        DsServiceInfoDTO data = dsInfoById.getData();
        if(null == data){
            throw new RdosDefineException("dataSource center do not have this dataSource}");
        }
        return data;
    }

    /**
     * 查询表上游表血缘数量和层数
     *
     * @return
     */
    public LevelAndCount queryTableInputLineageCountAndLevel(QueryTableLineageParam queryTableLineageParam) {

        Integer appType = queryTableLineageParam.getAppType();
        DsServiceInfoDTO dsServiceInfoDTO = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName();
        String dbName = queryTableLineageParam.getDbName() ;
        String tableName = queryTableLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dsServiceInfoDTO.getDataInfoId(), dbName, tableName, schemaName,appType);
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
        DsServiceInfoDTO dsServiceInfoDTO = getDataSource(queryTableLineageParam);
        //todo dbName是离线和资产传还是通过解析jdbcUrl来获取

        String schemaName = queryTableLineageParam.getSchemaName() ;
        String dbName = queryTableLineageParam.getDbName();
        String tableName = queryTableLineageParam.getTableName();
        List<LineageDataSetInfo> dataSetInfoList = lineageDataSetInfoService.getListByParams(dsServiceInfoDTO.getDataInfoId(), dbName, tableName, schemaName,appType);
        if(CollectionUtils.isEmpty(dataSetInfoList)){
            return ListUtils.EMPTY_LIST;
        }
        List<LineageTableTableVO> res = Lists.newArrayList();
        for (LineageDataSetInfo dataSetInfo : dataSetInfoList) {
            LevelAndCount levelAndCount = new LevelAndCount();
            levelAndCount.setLevelCount(queryTableLineageParam.getLevel());
            List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableInputLineageByAppType(dataSetInfo.getId(), appType,new HashSet<>(),levelAndCount);
            if(CollectionUtils.isEmpty(lineageTableTables)){
                continue;
            }
            Set<Long> tableIds = Sets.newHashSet();
            for (LineageTableTable ltt : lineageTableTables) {
                tableIds.add(ltt.getInputTableId());
                tableIds.add(ltt.getResultTableId());
            }
            List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
            Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
            Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getDataInfoId).collect(Collectors.toSet());
            Map<Long, DsServiceInfoDTO> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getDataInfoId(), ds2 -> ds2));
            for (LineageTableTable tt : lineageTableTables) {
                LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(tt.getInputTableId());
                LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(tt.getResultTableId());
                LineageTableTableVO lineageTableTableVO = TableLineageAdapter.tableTable2TableTableVO(tt, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getDataInfoId()), dataSourceMap.get(resultTableInfo.getDataInfoId()));
                res.add(lineageTableTableVO);
            }
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
        DsServiceInfoDTO dsServiceInfoDTO = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName();
        String dbName = queryTableLineageParam.getDbName() ;
        String tableName = queryTableLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dsServiceInfoDTO.getDataInfoId(), dbName, tableName, schemaName,appType);
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
        DsServiceInfoDTO dsServiceInfoDTO = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName() ;
        String dbName = queryTableLineageParam.getDbName() ;
        String tableName = queryTableLineageParam.getTableName();
        // 手动添加的表无法查看血缘关系
        LevelAndCount lv = new LevelAndCount();
        lv.setLevelCount(queryTableLineageParam.getLevel());
        List<LineageDataSetInfo> dataSetInfoList = lineageDataSetInfoService.getListByParams(dsServiceInfoDTO.getDataInfoId(), dbName, tableName, schemaName,appType);
        if(CollectionUtils.isEmpty(dataSetInfoList)){
            return ListUtils.EMPTY_LIST;
        }
        List<LineageTableTableVO> res = Lists.newArrayList();
        for (LineageDataSetInfo dataSetInfo : dataSetInfoList) {
            List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableResultLineageByAppType(dataSetInfo.getId(), appType,new HashSet<>(),lv);
            if(CollectionUtils.isEmpty(lineageTableTables)){
                continue;
            }
            Set<Long> tableIds = Sets.newHashSet();
            for (LineageTableTable ltt : lineageTableTables) {
                tableIds.add(ltt.getInputTableId());
                tableIds.add(ltt.getResultTableId());
            }
            List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
            Map<Long, LineageDataSetInfo> dataSetMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
            Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getDataInfoId).collect(Collectors.toSet());
            Map<Long, DsServiceInfoDTO> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getDataInfoId(), ds2 -> ds2));
            for (LineageTableTable tt : lineageTableTables) {
                LineageDataSetInfo inputTableInfo = dataSetMap.get(tt.getInputTableId());
                LineageDataSetInfo resultTableInfo = dataSetMap.get(tt.getResultTableId());
                LineageTableTableVO lineageTableTableVO = TableLineageAdapter.tableTable2TableTableVO(tt, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getDataInfoId()), dataSourceMap.get(resultTableInfo.getDataInfoId()));
                res.add(lineageTableTableVO);
            }
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
        DsServiceInfoDTO dsServiceInfoDTO = getDataSource(queryTableLineageParam);
        String schemaName = queryTableLineageParam.getSchemaName() ;
        String dbName = queryTableLineageParam.getDbName() ;
        String tableName = queryTableLineageParam.getTableName();
        //手动添加的表无法查看血缘关系
        List<LineageDataSetInfo> dataSetInfoList = lineageDataSetInfoService.getListByParams(dsServiceInfoDTO.getDataInfoId(), dbName, tableName, schemaName,appType);
        if(CollectionUtils.isEmpty(dataSetInfoList)){
            return ListUtils.EMPTY_LIST;
        }
        List<LineageTableTableVO> res = Lists.newArrayList();
        for (LineageDataSetInfo dataSetInfo : dataSetInfoList) {
            List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableTableByTableAndAppId(appType, dataSetInfo.getId(),queryTableLineageParam.getLevel());
            if(CollectionUtils.isEmpty(lineageTableTables)){
                continue;
            }
            Set<Long> tableIds = Sets.newHashSet();
            for (LineageTableTable ltt : lineageTableTables) {
                tableIds.add(ltt.getInputTableId());
                tableIds.add(ltt.getResultTableId());
            }
            List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
            Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
            Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getDataInfoId).collect(Collectors.toSet());
            Map<Long, DsServiceInfoDTO> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getDataInfoId(), ds2 -> ds2));
            for (LineageTableTable tt : lineageTableTables) {
                LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(tt.getInputTableId());
                LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(tt.getResultTableId());
                LineageTableTableVO lineageTableTableVO = TableLineageAdapter.tableTable2TableTableVO(tt, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getDataInfoId()), dataSourceMap.get(resultTableInfo.getDataInfoId()));
                res.add(lineageTableTableVO);
            }
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
            DsServiceInfoDTO dataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getDataInfoId(),appType);

            if (Objects.isNull(dataSource)){
                throw new RdosDefineException("数据源不存在");
            }
            LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSource.getDataInfoId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName(),appType);
            LineageTableVO resultTableInfoVO = tableTableVO.getResultTableInfo();
            LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
            DsServiceInfoDTO resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getDataInfoId(),appType);

            if (Objects.isNull(resultDataSource)){
                throw new RdosDefineException("数据源不存在");
            }
            LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getDataInfoId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName(),appType);
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
                DsServiceInfoDTO dataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getDataInfoId(), appType);
                if (Objects.isNull(dataSource)){
                    continue;
    //                throw new RdosDefineException("数据源不存在");
                }
                String inputDbName = inputTableInfoVo.getDbName();
                LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName( dataSource.getDataInfoId(), inputDbName, inputTableInfoVo.getTableName(), inputDbName,appType);
                LineageTableVO resultTableInfoVO = tableTableVO.getResultTableInfo();
                LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
                DsServiceInfoDTO resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getDataInfoId(), appType);

                if (Objects.isNull(resultDataSource)){
                    throw new RdosDefineException("数据源不存在");
                }
                String resultDbName = resultTableInfoVO.getDbName() ;
                LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getDataInfoId(), resultDbName, resultTableInfoVO.getTableName(),resultDbName,appType);
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
        DsServiceInfoDTO inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getDataInfoId(),appType);
        if (Objects.isNull(inputDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputDataSource.getDataInfoId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName(),appType);
        LineageTableVO resultTableInfoVO = tableTableVO.getResultTableInfo();
        LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
        DsServiceInfoDTO  resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getDataInfoId(),appType);
        if (Objects.isNull(resultDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getDataInfoId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName(),appType);
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

    private DsServiceInfoDTO getDataSourceByColumnParam(QueryColumnLineageParam queryColumnLineageParam){

        ApiResponse<DsServiceInfoDTO> dsInfoById = dataSourceAPIClient.getDsInfoById(queryColumnLineageParam.getDataInfoId());
        if(dsInfoById.getCode() != 1){
            logger.error("getDsInfoById query failed,param:{}",JSON.toJSONString(queryColumnLineageParam.getDataInfoId()));
            throw new RdosDefineException("调用数据源中心根据id查询数据源接口失败");
        }
        DsServiceInfoDTO data = dsInfoById.getData();
        if(null == data){
            throw new RdosDefineException("dataSource center do not have this dataSource}");
        }
        return data;
    }


    /**
     * 查询字段上游字段血缘
     * @return
     */
    public List<LineageColumnColumnVO> queryColumnInputLineage(QueryColumnLineageParam queryColumnLineageParam) {

        Integer appType = queryColumnLineageParam.getAppType();
        DsServiceInfoDTO lineageDataSource = getDataSourceByColumnParam(queryColumnLineageParam);
        String schemaName = queryColumnLineageParam.getSchemaName();
        String dbName = queryColumnLineageParam.getDbName() ;
        String columnName = queryColumnLineageParam.getColumnName();
        String tableName = queryColumnLineageParam.getTableName();
        // 手动添加的表无法查看血缘关系
        List<LineageDataSetInfo> dataSetInfoList = lineageDataSetInfoService.getListByParams(lineageDataSource.getDataInfoId(), dbName, tableName, schemaName,appType);
        if(CollectionUtils.isEmpty(dataSetInfoList)){
            return ListUtils.EMPTY_LIST;
        }
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageDataSetInfo dataSetInfo : dataSetInfoList) {
            List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnInputLineageByAppType(appType, dataSetInfo.getId(), columnName,new HashSet<>(),queryColumnLineageParam.getLevel());
            if(CollectionUtils.isEmpty(lineageColumnColumns)){
                continue ;
            }
            Set<Long> tableIds = Sets.newHashSet();
            for (LineageColumnColumn lcc : lineageColumnColumns) {
                tableIds.add(lcc.getInputTableId());
                tableIds.add(lcc.getResultTableId());
            }
            List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
            Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
            Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getDataInfoId).collect(Collectors.toSet());
            Map<Long, DsServiceInfoDTO> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getDataInfoId(), ds2 -> ds2));
            for (LineageColumnColumn cc : lineageColumnColumns) {
                LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(cc.getInputTableId());
                LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(cc.getResultTableId());
                LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(cc, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getDataInfoId()), dataSourceMap.get(resultTableInfo.getDataInfoId()));
                res.add(columnColumnVO);
            }
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
        DsServiceInfoDTO lineageDataSource = getDataSourceByColumnParam(queryColumnLineageParam);
        String schemaName = queryColumnLineageParam.getSchemaName();
        String dbName = queryColumnLineageParam.getDbName();
        String columnName = queryColumnLineageParam.getColumnName();
        String tableName = queryColumnLineageParam.getTableName();
        // 手动添加的表无法查看血缘关系
        List<LineageDataSetInfo> dataSetInfoList = lineageDataSetInfoService.getListByParams(lineageDataSource.getDataInfoId(), dbName, tableName, schemaName,appType);
        if(CollectionUtils.isEmpty(dataSetInfoList)){
            return ListUtils.EMPTY_LIST;
        }
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageDataSetInfo dataSetInfo : dataSetInfoList) {
            List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnResultLineageByAppType(appType, dataSetInfo.getId(), columnName,new HashSet<>(),queryColumnLineageParam.getLevel());
            if(CollectionUtils.isEmpty(lineageColumnColumns)){
                continue;
            }
            Set<Long> tableIds = Sets.newHashSet();
            for (LineageColumnColumn lcc : lineageColumnColumns) {
                tableIds.add(lcc.getInputTableId());
                tableIds.add(lcc.getResultTableId());
            }
            List<LineageDataSetInfo> dataSetListByIds = lineageDataSetInfoService.getDataSetListByIds(Lists.newArrayList(tableIds));
            Map<Long, LineageDataSetInfo> dataSetInfoMap = dataSetListByIds.stream().collect(Collectors.toMap(dataSetInfo1 -> dataSetInfo1.getId(), dataSetInfo12 -> dataSetInfo12));
            Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getDataInfoId).collect(Collectors.toSet());
            Map<Long, DsServiceInfoDTO> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getDataInfoId(), ds2 -> ds2));
            for (LineageColumnColumn cc : lineageColumnColumns) {
                LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(cc.getInputTableId());
                LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(cc.getResultTableId());
                LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(cc, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getDataInfoId()), dataSourceMap.get(resultTableInfo.getDataInfoId()));
                res.add(columnColumnVO);
            }
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
        DsServiceInfoDTO lineageDataSource = getDataSourceByColumnParam(queryColumnLineageParam);
        String schemaName = queryColumnLineageParam.getSchemaName();
        String dbName = queryColumnLineageParam.getDbName() ;
        String columnName = queryColumnLineageParam.getColumnName();
        String tableName = queryColumnLineageParam.getTableName();
        //TODO 手动添加的表无法查看血缘关系
        List<LineageDataSetInfo> dataSetInfoList = lineageDataSetInfoService.getListByParams(lineageDataSource.getDataInfoId(), dbName, tableName, schemaName,appType);
        if(CollectionUtils.isEmpty(dataSetInfoList)){
            return ListUtils.EMPTY_LIST;
        }
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageDataSetInfo dataSetInfo : dataSetInfoList) {
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
            Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getDataInfoId).collect(Collectors.toSet());
            Map<Long, DsServiceInfoDTO> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getDataInfoId(), ds2 -> ds2));
            for (LineageColumnColumn cc : lineageColumnColumns) {
                LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(cc.getInputTableId());
                LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(cc.getResultTableId());
                LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(cc, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getDataInfoId()), dataSourceMap.get(resultTableInfo.getDataInfoId()));
                res.add(columnColumnVO);
            }
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
            DsServiceInfoDTO inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getDataInfoId(),appType);
            if (Objects.isNull(inputDataSource)){
                throw new RdosDefineException("数据源不存在");
            }
            LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputDataSource.getDataInfoId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName(),appType);
            LineageTableVO resultTableInfoVO = lineageColumnColumnVO.getResultTableInfo();
            LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
            DsServiceInfoDTO  resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getDataInfoId(),appType);
            if (Objects.isNull(resultDataSource)){
                throw new RdosDefineException("数据源不存在");
            }
            LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getDataInfoId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName(),appType);
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
                DsServiceInfoDTO inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getDataInfoId(), appType);
                if (Objects.isNull(inputDataSource)){
                    continue;
    //                throw new RdosDefineException("数据源不存在");
                }
                String inputDbName = inputTableInfoVo.getDbName();
                LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputDataSource.getDataInfoId(), inputDbName, inputTableInfoVo.getTableName(), inputDbName,appType);
                LineageTableVO resultTableInfoVO = lineageColumnColumnVO.getResultTableInfo();
                LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
                DsServiceInfoDTO resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getDataInfoId(), appType);

                if (Objects.isNull(resultDataSource)){
                    throw new RdosDefineException("数据源不存在");
                }
                String resultDbName = resultTableInfoVO.getDbName();
                LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getDataInfoId(), resultDbName, resultTableInfoVO.getTableName(), resultDbName,appType);
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
        DsServiceInfoDTO  inputDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(inputDataSourceVO.getDataInfoId(),appType);

        if (Objects.isNull(inputDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputDataSource.getDataInfoId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName(),appType);
        LineageTableVO resultTableInfoVO = lineageColumnColumnVO.getResultTableInfo();
        LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
        DsServiceInfoDTO resultDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(resultDataSourceVO.getDataInfoId(),appType);
        if (Objects.isNull(resultDataSource)){
            throw new RdosDefineException("数据源不存在");
        }
        LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSource.getDataInfoId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName(),appType);
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
        List<Long> tableIds = checkAndGetTableId(queryTableLineageColumnParam);
        List<String> columnList = new ArrayList<>();
        for (Long tableId : tableIds) {
            columnList.addAll(lineageColumnColumnService.queryTableInputLineageColumns(tableId));
        }
        return columnList.stream().distinct().collect(Collectors.toList());
    }


    /**
     * 查询表血缘下游字段列表
     * @param queryTableLineageColumnParam
     * @return
     */
    public List<String> queryTableResultLineageColumns(QueryTableLineageColumnParam queryTableLineageColumnParam){
        List<Long> tableIds = checkAndGetTableId(queryTableLineageColumnParam);
        List<String> columnList = new ArrayList<>();
        for (Long tableId : tableIds) {
            columnList.addAll(lineageColumnColumnService.queryTableResultLineageColumns(tableId));
        }
        return columnList.stream().distinct().collect(Collectors.toList());
    }

    private List<Long> checkAndGetTableId(QueryTableLineageColumnParam queryTableLineageColumnParam){
        Integer appType = queryTableLineageColumnParam.getAppType();
        String dbName = queryTableLineageColumnParam.getDbName();
        String tableName = queryTableLineageColumnParam.getTableName();
        DsServiceInfoDTO dsServiceInfoDTO = lineageDataSourceService.getDataSourceByIdAndAppType(queryTableLineageColumnParam.getDataInfoId(),queryTableLineageColumnParam.getAppType());
        if(null == dsServiceInfoDTO ){
            logger.error("do not find need dataSource");
            throw new RdosDefineException("没有可用的数据源");
        }
        List<LineageDataSetInfo> dataSetInfos = lineageDataSetInfoService.getListByParams(dsServiceInfoDTO.getDataInfoId(), dbName, tableName, dbName,appType);
        if (CollectionUtils.isEmpty(dataSetInfos)){
            return ListUtils.EMPTY_LIST;
        }
        return dataSetInfos.stream().map(LineageDataSetInfo::getId).collect(Collectors.toList());
    }

    public List<com.dtstack.engine.api.pojo.lineage.Table> parseTables(String sql, String defaultDb, Integer sourceType) {

        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(sourceType);
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + sourceType + "不支持");
        }
        ISqlParserClient sqlParserClient = sqlParserClientOperator.getClient("sqlparser");
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
            throw new RdosDefineException(e.getMessage(),ErrorCode.SQLPARSE_ERROR);
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
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getDataInfoId).collect(Collectors.toSet());
        Map<Long, DsServiceInfoDTO> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getDataInfoId(), ds2 -> ds2));
        List<LineageTableTableVO> res = Lists.newArrayList();
        for (LineageTableTable tt : lineageTableTables) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(tt.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(tt.getResultTableId());
            LineageTableTableVO lineageTableTableVO = TableLineageAdapter.tableTable2TableTableVO(tt, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getDataInfoId()), dataSourceMap.get(resultTableInfo.getDataInfoId()));
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
        Set<Long> dataSourceIds = dataSetListByIds.stream().map(LineageDataSetInfo::getDataInfoId).collect(Collectors.toSet());
        Map<Long, DsServiceInfoDTO> dataSourceMap = lineageDataSourceService.getDataSourcesByIdList(Lists.newArrayList(dataSourceIds)).stream().collect(Collectors.toMap(ds1 -> ds1.getDataInfoId(), ds2 -> ds2));
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageColumnColumn cc : lineageColumnColumns) {
            LineageDataSetInfo inputTableInfo = dataSetInfoMap.get(cc.getInputTableId());
            LineageDataSetInfo resultTableInfo = dataSetInfoMap.get(cc.getResultTableId());
            LineageColumnColumnVO columnColumnVO = ColumnLineageAdapter.columnColumn2ColumnColumnVO(cc, inputTableInfo, resultTableInfo, dataSourceMap.get(inputTableInfo.getDataInfoId()), dataSourceMap.get(resultTableInfo.getDataInfoId()));
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
