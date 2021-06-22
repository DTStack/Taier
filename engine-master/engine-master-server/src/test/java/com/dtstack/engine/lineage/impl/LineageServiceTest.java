package com.dtstack.engine.lineage.impl;

import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.pojo.LevelAndCount;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.LineageTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.param.*;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.dao.LineageDataSetDao;
import com.dtstack.engine.dao.LineageDataSourceDao;
import com.dtstack.engine.lineage.util.SqlParserClientOperator;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.sqlparser.common.client.ISqlParserClient;
import com.dtstack.sqlparser.common.client.domain.AlterResult;
import com.dtstack.sqlparser.common.client.domain.ParseResult;
import com.dtstack.sqlparser.common.client.domain.Table;
import com.dtstack.sqlparser.common.client.domain.TableLineage;
import com.dtstack.sqlparser.common.client.enums.ETableType;
import com.dtstack.sqlparser.common.client.enums.SqlType;
import com.dtstack.sqlparser.common.client.enums.TableOperateEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.dtstack.engine.master.AbstractTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author basion
 * @Classname LineageServiceTest
 * @Description unit test for LineageService
 * @Date 2020-11-27 10:33:55
 * @Created basion
 */
public class LineageServiceTest extends AbstractTest {

    @Autowired
    private LineageService lineageService;

    @Autowired
    private TestLineageDataSourceDao testLineageDataSourceDao;

    @Autowired
    private TestLineageDataSetInfoDao testLineageDataSetInfoDao;

    @Autowired
    private TestLineageColumnColumnDao testLineageColumnColumnDao;

    @Autowired
    private TestLineageTableTableDao testLineageTableTableDao;

    @Autowired
    private LineageDataSourceDao lineageDataSourceDao;

    @MockBean
    private SqlParserClientOperator clientOperator;

    @MockBean
    private LineageDataSetInfoService lineageDataSetInfoService;

    @Autowired
    private LineageDataSourceService dataSourceService;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private LineageDataSetDao lineageDataSetDao;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ComponentConfigDao componentConfigDao;



    @Before
    public void setUp() throws Exception {

        ISqlParserClient iSqlParserClient = new ISqlParserClient() {
            @Override
            public ParseResult parseSql(String s, String s1, Map<String, List<com.dtstack.sqlparser.common.client.domain.Column>> map, ETableType eTableType) throws Exception {

                ParseResult parseResult = new ParseResult();
                parseResult.setCurrentDb("dev");
                parseResult.setFailedMsg("");
                parseResult.setParseSuccess(true);
                parseResult.setSqlType(SqlType.ALTER);
                return parseResult;
            }

            @Override
            public List<com.dtstack.sqlparser.common.client.domain.Table> parseTables(String s, String s1, ETableType eTableType) throws Exception {
                return null;
            }

            @Override
            public ParseResult parseTableLineage(String s, String s1, ETableType eTableType) throws Exception {

                ParseResult parseResult = new ParseResult();
                parseResult.setStandardSql("create table chener as select * from chener1");
                return parseResult;
            }

            @Override
            public ParseResult getSqlParserNode(String s, String s1, Map<String, List<com.dtstack.sqlparser.common.client.domain.Column>> map, ETableType eTableType) throws Exception {
                return null;
            }

            @Override
            public Set<String> parseFunction(String sql) throws Exception {
                Set<String> sets = new HashSet<>();
                sets.add("nmd");
                return sets;
            }
        };

        when(clientOperator.getClient(any())).thenReturn(iSqlParserClient);
        ParseResult parseResult = new ParseResult();
        parseResult.setCurrentDb("dev");
        Map<String,List<Column>> mapColumn = new HashMap<>();
        Column column = new Column();
        column.setName("id");
        when(lineageDataSetInfoService.getColumnsBySourceIdAndListTable(any(),any())).thenReturn(mapColumn);
        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        dataSetInfo.setId(1L);
        dataSetInfo.setAppType(1);
        when(lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(any(),any(),any(),any(),any())).thenReturn(dataSetInfo);

        LineageDataSetInfo lineageDataSetInfo = new LineageDataSetInfo();
        lineageDataSetInfo.setId(10L);
        lineageDataSetInfo.setDbName("dev");
        lineageDataSetInfo.setSchemaName("dev");
        lineageDataSetInfo.setDataInfoId(1L);
        lineageDataSetInfo.setTableName("chener");
        lineageDataSetInfo.setTableKey("dev.chener");
        when(lineageDataSetInfoService.getOneBySourceIdAndDbNameAndTableName(any(),any(),any(),any(),any())).thenReturn(lineageDataSetInfo);

    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseSql() {
        SqlParseInfo parseSql = lineageService.parseSql("create table chener (id int)", "dev", DataSourceType.HIVE.getVal());
        Assert.assertEquals("dev",parseSql.getCurrentDb());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseTableLineage() {
        TableLineageParseInfo parseTableLineage = lineageService.parseTableLineage("create table chener as select * from chener1", "dev", DataSourceType.HIVE.getVal());
        Assert.assertEquals("create table chener as select * from chener1",parseTableLineage.getStandardSql());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseAndSaveTableLineage() {
        LineageDataSource defaultHiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        //测试资产
        lineageService.parseAndSaveTableLineage(1L, AppType.DATAASSETS.getType(), "insert into test select * from test1", "dev", defaultHiveDataSourceTemplate.getId(), defaultHiveDataSourceTemplate.getSourceType(), "11");
        //测试离线
        LineageDataSource rdosHiveDataSourceTemplate = Template.getRdostHiveDataSourceTemplate();
        lineageService.parseAndSaveTableLineage(1L,AppType.RDOS.getType(),"insert into test_beihai select * from test2","beihai",rdosHiveDataSourceTemplate.getId(),rdosHiveDataSourceTemplate.getSourceType(),"1125");

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetTableRef() {


        LineageDataSource sourceTemplate = DataCollection.getData().getRdostHiveDataSourceTemplate();
        Map<String,LineageDataSource> map = new HashMap<>();
        List<Table> tables = new ArrayList<>();
        Table table = new Table();
        table.setDb("beihai");
        table.setName("table1");
        table.setOperate(TableOperateEnum.CREATE);
        tables.add(table);
        map.put("beihai",sourceTemplate);
        Map<String, LineageDataSetInfo> tableRef = lineageService.getTableRef(1, "beihai", null, tables);
        Assert.assertNotNull(tableRef);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSaveTableLineage() {

        Tenant tenant = Template.getTenantTemplate();
        List<TableLineage> tableLineages = new ArrayList<>();
        TableLineage tableLineage = new TableLineage();
        tableLineage.setFromDb("beihai");
        tableLineage.setFromTable("t1");
        tableLineage.setToDb("beihai");
        tableLineage.setToTable("t2");
        tableLineages.add(tableLineage);
        ParseResult parseResult = new ParseResult();
        parseResult.setTableLineages(tableLineages);
        LineageDataSetInfo dataSetInfo1 = new LineageDataSetInfo();
        dataSetInfo1.setDtUicTenantId(tenant.getDtUicTenantId());
        dataSetInfo1.setId(1L);
        dataSetInfo1.setTableKey("beihai.t1");
        LineageDataSetInfo dataSetInfo2 = new LineageDataSetInfo();
        dataSetInfo2.setId(2L);
        dataSetInfo2.setTableKey("beihai.t2");
        Map<String,LineageDataSetInfo> tableRef = new HashMap<>();
        tableRef.put("beihai.t1",dataSetInfo1);
        tableRef.put("beihai.t2",dataSetInfo2);
        lineageService.saveTableLineage(tenant.getDtUicTenantId(),1,"111",parseResult,tableRef);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testHandleDropTableAndAlterRename() {
        LineageDataSource sourceTemplate = Template.getRdostHiveDataSourceTemplate();

        ParseResult parseResult = new ParseResult();
        parseResult.setSqlType(SqlType.DROP);
        Table table = new Table();
        table.setDb("beihai");
        table.setName("t1");
        parseResult.setMainTable(table);
        Boolean flag = lineageService.handleDropTableAndAlterRename(null, parseResult,1);
        Assert.assertTrue(flag);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testHandleDropTableAndAlterRename2() {
        LineageDataSource sourceTemplate = Template.getRdostHiveDataSourceTemplate();
        Map<String,LineageDataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("beihai",sourceTemplate);
        ParseResult parseResult = new ParseResult();
        parseResult.setSqlType(SqlType.ALTER);
        AlterResult alterResult = new AlterResult();
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_RENAME);
        alterResult.setOldDB("beihai");
        alterResult.setOldTableName("t1");
        alterResult.setNewTableName("t2");
        parseResult.setAlterResult(alterResult);
        Boolean flag = lineageService.handleDropTableAndAlterRename(null, parseResult,1);
        Assert.assertTrue(flag);

    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseColumnLineage() {
        Map<String, List<Column>> columnMap = new HashMap<>(4);
        List<Column> list = new ArrayList<>();
        Column column = new Column();
        column.setName("id");
        Column column2 = new Column();
        column2.setName("name");
        list.add(column);
        list.add(column2);
        columnMap.put("dev.test",list);
        columnMap.put("dev.test1",list);
        ColumnLineageParseInfo parseColumnLineage = lineageService.parseColumnLineage("insert into test select * from test1", DataSourceType.HIVE.getVal(), "dev", columnMap);
        Assert.assertNotNull(parseColumnLineage);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseAndSaveColumnLineage() {
        LineageDataSource dataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
        parseColumnLineageParam.setSql("insert into test select * from test1");
        parseColumnLineageParam.setUniqueKey(null);
        parseColumnLineageParam.setDtUicTenantId(1L);
        parseColumnLineageParam.setDefaultDb("dev");
        parseColumnLineageParam.setAppType(AppType.DATAASSETS.getType());
        parseColumnLineageParam.setDataInfoId(dataSourceTemplate.getId());
        lineageService.parseAndSaveColumnLineage(parseColumnLineageParam);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseAndSaveColumnLineage2() {
        //测试离线字段血缘解析保存
        LineageDataSource dataSourceTemplate = Template.getRdostHiveDataSourceTemplate();
        ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
        parseColumnLineageParam.setSql("insert into test select * from test1");
        parseColumnLineageParam.setUniqueKey(null);
        parseColumnLineageParam.setDtUicTenantId(1L);
        parseColumnLineageParam.setDefaultDb("beihai");
        parseColumnLineageParam.setAppType(AppType.RDOS.getType());
        parseColumnLineageParam.setDataInfoId(dataSourceTemplate.getId());
        lineageService.parseAndSaveColumnLineage(parseColumnLineageParam);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableInputLineage() {
        LineageDataSource defaultHiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        QueryTableLineageParam queryTableLineageParam = new QueryTableLineageParam();
        queryTableLineageParam.setAppType(AppType.DATAASSETS.getType());
        queryTableLineageParam.setDbName("dev");
        queryTableLineageParam.setDtUicTenantId(1L);
        queryTableLineageParam.setSourceName(defaultHiveDataSourceTemplate.getSourceName());
        queryTableLineageParam.setSourceType(defaultHiveDataSourceTemplate.getSourceType());
        queryTableLineageParam.setTableName("test");
        queryTableLineageParam.setLevel(1);
        List<LineageTableTableVO> queryTableInputLineage = lineageService.queryTableInputLineage(queryTableLineageParam);
        Assert.assertNotNull(queryTableInputLineage);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableResultLineage() {
        LineageDataSource defaultHiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        QueryTableLineageParam queryTableLineageParam = new QueryTableLineageParam();
        queryTableLineageParam.setAppType(AppType.DATAASSETS.getType());
        queryTableLineageParam.setDbName("dev");
        queryTableLineageParam.setDtUicTenantId(1L);
        queryTableLineageParam.setSourceName(defaultHiveDataSourceTemplate.getSourceName());
        queryTableLineageParam.setSourceType(defaultHiveDataSourceTemplate.getSourceType());
        queryTableLineageParam.setTableName("test");
        queryTableLineageParam.setLevel(1);
        List<LineageTableTableVO> queryTableResultLineage = lineageService.queryTableResultLineage(queryTableLineageParam);
        Assert.assertNotNull(queryTableResultLineage);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableLineages() {
        LineageDataSource defaultHiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        QueryTableLineageParam queryTableLineageParam = new QueryTableLineageParam();
        queryTableLineageParam.setAppType(AppType.DATAASSETS.getType());
        queryTableLineageParam.setDbName("dev");
        queryTableLineageParam.setDtUicTenantId(1L);
        queryTableLineageParam.setSourceName(defaultHiveDataSourceTemplate.getSourceName());
        queryTableLineageParam.setSourceType(defaultHiveDataSourceTemplate.getSourceType());
        queryTableLineageParam.setTableName("test");
        queryTableLineageParam.setLevel(1);
        List<LineageTableTableVO> queryTableLineages = lineageService.queryTableLineages(queryTableLineageParam);
        Assert.assertNotNull(queryTableLineages);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualAddTableLineage() {
        LineageDataSource hiveDataSourceTemplate = testLineageDataSourceDao.getOne();
        List<LineageTableTableVO> tableTableVOs = new ArrayList<>();
        LineageTableTableVO vo = new LineageTableTableVO();
        vo.setDtUicTenantId(1L);
        LineageTableVO tableVO = new LineageTableVO();
        LineageDataSourceVO sourceVO = new LineageDataSourceVO();
        sourceVO.setAppType(hiveDataSourceTemplate.getAppType());
        sourceVO.setSourceType(hiveDataSourceTemplate.getSourceType());
        sourceVO.setDataInfoId(hiveDataSourceTemplate.getId());
        tableVO.setDataSourceVO(sourceVO);
        tableVO.setTableName("chener1");
        tableVO.setDbName("dev");
        tableVO.setSchemaName("dev");
        vo.setInputTableInfo(tableVO);
        LineageTableVO tableVO2 = new LineageTableVO();
        tableVO2.setDataSourceVO(sourceVO);
        tableVO2.setTableName("chener2");
        tableVO2.setDbName("dev");
        tableVO2.setSchemaName("dev");
        vo.setResultTableInfo(tableVO2);
        vo.setAppType(hiveDataSourceTemplate.getAppType());
        vo.setManual(true);
        vo.setUniqueKey(null);
        tableTableVOs.add(vo);
        lineageService.manualAddTableLineage(tableTableVOs);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAcquireOldTableTable() {
        LineageDataSource hiveDataSourceTemplate = testLineageDataSourceDao.getOne();

        List<LineageTableTableVO> tableTableVOs = new ArrayList<>();
        LineageTableTableVO vo = new LineageTableTableVO();
        vo.setDtUicTenantId(1L);
        LineageTableVO tableVO = new LineageTableVO();
        LineageDataSourceVO sourceVO = new LineageDataSourceVO();
        sourceVO.setAppType(AppType.DATAASSETS.getType());
        sourceVO.setSourceType(DataSourceType.HIVE.getVal());
        sourceVO.setDataInfoId(hiveDataSourceTemplate.getId());
        tableVO.setDataSourceVO(sourceVO);
        tableVO.setTableName("chener1");
        tableVO.setDbName("dev");
        tableVO.setSchemaName("dev");
        vo.setInputTableInfo(tableVO);
        LineageTableVO tableVO2 = new LineageTableVO();
        tableVO2.setDataSourceVO(sourceVO);
        tableVO2.setTableName("chener2");
        tableVO2.setDbName("dev");
        tableVO2.setSchemaName("dev");
        vo.setResultTableInfo(tableVO2);
        vo.setAppType(AppType.DATAASSETS.getType());
        vo.setManual(true);
        vo.setUniqueKey(null);
        tableTableVOs.add(vo);
        lineageService.acquireOldTableTable(tableTableVOs);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualDeleteTableLineage() {
        LineageDataSource hiveDataSourceTemplate = testLineageDataSourceDao.getOne();

        LineageDataSetInfo dataSetInfo1 = Template.getDefaultDataSetInfoTemplate();
        dataSetInfo1.setSchemaName("dev");
        dataSetInfo1.setDbName("dev");
        dataSetInfo1.setTableName("hjl1");
        dataSetInfo1.setTableKey("1devhjl1");
        dataSetInfo1.setDataInfoId(hiveDataSourceTemplate.getId());
        testLineageDataSetInfoDao.insert(dataSetInfo1);
        LineageDataSetInfo dataSetInfo = Template.getDefaultDataSetInfoTemplate();
        dataSetInfo.setDbName("dev");
        dataSetInfo.setSchemaName("dev");
        dataSetInfo.setTableName("hjl2");
        dataSetInfo.setTableKey("1devhjl2");
        dataSetInfo.setDataInfoId(hiveDataSourceTemplate.getId());
        testLineageDataSetInfoDao.insert(dataSetInfo);
        LineageTableTable tableTable = Template.getDefaultTableTable();
        tableTable.setInputTableKey("1devhjl1");
        tableTable.setLineageSource(LineageOriginType.SQL_PARSE.getType());
        tableTable.setInputTableId(dataSetInfo1.getId());
        tableTable.setResultTableId(dataSetInfo.getId());
        tableTable.setResultTableKey(dataSetInfo.getTableKey());
        String rawKey = String.format("%s_%s", dataSetInfo1.getId(),dataSetInfo.getId());
        tableTable.setTableLineageKey(rawKey);
        testLineageTableTableDao.insert(tableTable);

        LineageTableTableVO vo = new LineageTableTableVO();
        vo.setDtUicTenantId(1L);
        LineageTableVO tableVO = new LineageTableVO();
        LineageDataSourceVO sourceVO = new LineageDataSourceVO();
        sourceVO.setAppType(hiveDataSourceTemplate.getAppType());
        sourceVO.setSourceType(DataSourceType.HIVE.getVal());
        sourceVO.setDataInfoId(hiveDataSourceTemplate.getId());
        tableVO.setDataSourceVO(sourceVO);
        tableVO.setTableName("hjl1");
        tableVO.setDbName("dev");
        tableVO.setSchemaName("dev");
        vo.setInputTableInfo(tableVO);
        LineageTableVO tableVO2 = new LineageTableVO();
        tableVO2.setDataSourceVO(sourceVO);
        tableVO2.setTableName("hjl2");
        tableVO2.setDbName("dev");
        tableVO2.setSchemaName("dev");
        vo.setResultTableInfo(tableVO2);
        vo.setAppType(hiveDataSourceTemplate.getAppType());
        vo.setManual(true);
        vo.setUniqueKey(null);
        try {
            lineageService.manualDeleteTableLineage(vo);
        } catch (Exception e) {
            Assert.assertEquals("未找到血缘关系",e.getMessage());
        }
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnInputLineage() {
        LineageDataSource defaultHiveDataSourceTemplate = testLineageDataSourceDao.getOne();
        QueryColumnLineageParam queryTableLineageParam = new QueryColumnLineageParam();
        queryTableLineageParam.setAppType(defaultHiveDataSourceTemplate.getAppType());
        queryTableLineageParam.setDbName("dev");
        queryTableLineageParam.setDtUicTenantId(1L);
        queryTableLineageParam.setSourceName(defaultHiveDataSourceTemplate.getSourceName());
        queryTableLineageParam.setSourceType(defaultHiveDataSourceTemplate.getSourceType());
        queryTableLineageParam.setTableName("test");
        queryTableLineageParam.setColumnName("id");
        queryTableLineageParam.setDataInfoId(defaultHiveDataSourceTemplate.getSourceId());
        queryTableLineageParam.setLevel(1);
        List<LineageColumnColumnVO> queryColumnInputLineage = lineageService.queryColumnInputLineage(queryTableLineageParam);
        Assert.assertNotNull(queryColumnInputLineage);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnResultLineage() {
        LineageDataSource defaultHiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        QueryColumnLineageParam queryTableLineageParam = new QueryColumnLineageParam();
        queryTableLineageParam.setAppType(AppType.DATAASSETS.getType());
        queryTableLineageParam.setDbName("dev");
        queryTableLineageParam.setDtUicTenantId(1L);
        queryTableLineageParam.setSourceName(defaultHiveDataSourceTemplate.getSourceName());
        queryTableLineageParam.setSourceType(defaultHiveDataSourceTemplate.getSourceType());
        queryTableLineageParam.setTableName("test");
        queryTableLineageParam.setColumnName("id");
        queryTableLineageParam.setLevel(1);
        List<LineageColumnColumnVO> queryColumnResultLineage = lineageService.queryColumnResultLineage(queryTableLineageParam);
        Assert.assertNotNull(queryColumnResultLineage);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnLineages() {
        LineageDataSource defaultHiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        QueryColumnLineageParam queryTableLineageParam = new QueryColumnLineageParam();
        queryTableLineageParam.setAppType(AppType.DATAASSETS.getType());
        queryTableLineageParam.setDbName("dev");
        queryTableLineageParam.setDtUicTenantId(1L);
        queryTableLineageParam.setSourceName(defaultHiveDataSourceTemplate.getSourceName());
        queryTableLineageParam.setSourceType(defaultHiveDataSourceTemplate.getSourceType());
        queryTableLineageParam.setTableName("test");
        queryTableLineageParam.setColumnName("id");
        queryTableLineageParam.setLevel(1);
        List<LineageColumnColumnVO> queryColumnLineages = lineageService.queryColumnLineages(queryTableLineageParam);
        Assert.assertNotNull(queryColumnLineages);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualAddColumnLineage() {
        LineageDataSource hiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();

        List<LineageColumnColumnVO> lineageColumnColumnVOs = new ArrayList<>();
        LineageColumnColumnVO columnColumnVO = new LineageColumnColumnVO();
        LineageTableVO inTableVo = new LineageTableVO();
        LineageDataSourceVO sourceVO = new LineageDataSourceVO();
        sourceVO.setAppType(AppType.DATAASSETS.getType());
        sourceVO.setSourceType(DataSourceType.HIVE.getVal());
        sourceVO.setDataInfoId(hiveDataSourceTemplate.getId());
        inTableVo.setSchemaName("dev");
        inTableVo.setDbName("dev");
        inTableVo.setTableName("chener1");
        inTableVo.setDataSourceVO(sourceVO);
        columnColumnVO.setInputTableInfo(inTableVo);
        columnColumnVO.setInputColumnName("id");
        columnColumnVO.setManual(true);
        LineageTableVO resultTableVo = new LineageTableVO();
        resultTableVo.setSchemaName("dev");
        resultTableVo.setDbName("dev");
        resultTableVo.setTableName("chener2");
        resultTableVo.setDataSourceVO(sourceVO);
        columnColumnVO.setResultTableInfo(resultTableVo);
        columnColumnVO.setResultColumnName("id");
        columnColumnVO.setAppType(AppType.DATAASSETS.getType());
        columnColumnVO.setDtUicTenantId(1L);
        columnColumnVO.setUniqueKey(null);
        lineageService.manualAddColumnLineage(lineageColumnColumnVOs);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAcquireOldColumnColumn() {
        LineageDataSource hiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();

        List<LineageColumnColumnVO> lineageColumnColumnVOs = new ArrayList<>();
        LineageColumnColumnVO columnColumnVO = new LineageColumnColumnVO();
        LineageTableVO inTableVo = new LineageTableVO();
        LineageDataSourceVO sourceVO = new LineageDataSourceVO();
        sourceVO.setAppType(AppType.DATAASSETS.getType());
        sourceVO.setSourceType(DataSourceType.HIVE.getVal());
        sourceVO.setDataInfoId(hiveDataSourceTemplate.getId());
        inTableVo.setSchemaName("dev");
        inTableVo.setDbName("dev");
        inTableVo.setTableName("chener1");
        inTableVo.setDataSourceVO(sourceVO);
        columnColumnVO.setInputTableInfo(inTableVo);
        columnColumnVO.setInputColumnName("id");
        columnColumnVO.setManual(true);
        LineageTableVO resultTableVo = new LineageTableVO();
        resultTableVo.setSchemaName("dev");
        resultTableVo.setDbName("dev");
        resultTableVo.setTableName("chener2");
        resultTableVo.setDataSourceVO(sourceVO);
        columnColumnVO.setResultTableInfo(resultTableVo);
        columnColumnVO.setResultColumnName("id");
        columnColumnVO.setAppType(AppType.DATAASSETS.getType());
        columnColumnVO.setDtUicTenantId(1L);
        columnColumnVO.setUniqueKey(null);
        List<LineageColumnColumnVO> list = new ArrayList<>();
        list.add(columnColumnVO);
        lineageService.acquireOldColumnColumn(list);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualDeleteColumnLineage() {
        LineageDataSource hiveDataSourceTemplate = testLineageDataSourceDao.getOne();

        LineageDataSetInfo defaultDataSetInfoTemplate = Template.getDefaultDataSetInfoTemplate();
        defaultDataSetInfoTemplate.setSchemaName("dev");
        defaultDataSetInfoTemplate.setDbName("dev");
        defaultDataSetInfoTemplate.setTableName("hjl1");
        defaultDataSetInfoTemplate.setTableKey("1devhjl1");
        defaultDataSetInfoTemplate.setDataInfoId(hiveDataSourceTemplate.getId());
        testLineageDataSetInfoDao.insert(defaultDataSetInfoTemplate);
        LineageDataSetInfo dataSetInfo = Template.getDefaultDataSetInfoTemplate();
        dataSetInfo.setDbName("dev");
        dataSetInfo.setSchemaName("dev");
        dataSetInfo.setTableName("hjl2");
        dataSetInfo.setTableKey("1devhjl2");
        dataSetInfo.setDataInfoId(hiveDataSourceTemplate.getId());
        testLineageDataSetInfoDao.insert(dataSetInfo);
        LineageColumnColumn columnColumn = Template.getDefaultColumnColumn();
        columnColumn.setInputTableKey("1devhjl1");
        columnColumn.setLineageSource(LineageOriginType.SQL_PARSE.getType());
        columnColumn.setInputColumnName("id");
        columnColumn.setInputTableId(defaultDataSetInfoTemplate.getId());
        columnColumn.setResultTableId(dataSetInfo.getId());
        columnColumn.setResultTableKey(dataSetInfo.getTableKey());
        columnColumn.setResultColumnName("tid");
        String rawKey = String.format("%s.%s_%s.%s", columnColumn.getInputTableId(), columnColumn.getInputColumnName(), columnColumn.getResultTableId(), columnColumn.getResultColumnName());
        columnColumn.setColumnLineageKey(MD5Util.getMd5String(rawKey));
        testLineageColumnColumnDao.insert(columnColumn);

        LineageColumnColumnVO columnColumnVO = new LineageColumnColumnVO();
        LineageTableVO inTableVo = new LineageTableVO();
        LineageDataSourceVO sourceVO = new LineageDataSourceVO();
        sourceVO.setAppType(AppType.DATAASSETS.getType());
        sourceVO.setSourceType(DataSourceType.HIVE.getVal());
        sourceVO.setDataInfoId(hiveDataSourceTemplate.getId());
        inTableVo.setSchemaName("dev");
        inTableVo.setDbName("dev");
        inTableVo.setTableName("hjl1");
        inTableVo.setDataSourceVO(sourceVO);
        columnColumnVO.setInputTableInfo(inTableVo);
        columnColumnVO.setInputColumnName("id");
        columnColumnVO.setManual(true);
        LineageTableVO resultTableVo = new LineageTableVO();
        resultTableVo.setSchemaName("dev");
        resultTableVo.setDbName("dev");
        resultTableVo.setTableName("hjl2");
        resultTableVo.setDataSourceVO(sourceVO);
        columnColumnVO.setResultTableInfo(resultTableVo);
        columnColumnVO.setResultColumnName("tid");
        columnColumnVO.setAppType(hiveDataSourceTemplate.getAppType());
        columnColumnVO.setDtUicTenantId(hiveDataSourceTemplate.getDtUicTenantId());
        columnColumnVO.setUniqueKey(null);
        try {
            lineageService.manualDeleteColumnLineage(columnColumnVO);
        } catch (Exception e) {
            Assert.assertEquals("血缘关系未查到",e.getMessage());
        }
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableInputLineageColumns(){
        LineageDataSource hiveDataSourceTemplate = testLineageDataSourceDao.getOne();

        LineageDataSetInfo defaultDataSetInfoTemplate = Template.getDefaultDataSetInfoTemplate();
        defaultDataSetInfoTemplate.setSchemaName("dev");
        defaultDataSetInfoTemplate.setDbName("dev");
        defaultDataSetInfoTemplate.setTableName("hjl1");
        defaultDataSetInfoTemplate.setTableKey("1devhjl1");
        defaultDataSetInfoTemplate.setDataInfoId(hiveDataSourceTemplate.getId());
        testLineageDataSetInfoDao.insert(defaultDataSetInfoTemplate);
        LineageDataSetInfo dataSetInfo = Template.getDefaultDataSetInfoTemplate();
        dataSetInfo.setDbName("dev");
        dataSetInfo.setSchemaName("dev");
        dataSetInfo.setTableName("hjl2");
        dataSetInfo.setTableKey("1devhjl2");
        dataSetInfo.setDataInfoId(hiveDataSourceTemplate.getId());
        testLineageDataSetInfoDao.insert(dataSetInfo);
        LineageColumnColumn columnColumn = Template.getDefaultColumnColumn();
        columnColumn.setInputTableKey("1devhjl1");
        columnColumn.setLineageSource(LineageOriginType.SQL_PARSE.getType());
        columnColumn.setInputColumnName("id");
        columnColumn.setInputTableId(defaultDataSetInfoTemplate.getId());
        columnColumn.setResultTableId(dataSetInfo.getId());
        columnColumn.setResultTableKey(dataSetInfo.getTableKey());
        columnColumn.setResultColumnName("tid");
        String rawKey = String.format("%s.%s_%s.%s", columnColumn.getInputTableId(), columnColumn.getInputColumnName(), columnColumn.getResultTableId(), columnColumn.getResultColumnName());
        columnColumn.setColumnLineageKey(MD5Util.getMd5String(rawKey));
        testLineageColumnColumnDao.insert(columnColumn);

        QueryTableLineageColumnParam queryTableLineageColumnParam = new QueryTableLineageColumnParam();
        queryTableLineageColumnParam.setAppType(hiveDataSourceTemplate.getAppType());
        queryTableLineageColumnParam.setDbName("dev");
        queryTableLineageColumnParam.setDtUicTenantId(hiveDataSourceTemplate.getDtUicTenantId());
        queryTableLineageColumnParam.setSourceName(hiveDataSourceTemplate.getSourceName());
        queryTableLineageColumnParam.setTableName("hjl1");
        List<String> strings = lineageService.queryTableInputLineageColumns(queryTableLineageColumnParam);
        Assert.assertNotNull(strings);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableResultLineageColumns(){
        QueryTableLineageColumnParam queryTableLineageColumnParam = new QueryTableLineageColumnParam();
        queryTableLineageColumnParam.setAppType(AppType.DATAASSETS.getType());
        queryTableLineageColumnParam.setDbName("dev");
        queryTableLineageColumnParam.setDtUicTenantId(1L);
        queryTableLineageColumnParam.setSourceName("hive");
        queryTableLineageColumnParam.setTableName("test");
        List<String> strings = lineageService.queryTableResultLineageColumns(queryTableLineageColumnParam);
        Assert.assertNotNull(strings);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseFunction(){

        String sql = "select id,nmd(name,age) as nmd from test";
        Set<String> functions = lineageService.parseFunction(sql);
        Assert.assertEquals(1,functions.size());

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParserTables(){

        String sql = "select c.id,c.name from chener c left join tengzhen t on c.id = t.id";
        List<com.dtstack.engine.api.pojo.lineage.Table> tables = lineageService.parseTables(sql, "dev", 31);
        Assert.assertEquals(0,tables.size());
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnLineageByTaskIdAndAppType(){

        List<LineageColumnColumnVO> lineageColumnColumnVOS = lineageService.queryColumnLineageByTaskIdAndAppType(1129L, 1);
        Assert.assertNotNull(lineageColumnColumnVOS);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableLineageByTaskIdAndAppType(){

        List<LineageTableTableVO> lineageTableTableVO = lineageService.queryTableLineageByTaskIdAndAppType(2297L, 1);
        Assert.assertNotNull(lineageTableTableVO);
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDeleteLineageByTaskIdAndAppType(){
        DeleteLineageParam deleteLineageParam = new DeleteLineageParam();
        deleteLineageParam.setAppType(1);
        deleteLineageParam.setTaskId(2297L);
        lineageService.deleteLineageByTaskIdAndAppType(deleteLineageParam);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableResultLineageCountAndLevel(){

        LineageDataSource defaultHiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        QueryTableLineageParam queryTableLineageParam = new QueryTableLineageParam();
        queryTableLineageParam.setAppType(AppType.DATAASSETS.getType());
        queryTableLineageParam.setDbName("dev");
        queryTableLineageParam.setDtUicTenantId(1L);
        queryTableLineageParam.setSourceName(defaultHiveDataSourceTemplate.getSourceName());
        queryTableLineageParam.setSourceType(defaultHiveDataSourceTemplate.getSourceType());
        queryTableLineageParam.setTableName("test");
        queryTableLineageParam.setLevel(1);
        LevelAndCount levelAndCount = lineageService.queryTableResultLineageCountAndLevel(queryTableLineageParam);
        Assert.assertEquals("0",levelAndCount.getLevelCount().toString());
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableInputLineageCountAndLevel(){

        LineageDataSource defaultHiveDataSourceTemplate = Template.getDefaultHiveDataSourceTemplate();
        QueryTableLineageParam queryTableLineageParam = new QueryTableLineageParam();
        queryTableLineageParam.setAppType(AppType.DATAASSETS.getType());
        queryTableLineageParam.setDbName("dev");
        queryTableLineageParam.setDtUicTenantId(1L);
        queryTableLineageParam.setSourceName(defaultHiveDataSourceTemplate.getSourceName());
        queryTableLineageParam.setSourceType(defaultHiveDataSourceTemplate.getSourceType());
        queryTableLineageParam.setTableName("test");
        queryTableLineageParam.setLevel(1);
        LevelAndCount levelAndCount = lineageService.queryTableInputLineageCountAndLevel(queryTableLineageParam);
        Assert.assertEquals("0",levelAndCount.getLevelCount().toString());
    }

}
