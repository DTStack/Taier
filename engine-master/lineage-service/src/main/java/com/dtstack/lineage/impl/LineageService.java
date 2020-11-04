package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlParserImpl;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.TableLineage;
import com.dtstack.engine.sql.parse.SqlParserFactory;
import com.dtstack.lineage.adapter.SqlTypeAdapter;
import com.dtstack.lineage.adapter.TableAdapter;
import com.dtstack.lineage.adapter.TableLineageAdapter;
import com.dtstack.lineage.dao.LineageColumnColumnDao;
import com.dtstack.lineage.dao.LineageTableTableDao;
import com.dtstack.lineage.enums.SourceType2TableType;
import com.dtstack.schedule.common.enums.AppType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * @param sql 单条sql
     * @return
     */
    public SqlParseInfo parseSql(String sql,String defaultDb,Integer dataSourceType){
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(dataSourceType);
        if (Objects.isNull(sourceType2TableType)){
            throw new IllegalArgumentException("数据源类型"+dataSourceType+"不支持");
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
            logger.error("sql解析失败：{}",e);
            parseInfo.setFailedMsg(e.getMessage());
            parseInfo.setParseSuccess(false);
        }
        return parseInfo;
    }

    /**
     * 解析表血缘
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @return
     */
    public TableLineageParseInfo parseTableLineage(String sql, String defaultDb, Integer dataSourceType){
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(dataSourceType);
        if (Objects.isNull(sourceType2TableType)){
            throw new IllegalArgumentException("数据源类型"+dataSourceType+"不支持");
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
            if (CollectionUtils.isNotEmpty(tableLineages)){
                parseInfo.setTableLineages(tableLineages.stream().map(TableLineageAdapter::sqlTableLineage2ApiTableLineage).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            logger.error("sql解析失败：{}",e);
            parseInfo.setFailedMsg(e.getMessage());
            parseInfo.setParseSuccess(false);
        }

        return parseInfo;
    }

    /**
     * 解析并存储表血缘
     * @param appType 应用类型
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @param engineSourceId 数据源id
     */
    public void parseAndSaveTableLineage(Integer appType,String sql, String defaultDb, Long engineSourceId ,String unionKey){
        LineageDataSource lineageDataSource = null;
        if (AppType.RDOS.getType() == appType){
            //离线根据uic租户id查询数据源

        }else {
            //资产通过数据源id查询数据源
            lineageDataSource = lineageDataSourceService.getDataSourceByIdAndAppType(engineSourceId,appType);
        }
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        SourceType2TableType sourceType2TableType = SourceType2TableType.getBySourceType(lineageDataSource.getSourceType());
        if (Objects.isNull(sourceType2TableType)){
            throw new IllegalArgumentException("数据源类型"+lineageDataSource.getSourceType()+"不支持");
        }
        SqlParserImpl sqlParser = SqlParserFactory.getInstance().getSqlParser(sourceType2TableType.getTableType());
        try {
            ParseResult parseResult = sqlParser.parseSql(sql, defaultDb, new HashMap<>());
            //3.根据表名和数dbName，schemaName查询表,sourceId。表不存在则需要插入表
            List<Table> tables = parseResult.getTables();
            Map<String,LineageDataSetInfo> tableRef = new HashMap<>();
            String tableKey = "%s.%s";
            for (int i = 0; i < tables.size(); i++) {
                Table ta = tables.get(i);
                LineageDataSetInfo dataSet = lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(lineageDataSource.getId().intValue(), ta.getDb(), ta.getName(), ta.getDb());
                if (Objects.isNull(dataSet)){
                    //TODO 保存dataSet
                }
                tableRef.put(String.format(tableKey,ta.getDb(),ta.getName()),dataSet);
            }
            List<TableLineage> tableLineages = parseResult.getTableLineages();
            if (CollectionUtils.isNotEmpty(tableLineages)){
                List<LineageTableTable> lineageTableTables = tableLineages.stream().map(l -> TableLineageAdapter.sqlTableLineage2DbTableLineage(l, tableRef, LineageOriginType.SQL_PARSE, unionKey)).collect(Collectors.toList());
                //如果uniqueKey不为空，则删除相同uniqueKey的血缘
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //4.获取表中的字段列表
        //5.解析字段级血缘关系
        //6.存储字段级血缘关系
        //TODO
    }

    /**
     * 解析字段级血缘
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @param tableColumnsMap 表字段map
     * @return
     */
    public ColumnLineageParseInfo parseColumnLineage(String sql, Integer dataSourceType,String defaultDb, Map<String, List<Column>> tableColumnsMap){
        //TODO
        return null;
    }

    /**
     * 解析并存储字段级血缘
     * @param appType 应用类型
     * @param sql 单条sql
     * @param defaultDb 默认数据库
     * @param engineSourceId 数据源id
     */
    public void parseAndSaveColumnLineage(Integer appType,String sql, String defaultDb, Long engineSourceId){
        //1.根据数据源id和appType查询数据源
        //2.解析出sql中的表
        //3.根据表名和数据库名，数据库id查询表。表不存在则需要插入表
        //4.获取表中的字段列表
        //5.解析字段级血缘关系
        //6.存储字段级血缘关系
        //TODO
    }

    /**
     * 查询表上游表血缘
     * @param appType
     * @param tableId
     * @return
     */
    public List<LineageTableTable> queryTableInputLineage(Long appType,Long tableId){
        //TODO
        return null;
    }

    /**
     * 查询表下游表血缘
     * @param appType
     * @param tableId
     * @return
     */
    public List<LineageTableTable> queryTableResultLineage(Long appType,Long tableId){
        //TODO
        return null;
    }

    /**
     * 查询表级血缘关系
     * @param appType
     * @param tableId
     * @return
     */
    public List<LineageTableTable> queryTableLineages(Long appType,Long tableId){
        //TODO
        return null;
    }

    /**
     * 手动添加表级血缘
     * @param appType
     * @param lineageTableTable
     */
    public void manualAddTableLineage(Long appType,LineageTableTable lineageTableTable){
        //TODO
    }

    /**
     * 手动删除表级血缘
     * @param appType
     * @param lineageTableTable
     */
    public void manualDeleteTableLineage(Long appType,LineageTableTable lineageTableTable){
        //TODO
    }

    /**
     * 查询字段上游字段血缘
     * @return
     */
    public List<LineageColumnColumn> queryColumnInoutLineage(Long appType,Long tableId,String columnName){
        //TODO
        return null;
    }

    /**
     * 查询字段下游字段血缘
     * @return
     */
    public List<LineageColumnColumn> queryColumnResultLineage(Long appType,Long tableId,String columnName){
        //TODO
        return null;
    }

    /**
     * 查询字段级血缘关系
     * @param appType
     * @param tableId
     * @param columnName
     * @return
     */
    public List<LineageColumnColumn> queryColumnLineages(Long appType,Long tableId,String columnName){
        //TODO
        return null;
    }

    /**
     * 手动添加表级血缘
     * @param appType
     * @param lineageColumnColumn
     */
    public void manualAddColumnLineage(Long appType,LineageColumnColumn lineageColumnColumn){
        //TODO
    }

    /**
     * 手动删除字段级级血缘
     * @param appType
     * @param lineageColumnColumn
     */
    public void manualDeleteColumnLineage(Long appType,LineageColumnColumn lineageColumnColumn){
        //TODO
    }
}
