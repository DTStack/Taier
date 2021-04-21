package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: ZYD
 * Date: 2021/4/20 15:47
 * Description: 数据源转换器
 * @since 1.0.0
 */
public class DataSourceAdapterTest {


    @Test
    public void testDataSource2DataSourceVO(){

        LineageDataSource dataSource = new LineageDataSource();
        dataSource.setAppType(1);
        LineageDataSourceVO lineageDataSourceVO = DataSourceAdapter.dataSource2DataSourceVO(dataSource);
        Assert.assertNotNull(lineageDataSourceVO);
    }
}
