package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author basion
 * @Classname LineageTableTableServiceTest
 * @Description unit test for LineageTableTableService
 * @Date 2020-11-27 10:36:29
 * @Created basion
 */
public class LineageTableTableServiceTest extends AbstractTest {

    @Autowired
    private LineageTableTableService lineageTableTableService;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSaveTableLineage() {
        LineageTableTable defaultTableTable = Template.getDefaultTableTable();
        List<LineageTableTable> tt = new ArrayList<>();
        tt.add(defaultTableTable);
        lineageTableTableService.saveTableLineage(tt, "22");
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableInputLineageByAppType() {
        LineageDataSetInfo defaultDataSetInfoTemplate = Template.getDefaultDataSetInfoTemplate();
        List<LineageTableTable> queryTableInputLineageByAppType = lineageTableTableService.queryTableInputLineageByAppType(defaultDataSetInfoTemplate.getId(), defaultDataSetInfoTemplate.getAppType(), null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableResultLineageByAppType() {
        List<LineageTableTable> queryTableResultLineageByAppType = lineageTableTableService.queryTableResultLineageByAppType(0L, 0, null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableTableByTableAndAppId() {
        List<LineageTableTable> queryTableTableByTableAndAppId = lineageTableTableService.queryTableTableByTableAndAppId(0, 0L);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualAddTableLineage() {
        lineageTableTableService.manualAddTableLineage(0, null, "",null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualDeleteTableLineage() {
        lineageTableTableService.manualDeleteTableLineage(0, null, "");
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGenerateTableTableKey() {
        String generateTableTableKey = lineageTableTableService.generateTableTableKey(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGenerateDefaultUniqueKey() {
        String generateDefaultUniqueKey = lineageTableTableService.generateDefaultUniqueKey(0);
        //TODO
    }
}
