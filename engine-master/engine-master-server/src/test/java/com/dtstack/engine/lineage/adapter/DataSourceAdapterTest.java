package com.dtstack.engine.lineage.adapter;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
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

        DsServiceInfoDTO dataSource = new DsServiceInfoDTO();
        LineageDataSourceVO lineageDataSourceVO = DataSourceAdapter.dataSource2DataSourceVO(dataSource);
        Assert.assertNotNull(lineageDataSourceVO);
    }
}
