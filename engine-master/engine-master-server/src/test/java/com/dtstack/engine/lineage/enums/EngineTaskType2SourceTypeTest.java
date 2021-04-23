package com.dtstack.engine.lineage.enums;

import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/20 16:48
 * Description: 单测
 * @since 1.0.0
 */
public class EngineTaskType2SourceTypeTest {


    @Test
    public void testGetDataSourceType(){

        DataSourceType dataSourceType = EngineTaskType2SourceType.HIVE1.getDataSourceType();
        Assert.assertNotNull(dataSourceType);
    }

    @Test
    public void testGetTaskType(){

        EScheduleJobType taskType = EngineTaskType2SourceType.HIVE2.getTaskType();
        Assert.assertEquals(EScheduleJobType.HIVE_SQL,taskType);

    }

    @Test
    public void testGetDataSourceTypeByTaskTypeInt(){

        DataSourceType dataSourceType = EngineTaskType2SourceType.getDataSourceTypeByTaskTypeInt(17);
        Assert.assertEquals(DataSourceType.HIVE,dataSourceType);
    }

    @Test
    public void testGetDataSourceTypeByTaskType(){

        DataSourceType dataSourceType = EngineTaskType2SourceType.getDataSourceTypeByTaskType(EScheduleJobType.LIBRA_SQL);
        Assert.assertEquals(DataSourceType.LIBRA,dataSourceType);
    }




}
