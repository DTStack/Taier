package com.dtstack.lineage.impl;


import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.lineage.dao.LineageDataSetDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
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


    @Before
    public void setup() throws Exception{


        ReflectionTestUtils.setField(dataSetInfoService,"sourceService", dataSourceService);
        ReflectionTestUtils.setField(dataSetInfoService,"tenantDao", tenantDao);
        ReflectionTestUtils.setField(dataSetInfoService,"lineageDataSetDao", lineageDataSetDao);
        ReflectionTestUtils.setField(dataSetInfoService,"componentDao", componentDao);
        ReflectionTestUtils.setField(dataSetInfoService,"tenantDao", tenantDao);
        ReflectionTestUtils.setField(dataSetInfoService,"componentConfigDao", componentConfigDao);
        when(dataSetInfoService.getClient(any(),any(),any())).thenReturn(null);
        when(dataSetInfoService.getAllColumns(any(),any())).thenReturn(new ArrayList<>());

    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetOneBySourceIdAndDbNameAndTableName(){

        LineageDataSource dataSource = DataCollection.getData().getDefaultLineageDataSource();
        String dbName = "default";
        String tableName = "t1";
        String schemaName = "t1";
        LineageDataSetInfo dataSetInfo = dataSetInfoService.getOneBySourceIdAndDbNameAndTableName(dataSource.getId(), dbName, tableName, schemaName);
        Assert.assertNotNull(dataSetInfo);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetTableColumns(){

        LineageDataSource dataSource = DataCollection.getData().getDefaultLineageDataSource();
        LineageDataSetInfo dataSetInfo = new LineageDataSetInfo();
        dataSetInfo.setSourceId(dataSource.getId());
        dataSetInfo.setTableName("chener");
        dataSetInfo.setDbName("beihai");
        dataSetInfo.setSchemaName("beihai");
        List<Column> tableColumns = dataSetInfoService.getTableColumns(dataSetInfo);
        Assert.assertNotNull(tableColumns);
    }



}


