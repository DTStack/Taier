package com.dtstack.lineage.impl;

import com.dtstack.engine.api.enums.DataSourceType;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
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
 * @Classname LineageServiceTest
 * @Description unit test for LineageService
 * @Date 2020-11-27 10:33:55
 * @Created basion
 */
public class LineageServiceTest extends AbstractTest {

    @Autowired
    private LineageService lineageService;

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
    public void testParseSql() {
        SqlParseInfo parseSql = lineageService.parseSql("create table chener (id int)", "dev", DataSourceType.HIVE2.getType());
        Assert.assertNotNull(parseSql);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseTableLineage() {
        TableLineageParseInfo parseTableLineage = lineageService.parseTableLineage("create table chener as select * from chener1", "dev", DataSourceType.HIVE2.getType());
        Assert.assertNotNull(parseTableLineage);
        Assert.assertNotNull(parseTableLineage.getTableLineages());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseAndSaveTableLineage() {
        lineageService.parseAndSaveTableLineage(0L, 0, "", "", 0L, 0, "");
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseColumnLineage() {
        ColumnLineageParseInfo parseColumnLineage = lineageService.parseColumnLineage("", 0, "", null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testParseAndSaveColumnLineage() {
        lineageService.parseAndSaveColumnLineage(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableInputLineage() {
        List<LineageTableTableVO> queryTableInputLineage = lineageService.queryTableInputLineage(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableResultLineage() {
        List<LineageTableTableVO> queryTableResultLineage = lineageService.queryTableResultLineage(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryTableLineages() {
        List<LineageTableTableVO> queryTableLineages = lineageService.queryTableLineages(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualAddTableLineage() {
        lineageService.manualAddTableLineage(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAcquireOldTableTable() {
        lineageService.acquireOldTableTable(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualDeleteTableLineage() {
        lineageService.manualDeleteTableLineage(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnInputLineage() {
        List<LineageColumnColumnVO> queryColumnInputLineage = lineageService.queryColumnInputLineage(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnResultLineage() {
        List<LineageColumnColumnVO> queryColumnResultLineage = lineageService.queryColumnResultLineage(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testQueryColumnLineages() {
        List<LineageColumnColumnVO> queryColumnLineages = lineageService.queryColumnLineages(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualAddColumnLineage() {
        lineageService.manualAddColumnLineage(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testAcquireOldColumnColumn() {
        lineageService.acquireOldColumnColumn(null);
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testManualDeleteColumnLineage() {
        lineageService.manualDeleteColumnLineage(null);
        //TODO
    }
}
