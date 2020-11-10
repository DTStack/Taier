package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.param.QueryColumnLineageParam;
import com.dtstack.engine.api.vo.lineage.param.QueryTableLineageParam;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.sql.ColumnLineage;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.TableLineage;
import com.dtstack.engine.sql.parse.SqlParserFactory;
import com.dtstack.lineage.adapter.ColumnAdapter;
import com.dtstack.lineage.adapter.ColumnLineageAdapter;
import com.dtstack.lineage.adapter.SqlTypeAdapter;
import com.dtstack.lineage.adapter.TableAdapter;
import com.dtstack.lineage.adapter.TableLineageAdapter;
import com.dtstack.lineage.enums.SourceType2TableType;
import com.dtstack.schedule.common.enums.AppType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
        SqlParserImpl sqlParser = SqlParserFactory.getInstance().getSqlParser(sourceType2TableType.getTableType());
        SqlParseInfo parseInfo = new SqlParseInfo();
        parseInfo.setOriginSql(sql);
        try {
            ParseResult parseResult = sqlParser.parseSql(sql, defaultDb, new HashMap<>());
            parseInfo.setMainDb(parseResult.getCurrentDb());
            Table mainTable = parseResult.getMainTable();
            parseInfo.setMainTable(TableAdapter.sqlTable2ApiTable(mainTable));
            parseInfo.setCurrentDb(parseResult.getCurrentDb());
            parseInfo.setFailedMsg(parseResult.getFailedMsg());
            parseInfo.setParseSuccess(parseResult.isParseSuccess());
            parseInfo.setSqlType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getSqlType()));
            parseInfo.setExtraType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getExtraSqlType()));
            parseInfo.setStandardSql(parseResult.getStandardSql());
        } catch (Exception e) {
            logger.error("sql解析失败：{}", e);
            parseInfo.setFailedMsg(e.getMessage());
            parseInfo.setParseSuccess(false);
        }
        return parseInfo;
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
        SqlParserImpl sqlParser = SqlParserFactory.getInstance().getSqlParser(sourceType2TableType.getTableType());
        TableLineageParseInfo parseInfo = new TableLineageParseInfo();
        try {
            ParseResult parseResult = sqlParser.parseSql(sql, defaultDb, new HashMap<>());
            parseInfo.setMainDb(parseResult.getCurrentDb());
            Table mainTable = parseResult.getMainTable();
            parseInfo.setMainTable(TableAdapter.sqlTable2ApiTable(mainTable));
            parseInfo.setCurrentDb(parseResult.getCurrentDb());
            parseInfo.setFailedMsg(parseResult.getFailedMsg());
            parseInfo.setParseSuccess(parseResult.isParseSuccess());
            parseInfo.setSqlType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getSqlType()));
            parseInfo.setExtraType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getExtraSqlType()));
            parseInfo.setStandardSql(parseResult.getStandardSql());
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
     */
    public void parseAndSaveTableLineage(Long dtUicTenantId, Integer appType, String sql, String defaultDb, Long engineSourceId, String unionKey) {
        LineageDataSource lineageDataSource = null;
        if (AppType.RDOS.getType() == appType) {
            //离线根据uic租户id查询数据源

        } else {
            //资产通过数据源id查询数据源
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(lineageDataSource.getSourceType());
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + lineageDataSource.getSourceType() + "不支持");
        }
        SqlParserImpl sqlParser = SqlParserFactory.getInstance().getSqlParser(sourceType2TableType.getTableType());
        try {
            ParseResult parseResult = sqlParser.parseSql(sql, defaultDb, new HashMap<>());
            //3.根据表名和数dbName，schemaName查询表,sourceId。表不存在则需要插入表
            List<Table> tables = parseResult.getTables();
            Map<String, LineageDataSetInfo> tableRef = new HashMap<>();
            String tableKey = "%s.%s";
            for (int i = 0; i < tables.size(); i++) {
                Table ta = tables.get(i);
                LineageDataSetInfo dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), ta.getDb(), ta.getName(), ta.getDb());
                if (Objects.isNull(dataSet)) {
                    //TODO 保存dataSet
                }
                tableRef.put(String.format(tableKey, ta.getDb(), ta.getName()), dataSet);
            }
            List<TableLineage> tableLineages = parseResult.getTableLineages();
            if (CollectionUtils.isNotEmpty(tableLineages)) {
                List<LineageTableTable> lineageTableTables = tableLineages.stream().map(l -> TableLineageAdapter.sqlTableLineage2DbTableLineage(l, tableRef, LineageOriginType.SQL_PARSE, unionKey)).collect(Collectors.toList());
                //如果uniqueKey不为空，则删除相同uniqueKey的血缘
                lineageTableTableService.saveTableLineage(lineageTableTables);
            }

        } catch (Exception e) {
            logger.error("解析保存表血缘失败：{}", e);
            throw new RdosDefineException("解析保存表血缘失败");
        }
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
            throw new IllegalArgumentException("数据源类型" + dataSourceType + "不支持");
        }
        SqlParserImpl sqlParser = SqlParserFactory.getInstance().getSqlParser(sourceType2TableType.getTableType());
        ColumnLineageParseInfo parseInfo = new ColumnLineageParseInfo();
        try {
            Map<String, List<com.dtstack.engine.sql.Column>> sqlColumnMap = new HashMap<>();
            for (Map.Entry<String, List<Column>> entry : tableColumnsMap.entrySet()) {
                String key = entry.getKey();
                List<Column> value = entry.getValue();
                sqlColumnMap.put(key, value.stream().map(ColumnAdapter::apiColumn2SqlColumn).collect(Collectors.toList()));
            }
            ParseResult parseResult = sqlParser.parseSql(sql, defaultDb, sqlColumnMap);
            parseInfo.setMainDb(parseResult.getCurrentDb());
            Table mainTable = parseResult.getMainTable();
            parseInfo.setMainTable(TableAdapter.sqlTable2ApiTable(mainTable));
            parseInfo.setCurrentDb(parseResult.getCurrentDb());
            parseInfo.setFailedMsg(parseResult.getFailedMsg());
            parseInfo.setParseSuccess(parseResult.isParseSuccess());
            parseInfo.setSqlType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getSqlType()));
            parseInfo.setExtraType(SqlTypeAdapter.sqlType2ApiSqlType(parseResult.getExtraSqlType()));
            parseInfo.setStandardSql(parseResult.getStandardSql());
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
     *
     * @param appType        应用类型
     * @param sql            单条sql
     * @param defaultDb      默认数据库
     * @param engineSourceId 数据源id
     */
    public void parseAndSaveColumnLineage(Integer appType, String sql, String defaultDb, Long engineSourceId, String uniqueKey) {
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        //3.根据表名和数据库名，数据库id查询表。表不存在则需要插入表
        //4.获取表中的字段列表
        //5.解析字段级血缘关系
        //6.存储字段级血缘关系
        LineageDataSource lineageDataSource = null;
        if (AppType.RDOS.getType() == appType) {
            //TODO 离线根据uic租户id查询数据源
        } else {
            //资产通过数据源id查询数据源
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(lineageDataSource.getSourceType());
        if (Objects.isNull(sourceType2TableType)) {
            throw new IllegalArgumentException("数据源类型" + lineageDataSource.getSourceType() + "不支持");
        }
        SqlParserImpl sqlParser = SqlParserFactory.getInstance().getSqlParser(sourceType2TableType.getTableType());
        try {
            List<Table> resTables = sqlParser.parseTables(sql, defaultDb);
            //TODO 获取表字段信息
            ParseResult parseResult = sqlParser.parseSql(sql, defaultDb, new HashMap<>());
            //3.根据表名和数dbName，schemaName查询表,sourceId。表不存在则需要插入表
            List<Table> tables = parseResult.getTables();
            Map<String, LineageDataSetInfo> tableRef = new HashMap<>();
            String tableKey = "%s.%s";
            for (int i = 0; i < tables.size(); i++) {
                Table ta = tables.get(i);
                LineageDataSetInfo dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), ta.getDb(), ta.getName(), ta.getDb());
                if (Objects.isNull(dataSet)) {
                    //TODO 保存dataSet
                }
                tableRef.put(String.format(tableKey, ta.getDb(), ta.getName()), dataSet);
            }
            List<TableLineage> tableLineages = parseResult.getTableLineages();
            if (CollectionUtils.isNotEmpty(tableLineages)) {
                List<LineageTableTable> lineageTableTables = tableLineages.stream().map(l -> TableLineageAdapter.sqlTableLineage2DbTableLineage(l, tableRef, LineageOriginType.SQL_PARSE, uniqueKey)).collect(Collectors.toList());
                //如果uniqueKey不为空，则删除相同uniqueKey的血缘
                lineageTableTableService.saveTableLineage(lineageTableTables);
            }
            List<ColumnLineage> columnLineages = parseResult.getColumnLineages();
            if (CollectionUtils.isNotEmpty(columnLineages)) {
                lineageColumnColumnService.saveColumnLineage(columnLineages.stream().map(cl -> ColumnLineageAdapter.sqlColumnLineage2ColumnColumn(cl, appType, tableRef, uniqueKey)).collect(Collectors.toList()));
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
            //TODO 离线通过租户查询数据源
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableInputLineageByAppType(dataSetInfo.getId(), appType);
        Set<Long> tableIds = Sets.newHashSet();
        //TODO 批量查询表信息
//        lineageDataSetInfoService.
        Map<Long, LineageDataSetInfo> dataSetInfoMap = null;
        //TODO 批量查询数据源信息
        Set<Long> dataSourceIds = Sets.newHashSet();
        Map<Long, LineageDataSource> dataSourceMap = null;
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
            //TODO 离线通过租户查询数据源
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableResultLineageByAppType(dataSetInfo.getId(), appType);
        Set<Long> tableIds = Sets.newHashSet();
        //TODO 批量查询表信息
//        lineageDataSetInfoService.
        Map<Long, LineageDataSetInfo> dataSetInfoMap = null;
        //TODO 批量查询数据源信息
        Set<Long> dataSourceIds = Sets.newHashSet();
        Map<Long, LineageDataSource> dataSourceMap = null;
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
            //TODO 离线通过租户查询数据源
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageTableTable> lineageTableTables = lineageTableTableService.queryTableTableByTableAndAppId(appType,dataSetInfo.getId());
        Set<Long> tableIds = Sets.newHashSet();
        //TODO 批量查询表信息
//        lineageDataSetInfoService.
        Map<Long, LineageDataSetInfo> dataSetInfoMap = null;
        //TODO 批量查询数据源信息
        Set<Long> dataSourceIds = Sets.newHashSet();
        Map<Long, LineageDataSource> dataSourceMap = null;
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
     * @param tableTableVO
     */
    public void manualAddTableLineage(LineageTableTableVO tableTableVO) {
        LineageTableTable lineageTableTable = null;
        LineageTableVO inputTableInfoVo = tableTableVO.getInputTableInfo();
        LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
        if (Objects.isNull(inputDataSourceVO.getSourceId())) {
            //TODO 查询数据源是否存在
//            lineageDataSourceService.getBySourceNameAndType;
        }
        LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputTableInfoVo.getDataSourceVO().getSourceId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
        LineageTableVO resultTableInfoVO = tableTableVO.getResultTableInfo();
        LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
        if (Objects.isNull(resultDataSourceVO.getSourceId())) {
            //TODO 检查数据源
        }
        LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSourceVO.getSourceId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName());
        lineageTableTable = new LineageTableTable();
        lineageTableTable.setResultTableId(resultTableInfo.getId());
        lineageTableTable.setResultTableKey(resultTableInfo.getTableKey());
        lineageTableTable.setInputTableId(inputTableInfo.getId());
        lineageTableTable.setInputTableKey(inputTableInfo.getTableKey());
        lineageTableTable.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
        lineageTableTable.setAppType(tableTableVO.getAppType());
        lineageTableTableService.manualAddTableLineage(tableTableVO.getAppType(), lineageTableTable);
    }

    /**
     * 手动删除表级血缘
     *
     * @param tableTableVO
     */
    public void manualDeleteTableLineage(LineageTableTableVO tableTableVO) {
        //LineageTableTable lineageTableTable
        LineageTableTable lineageTableTable = null;
        LineageTableVO inputTableInfoVo = tableTableVO.getInputTableInfo();
        LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
        if (Objects.isNull(inputDataSourceVO.getSourceId())) {
            //TODO 查询数据源是否存在
//            lineageDataSourceService.getBySourceNameAndType;
        }
        LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputTableInfoVo.getDataSourceVO().getSourceId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
        LineageTableVO resultTableInfoVO = tableTableVO.getResultTableInfo();
        LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
        if (Objects.isNull(resultDataSourceVO.getSourceId())) {
            //TODO 检查数据源
        }
        LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSourceVO.getSourceId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName());
        lineageTableTable = new LineageTableTable();
        lineageTableTable.setResultTableId(resultTableInfo.getId());
        lineageTableTable.setResultTableKey(resultTableInfo.getTableKey());
        lineageTableTable.setInputTableId(inputTableInfo.getId());
        lineageTableTable.setInputTableKey(inputTableInfo.getTableKey());
        lineageTableTable.setLineageSource(LineageOriginType.MANUAL_ADD.getType());
        lineageTableTable.setAppType(tableTableVO.getAppType());
        lineageTableTableService.manualDeleteTableLineage(tableTableVO.getAppType(), lineageTableTable);
    }

    /**
     * 查询字段上游字段血缘
     *
     * @return
     */
    public List<LineageColumnColumnVO> queryColumnInoutLineage(QueryColumnLineageParam queryColumnLineageParam) {
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
            //TODO 离线通过租户查询数据源
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnInputLineageByAppType(appType, dataSetInfo.getId(), columnName);
        Set<Long> tableIds = Sets.newHashSet();
        //TODO 批量查询表信息
//        lineageDataSetInfoService.
        Map<Long, LineageDataSetInfo> dataSetInfoMap = null;
        //TODO 批量查询数据源信息
        Set<Long> dataSourceIds = Sets.newHashSet();
        Map<Long, LineageDataSource> dataSourceMap = null;
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageColumnColumn cc:lineageColumnColumns){
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
            //TODO 离线通过租户查询数据源
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnResultLineageByAppType(appType, dataSetInfo.getId(), columnName);
        Set<Long> tableIds = Sets.newHashSet();
        //TODO 批量查询表信息
//        lineageDataSetInfoService.
        Map<Long, LineageDataSetInfo> dataSetInfoMap = null;
        //TODO 批量查询数据源信息
        Set<Long> dataSourceIds = Sets.newHashSet();
        Map<Long, LineageDataSource> dataSourceMap = null;
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageColumnColumn cc:lineageColumnColumns){
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
            //TODO 离线通过租户查询数据源
        } else {
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId, appType);
        }
        //TODO 手动添加的表无法查看血缘关系
        LineageDataSetInfo dataSetInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId(), dbName, tableName, schemaName);
        List<LineageColumnColumn> lineageColumnColumns = lineageColumnColumnService.queryColumnLineages(appType, dataSetInfo.getId(), columnName);
        Set<Long> tableIds = Sets.newHashSet();
        //TODO 批量查询表信息
//        lineageDataSetInfoService.
        Map<Long, LineageDataSetInfo> dataSetInfoMap = null;
        //TODO 批量查询数据源信息
        Set<Long> dataSourceIds = Sets.newHashSet();
        Map<Long, LineageDataSource> dataSourceMap = null;
        List<LineageColumnColumnVO> res = Lists.newArrayList();
        for (LineageColumnColumn cc:lineageColumnColumns){
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
     * @param lineageColumnColumnVO
     */
    public void manualAddColumnLineage(LineageColumnColumnVO lineageColumnColumnVO) {
        LineageTableVO inputTableInfoVo = lineageColumnColumnVO.getInputTableInfo();
        LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
        if (Objects.isNull(inputDataSourceVO.getSourceId())) {
            //TODO 查询数据源是否存在
//            lineageDataSourceService.getBySourceNameAndType;
        }
        LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputTableInfoVo.getDataSourceVO().getSourceId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
        LineageTableVO resultTableInfoVO = lineageColumnColumnVO.getResultTableInfo();
        LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
        if (Objects.isNull(resultDataSourceVO.getSourceId())) {
            //TODO 检查数据源
        }
        LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSourceVO.getSourceId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName());
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
        lineageColumnColumnService.manualAddColumnLineage(lineageColumnColumnVO.getAppType(), lineageColumnColumn);
    }

    /**
     * 手动删除字段级级血缘
     *
     * @param lineageColumnColumnVO
     */
    public void manualDeleteColumnLineage(LineageColumnColumnVO lineageColumnColumnVO) {
        LineageTableVO inputTableInfoVo = lineageColumnColumnVO.getInputTableInfo();
        LineageDataSourceVO inputDataSourceVO = inputTableInfoVo.getDataSourceVO();
        if (Objects.isNull(inputDataSourceVO.getSourceId())) {
            //TODO 查询数据源是否存在
//            lineageDataSourceService.getBySourceNameAndType;
        }
        LineageDataSetInfo inputTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(inputTableInfoVo.getDataSourceVO().getSourceId(), inputTableInfoVo.getDbName(), inputTableInfoVo.getTableName(), inputTableInfoVo.getSchemaName());
        LineageTableVO resultTableInfoVO = lineageColumnColumnVO.getResultTableInfo();
        LineageDataSourceVO resultDataSourceVO = resultTableInfoVO.getDataSourceVO();
        if (Objects.isNull(resultDataSourceVO.getSourceId())) {
            //TODO 检查数据源
        }
        LineageDataSetInfo resultTableInfo = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(resultDataSourceVO.getSourceId(), resultTableInfoVO.getDbName(), resultTableInfoVO.getTableName(), resultTableInfoVO.getSchemaName());
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
        lineageColumnColumnService.manualDeleteColumnLineage(lineageColumnColumnVO.getAppType(), lineageColumnColumn);
    }
}
