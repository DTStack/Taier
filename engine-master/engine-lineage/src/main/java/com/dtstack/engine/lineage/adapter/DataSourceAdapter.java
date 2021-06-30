package com.dtstack.engine.lineage.adapter;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.schedule.common.enums.AppType;

/**
 * @author chener
 * @Classname DataSourceAdapter
 * @Description
 * @Date 2020/11/10 15:03
 * @Created chener@dtstack.com
 */
public class DataSourceAdapter {
    public static LineageDataSourceVO dataSource2DataSourceVO(DsServiceInfoDTO lineageDataSource){
        LineageDataSourceVO dataSourceVO = new LineageDataSourceVO();
        dataSourceVO.setDataInfoId(lineageDataSource.getDataInfoId());
        dataSourceVO.setSourceName(lineageDataSource.getDataName());
        dataSourceVO.setSourceType(lineageDataSource.getType());
        return dataSourceVO;
    }
}
