package com.dtstack.engine.lineage.impl;


import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.pojo.LevelAndCount;
import com.dtstack.engine.api.vo.lineage.param.DeleteLineageParam;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.schedule.common.enums.AppType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 *类名称:LineageTableTableServiceTest
 *类描述:表血缘service单测
 *创建人:newman
 *创建时间:2021/4/20 10:58 上午
 *Version 1.0
 */

public class LineageTableTableServiceTest extends AbstractTest {

    @Autowired
    private LineageTableTableService tableTableService;

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSaveTableLineage(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");

        tableTableService.saveTableLineage(0,EScheduleType.NORMAL_SCHEDULE.getType(),lineageTableTables,"222");

    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableInputLineageDirectCount(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");
        Integer directCount = tableTableService.queryTableInputLineageDirectCount(lineageTableTable.getResultTableId(), AppType.RDOS.getType());
        Assert.assertEquals("1",directCount.toString());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableInputLineageByAppType(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");
        LevelAndCount levelAndCount = new LevelAndCount();
        levelAndCount.setLevelCount(1);
        List<LineageTableTable> ltt = tableTableService.queryTableInputLineageByAppType(lineageTableTable.getResultTableId(), AppType.RDOS.getType(), new HashSet<>(), levelAndCount);
        Assert.assertNotNull(ltt);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableResultLineageDirectCount(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");
        Integer directCount = tableTableService.queryTableResultLineageDirectCount(lineageTableTable.getInputTableId(), AppType.RDOS.getType());
        Assert.assertEquals("1",directCount.toString());
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableResultLineageByAppType(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");
        LevelAndCount levelAndCount = new LevelAndCount();
        levelAndCount.setLevelCount(1);
        List<LineageTableTable> ltt = tableTableService.queryTableResultLineageByAppType(lineageTableTable.getInputTableId(), AppType.RDOS.getType(), new HashSet<>(), levelAndCount);
        Assert.assertNotNull(ltt);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableTableByTableAndAppId(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");
        List<LineageTableTable> ltt = tableTableService.queryTableTableByTableAndAppId( AppType.RDOS.getType(),lineageTableTable.getInputTableId(),1);
        Assert.assertNotNull(ltt);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualAddTableLineage(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        tableTableService.manualAddTableLineage( AppType.RDOS.getType(),lineageTableTable,"111",1);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualDeleteTableLineage(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");

        tableTableService.manualDeleteTableLineage(AppType.RDOS.getType(), lineageTableTable,"111");

    }

    @Test
    public void testGenerateTableTableKey(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        String key = tableTableService.generateTableTableKey(lineageTableTable);
        Assert.assertEquals("1_2",key);

    }

    @Test
    public void testGenerateDefaultUniqueKey(){

        String key1 = tableTableService.generateDefaultUniqueKey(AppType.RDOS.getType());
        Assert.assertEquals("RDOS",key1);
        String key2 = tableTableService.generateDefaultUniqueKey(12);
        Assert.assertEquals("APP_TYPE_12",key2);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableLineageByTaskIdAndAppType(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");

        List<LineageTableTable> ltt = tableTableService.queryTableLineageByTaskIdAndAppType(111L, 1);
        Assert.assertNotNull(ltt);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDeleteLineageByTaskIdAndAppType(){

        LineageTableTable lineageTableTable = getLineageTableTable();
        List<LineageTableTable> lineageTableTables = Arrays.asList(lineageTableTable);
        tableTableService.saveTableLineage(null,EScheduleType.TEMP_JOB.getType(),lineageTableTables,"111");
        DeleteLineageParam deleteLineageParam = new DeleteLineageParam();
        deleteLineageParam.setAppType(1);
        deleteLineageParam.setTaskId(111L);
        tableTableService.deleteLineageByTaskIdAndAppType(deleteLineageParam);
    }


    private LineageTableTable getLineageTableTable() {
        LineageTableTable lineageTableTable = new LineageTableTable();
        lineageTableTable.setAppType(AppType.RDOS.getType());
        lineageTableTable.setDtUicTenantId(1L);
        lineageTableTable.setInputTableKey("dev_beihai");
        lineageTableTable.setInputTableId(1L);
        lineageTableTable.setResultTableKey("dev_tengzhen");
        lineageTableTable.setResultTableId(2L);
        lineageTableTable.setTableLineageKey(tableTableService.generateTableTableKey(lineageTableTable));
        lineageTableTable.setLineageSource(0);
        return lineageTableTable;
    }




}


