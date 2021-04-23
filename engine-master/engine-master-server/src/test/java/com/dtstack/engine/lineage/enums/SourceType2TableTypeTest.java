package com.dtstack.engine.lineage.enums;

import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.schedule.common.enums.ETableType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/20 17:07
 * Description: 单测
 * @since 1.0.0
 */
public class SourceType2TableTypeTest {


    @Test
    public void testGetByTableType(){

        SourceType2TableType tableType = SourceType2TableType.getByTableType(ETableType.HIVE.getType());
        Assert.assertEquals(SourceType2TableType.HIVE,tableType);
    }

    @Test
    public void testGetBySourceType(){

        SourceType2TableType bySourceType = SourceType2TableType.getBySourceType(DataSourceType.LIBRA);
        Assert.assertEquals(SourceType2TableType.LIBRA,bySourceType);
    }
}
