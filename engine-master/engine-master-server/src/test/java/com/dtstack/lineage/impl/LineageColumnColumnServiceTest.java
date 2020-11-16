package com.dtstack.lineage.impl;

import com.dtstack.engine.api.domain.LineageColumnColumn;
import com.dtstack.engine.master.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author chener
 * @Classname LineageColumnColumnServiceTest
 * @Description TODO
 * @Date 2020/11/16 13:53
 * @Created chener@dtstack.com
 */
public class LineageColumnColumnServiceTest extends AbstractTest {
    @Autowired
    private LineageColumnColumnService lineageColumnColumnService;

    /**
     *  do some mock before test
     */
    @Before
    public void setup() throws Exception{
        //TODO
    }

    @Test
    public void testSaveColumnLineage(){
        lineageColumnColumnService.saveColumnLineage(null );
        //TODO
    }
    @Test
    public void testQueryColumnInputLineageByAppType(){
        List<LineageColumnColumn> queryColumnInputLineageByAppType = lineageColumnColumnService.queryColumnInputLineageByAppType(0 , 0L , "" );
        //TODO
    }
    @Test
    public void testQueryColumnResultLineageByAppType(){
        List<LineageColumnColumn> queryColumnResultLineageByAppType = lineageColumnColumnService.queryColumnResultLineageByAppType(0 , 0L , "" );
        //TODO
    }
    @Test
    public void testQueryColumnLineages(){
        List<LineageColumnColumn> queryColumnLineages = lineageColumnColumnService.queryColumnLineages(0 , 0L , "" );
        //TODO
    }
    @Test
    public void testManualAddColumnLineage(){
        lineageColumnColumnService.manualAddColumnLineage(0 , null );
        //TODO
    }
    @Test
    public void testManualDeleteColumnLineage(){
        lineageColumnColumnService.manualDeleteColumnLineage(0 , null );
        //TODO
    }
    @Test
    public void testGenerateDefaultUniqueKey(){
        String generateDefaultUniqueKey = lineageColumnColumnService.generateDefaultUniqueKey(0 );
        //TODO
    }
}
