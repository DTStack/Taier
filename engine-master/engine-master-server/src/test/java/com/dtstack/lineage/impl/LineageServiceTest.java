package com.dtstack.lineage.impl;

import com.dtstack.engine.api.enums.DataSourceType;
import com.dtstack.engine.api.pojo.lineage.TableLineage;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.LineageColumnColumnVO;
import com.dtstack.engine.api.vo.lineage.LineageTableTableVO;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.engine.api.vo.lineage.TableLineageParseInfo;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.schedule.common.enums.AppType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.jdo.annotations.Transactional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author chener
 * @Classname LineageServiceTest
 * @Description TODO
 * @Date 2020/11/16 14:17
 * @Created chener@dtstack.com
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
    public void testParseSql() {
        SqlParseInfo parseSql = lineageService.parseSql("create table chener_1 as select * from chener_o1 o1 join chener_o2 o2 on o1.id = o2.id", "dev", DataSourceType.SPARK_THRIFT.getType());
        Assert.assertEquals(parseSql.getSqlType(), SqlType.CREATE_AS);
    }

    @Test
    public void testParseTableLineage() {
        TableLineageParseInfo parseTableLineage = lineageService.parseTableLineage("create table chener_1 as select * from chener_o1 o1 join chener_o2 o2 on o1.id = o2.id", "dev", DataSourceType.SPARK_THRIFT.getType());
        List<TableLineage> tableLineages = parseTableLineage.getTableLineages();
        Assert.assertEquals(tableLineages.size(), 2);
    }

    @Test
    @Transactional
    @Rollback
    public void testParseAndSaveTableLineage() {
        lineageService.parseAndSaveTableLineage(0L, AppType.RDOS.getType(), "create table chener_1 as select * from chener_o1 o1 join chener_o2 o2 on o1.id = o2.id", "dev", 0L, DataSourceType.SPARK_THRIFT.getType(), "11");
    }

    @Test
    public void testParseColumnLineage() {
        ColumnLineageParseInfo parseColumnLineage = lineageService.parseColumnLineage("", 0, "", null);
        //TODO
    }

    @Test
    public void testParseAndSaveColumnLineage() {
        lineageService.parseAndSaveColumnLineage(null);
        //TODO
    }

    @Test
    public void testQueryTableInputLineage() {
        List<LineageTableTableVO> queryTableInputLineage = lineageService.queryTableInputLineage(null);
        //TODO
    }

    @Test
    public void testQueryTableResultLineage() {
        List<LineageTableTableVO> queryTableResultLineage = lineageService.queryTableResultLineage(null);
        //TODO
    }

    @Test
    public void testQueryTableLineages() {
        List<LineageTableTableVO> queryTableLineages = lineageService.queryTableLineages(null);
        //TODO
    }

    @Test
    public void testManualAddTableLineage() {
        lineageService.manualAddTableLineage(null);
        //TODO
    }

    @Test
    public void testManualDeleteTableLineage() {
        lineageService.manualDeleteTableLineage(null);
        //TODO
    }

    @Test
    public void testQueryColumnInoutLineage() {
        List<LineageColumnColumnVO> queryColumnInoutLineage = lineageService.queryColumnInputLineage(null);
        //TODO
    }

    @Test
    public void testQueryColumnResultLineage() {
        List<LineageColumnColumnVO> queryColumnResultLineage = lineageService.queryColumnResultLineage(null);
        //TODO
    }

    @Test
    public void testQueryColumnLineages() {
        List<LineageColumnColumnVO> queryColumnLineages = lineageService.queryColumnLineages(null);
        //TODO
    }

    @Test
    public void testManualAddColumnLineage() {
        lineageService.manualAddColumnLineage(null);
        //TODO
    }

    @Test
    public void testManualDeleteColumnLineage() {
        lineageService.manualDeleteColumnLineage(null);
        //TODO
    }
}
