package com.dtstack.engine.lineage.impl;

import com.dtstack.engine.api.domain.LineageColumnColumn;
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
 *类名称:LineageColumnColumnServiceTest
 *类描述: 字段血缘service单测类
 *创建人:newman
 *创建时间:2021/4/20 9:49 上午
 *Version 1.0
 */

public class LineageColumnColumnServiceTest extends AbstractTest {

    @Autowired
    private LineageColumnColumnService columnColumnService;

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSaveColumnLineage(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        //测试临时运行
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        //测试非临时运行
        columnColumnService.saveColumnLineage(EScheduleType.NORMAL_SCHEDULE.getType(),lineageColumnColumns,"122");

    }

    private List<LineageColumnColumn> getLineageColumnColumns() {
        LineageColumnColumn columnColumn = new LineageColumnColumn();
        columnColumn.setAppType(1);
        columnColumn.setInputColumnName("id");
        columnColumn.setResultColumnName("id");
        columnColumn.setColumnLineageKey("");
        columnColumn.setDtUicTenantId(1L);
        columnColumn.setInputTableId(1L);
        columnColumn.setResultTableId(2L);
        columnColumn.setInputTableKey("dev_a");
        columnColumn.setResultTableKey("dev_b");
        columnColumn.setColumnLineageKey("id_id");
        columnColumn.setLineageSource(0);
        List<LineageColumnColumn> lineageColumnColumns = Arrays.asList(columnColumn);
        return lineageColumnColumns;
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnInputLineageByAppType(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        List<LineageColumnColumn> lcc = columnColumnService.
                queryColumnInputLineageByAppType(1, columnColumn.getResultTableId(), columnColumn.getResultColumnName(), new HashSet<>(), 2);
        Assert.assertNotNull(lcc);
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnResultLineageByAppType(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        List<LineageColumnColumn> lcc = columnColumnService.
                queryColumnResultLineageByAppType(1, columnColumn.getInputTableId(), columnColumn.getInputColumnName(), new HashSet<>(), 2);
        Assert.assertNotNull(lcc);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnLineages(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        List<LineageColumnColumn> lcc = columnColumnService.
                queryColumnLineages(1, columnColumn.getInputTableId(), columnColumn.getInputColumnName(), 2);
        Assert.assertNotNull(lcc);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualAddColumnLineage(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.manualAddColumnLineage(1, columnColumn, "122", 1);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualDeleteColumnLineage(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        columnColumnService.manualDeleteColumnLineage(1, columnColumn, "111");
    }

    @Test
    public void testGenerateDefaultUniqueKey(){

        String key = columnColumnService.generateDefaultUniqueKey(AppType.RDOS.getType());
        Assert.assertEquals("RDOS",key);
        String key2 = columnColumnService.generateDefaultUniqueKey(12);
        Assert.assertEquals("APP_TYPE_12",key2);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableInputLineageColumns(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        List<String> list = columnColumnService.queryTableInputLineageColumns(columnColumn.getResultTableId());
        Assert.assertNotNull(list);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableResultLineageColumns(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        List<String> list = columnColumnService.queryTableResultLineageColumns(columnColumn.getInputTableId());
        Assert.assertNotNull(list);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnLineageByTaskIdAndAppType(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        List<LineageColumnColumn> lcc = columnColumnService.queryColumnLineageByTaskIdAndAppType(111L, 1);
        Assert.assertNotNull(lcc);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testDeleteLineageByTaskIdAndAppType(){

        List<LineageColumnColumn> lineageColumnColumns = getLineageColumnColumns();
        LineageColumnColumn columnColumn = lineageColumnColumns.get(0);
        columnColumnService.saveColumnLineage(EScheduleType.TEMP_JOB.getType(),lineageColumnColumns,"111");
        DeleteLineageParam deleteLineageParam = new DeleteLineageParam();
        deleteLineageParam.setTaskId(111L);
        deleteLineageParam.setAppType(1);
        columnColumnService.deleteLineageByTaskIdAndAppType(deleteLineageParam);
    }
}


