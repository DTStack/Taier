package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.schedule.common.enums.AppType;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.jdo.annotations.Transactional;
import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableServiceTest
 * @Description TODO
 * @Date 2020/11/16 14:14
 * @Created chener@dtstack.com
 */
public class LineageTableTableServiceTest extends AbstractTest {
    @Autowired
    private LineageTableTableService lineageTableTableService;

    /**
     *  do some mock before test
     */
    @Before
    public void setup() throws Exception{
        //TODO
    }

    @Test
    @Transactional
    @Rollback
    public void testSaveTableLineage(){
        LineageTableTable tableTable = new LineageTableTable();
        tableTable.setDtUicTenantId(1L);
        tableTable.setAppType(AppType.RDOS.getType());
        tableTable.setInputTableId(1L);
        tableTable.setInputTableKey("1_1");
        tableTable.setResultTableId(1L);
        tableTable.setResultTableKey("1_1");
        tableTable.setTableLineageKey("1_1#1_1");
        lineageTableTableService.saveTableLineage(Lists.newArrayList(tableTable) );
        //TODO
    }
    @Test
    public void testQueryTableInputLineageByAppType(){
        List<LineageTableTable> queryTableInputLineageByAppType = lineageTableTableService.queryTableInputLineageByAppType(0L , 0 );
        //TODO
    }
    @Test
    public void testQueryTableResultLineageByAppType(){
        List<LineageTableTable> queryTableResultLineageByAppType = lineageTableTableService.queryTableResultLineageByAppType(0L , 0 );
        //TODO
    }
    @Test
    public void testQueryTableTableByTableAndAppId(){
        List<LineageTableTable> queryTableTableByTableAndAppId = lineageTableTableService.queryTableTableByTableAndAppId(0 , 0L );
        //TODO
    }
    @Test
    public void testManualAddTableLineage(){
        lineageTableTableService.manualAddTableLineage(0 , null );
        //TODO
    }
    @Test
    public void testManualDeleteTableLineage(){
        lineageTableTableService.manualDeleteTableLineage(0 , null );
        //TODO
    }
    @Test
    public void testGenerateTableTableKey(){
        String generateTableTableKey = lineageTableTableService.generateTableTableKey(null );
        //TODO
    }
    @Test
    public void testGenerateDefaultUniqueKey(){
        String generateDefaultUniqueKey = lineageTableTableService.generateDefaultUniqueKey(0 );
        //TODO
    }
}
