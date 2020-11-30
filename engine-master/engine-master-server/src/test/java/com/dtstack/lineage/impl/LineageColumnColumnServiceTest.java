package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.dtstack.engine.master.AbstractTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author basion
 * @Classname LineageColumnColumnServiceTest
 * @Description unit test for LineageColumnColumnService
 * @Date 2020-11-27 10:35:35
 * @Created basion
 */
public class LineageColumnColumnServiceTest extends AbstractTest {

    @Autowired
    private LineageColumnColumnService lineageColumnColumnService;

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
    public void testSaveColumnLineage() {
        lineageColumnColumnService.saveColumnLineage(null, "");
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnInputLineageByAppType() {
        List<LineageColumnColumn> queryColumnInputLineageByAppType = lineageColumnColumnService.queryColumnInputLineageByAppType(0, 0L, "", null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnResultLineageByAppType() {
        List<LineageColumnColumn> queryColumnResultLineageByAppType = lineageColumnColumnService.queryColumnResultLineageByAppType(0, 0L, "", null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnLineages() {
        List<LineageColumnColumn> queryColumnLineages = lineageColumnColumnService.queryColumnLineages(0, 0L, "");
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualAddColumnLineage() {
        lineageColumnColumnService.manualAddColumnLineage(0, null, "");
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualDeleteColumnLineage() {
        lineageColumnColumnService.manualDeleteColumnLineage(0, null, "");
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGenerateDefaultUniqueKey() {
        String generateDefaultUniqueKey = lineageColumnColumnService.generateDefaultUniqueKey(0);
        //TODO
    }
}
