package com.dtstack.engine.lineage.impl;


import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.dao.LineageDataSetDao;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.sdk.core.common.ApiResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 *类名称:LineageDataSetInfoTest
 *类描述: LineageDataSetInfo 单测
 *创建人:newman
 *创建时间:2021/4/19 5:24 下午
 *Version 1.0
 */

public class LineageDataSetInfoTest extends AbstractTest {

    @Spy
    private LineageDataSetInfoService dataSetInfoService;

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

    @Autowired
    private EnvironmentContext environmentContext;

    @Mock
    private DataSourceAPIClient dataSourceAPIClient;


    @Before
    public void setup() throws Exception{


        ReflectionTestUtils.setField(dataSetInfoService,"tenantDao", tenantDao);
        ReflectionTestUtils.setField(dataSetInfoService,"lineageDataSetDao", lineageDataSetDao);
        ReflectionTestUtils.setField(dataSetInfoService,"componentDao", componentDao);
        ReflectionTestUtils.setField(dataSetInfoService,"tenantDao", tenantDao);
        ReflectionTestUtils.setField(dataSetInfoService,"componentConfigDao", componentConfigDao);
        ReflectionTestUtils.setField(dataSetInfoService,"environmentContext", environmentContext);
        ReflectionTestUtils.setField(dataSetInfoService,"dataSourceAPIClient",dataSourceAPIClient);
        ApiResponse<DsServiceInfoDTO> dsInfoById = new ApiResponse<>();
        DsServiceInfoDTO dsServiceInfoDTO = new DsServiceInfoDTO();
        dsServiceInfoDTO.setDataInfoId(1L);
        dsServiceInfoDTO.setType(1);
        dsServiceInfoDTO.setDtuicTenantId(1L);
        dsServiceInfoDTO.setDataName("测试数据源");
        dsInfoById.setCode(1);
        dsInfoById.setData(dsServiceInfoDTO);
        when(dataSourceAPIClient.getDsInfoById(any())).thenReturn(dsInfoById);
        when(dataSetInfoService.getAllColumns(any(),any(),any())).thenReturn(new ArrayList<>());

    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetOneBySourceIdAndDbNameAndTableName(){

        LineageDataSetInfo dataSetInfo = DataCollection.getData().getHiveLineageDataSetInfo();
        String dbName = dataSetInfo.getDbName();
        String tableName = dataSetInfo.getTableName();
        String schemaName = dataSetInfo.getSchemaName();
        LineageDataSetInfo setInfo = dataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSetInfo.getDataInfoId(), dbName, tableName, schemaName,dataSetInfo.getAppType());
        Assert.assertNotNull(setInfo);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetOneBySourceIdAndDbNameAndTableName2(){

        LineageDataSetInfo dataSetInfo = DataCollection.getData().getHiveLineageDataSetInfo();
        String dbName = dataSetInfo.getDbName();
        String tableName = dataSetInfo.getTableName();
        String schemaName = dataSetInfo.getSchemaName();
        LineageDataSetInfo setInfo = dataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSetInfo.getDataInfoId(), dbName, tableName, schemaName,1);
        Assert.assertNotNull(setInfo);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetTableColumns(){

        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        dataSetInfo.setDataInfoId(1L);
        dataSetInfo.setTableName("chener");
        dataSetInfo.setDbName("beihai");
        dataSetInfo.setSchemaName("beihai");
        List<Column> tableColumns = dataSetInfoService.getTableColumns(dataSetInfo);
        Assert.assertNotNull(tableColumns);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetOneById(){

        LineageDataSetInfo dataSetInfo = DataCollection.getData().getHiveLineageDataSetInfo();
        String dbName = dataSetInfo.getDbName();
        String tableName = dataSetInfo.getTableName();
        String schemaName = dataSetInfo.getSchemaName();
        LineageDataSetInfo setInfo = dataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSetInfo.getDataInfoId(), dbName, tableName, schemaName,dataSetInfo.getAppType());
        LineageDataSetInfo oneById = dataSetInfoService.getOneById(setInfo.getId());
        Assert.assertNotNull(oneById);
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetDataSetListByIds(){

        LineageDataSetInfo dataSetInfo = DataCollection.getData().getHiveLineageDataSetInfo();
        String dbName = dataSetInfo.getDbName();
        String tableName = dataSetInfo.getTableName();
        String schemaName = dataSetInfo.getSchemaName();
        LineageDataSetInfo setInfo = dataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSetInfo.getDataInfoId(), dbName, tableName, schemaName,dataSetInfo.getAppType());
        List<LineageDataSetInfo> dataSetListByIds = dataSetInfoService.getDataSetListByIds(Arrays.asList(setInfo.getId()));
        Assert.assertNotNull(dataSetListByIds);
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetColumnsBySourceIdAndListTable(){

        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        dataSetInfo.setDataInfoId(1L);
        dataSetInfo.setTableName("chener");
        dataSetInfo.setDbName("beihai");
        dataSetInfo.setSchemaName("beihai");
        List<Table> tables = new ArrayList<>();
        Table table = new Table();
        table.setDb("beihai");
        table.setName("chener");
        tables.add(table);
        Map<String, List<Column>> columnsBySourceIdAndListTable = dataSetInfoService.getColumnsBySourceIdAndListTable(1L, tables);
        Assert.assertNotNull(columnsBySourceIdAndListTable);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void test(){

        LineageDataSetInfo dataSetInfo = DataCollection.getData().getHiveLineageDataSetInfo();
        LineageDataSetInfo setInfo = dataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSetInfo.getDataInfoId(), dataSetInfo.getDbName(), dataSetInfo.getTableName(), dataSetInfo.getSchemaName(),dataSetInfo.getAppType());
        dataSetInfoService.updateTableNameByTableNameAndSourceId(setInfo.getTableName(),"t2",setInfo.getDbName(),setInfo.getDataInfoId());
    }

}


