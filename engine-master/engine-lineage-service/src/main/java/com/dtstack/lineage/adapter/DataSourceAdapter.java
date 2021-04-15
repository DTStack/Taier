package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.domain.LineageDataSource;
import com.dtstack.engine.api.vo.lineage.LineageDataSourceVO;
import com.dtstack.schedule.common.enums.AppType;

/**
 * @author chener
 * @Classname DataSourceAdapter
 * @Description
 * @Date 2020/11/10 15:03
 * @Created chener@dtstack.com
 */
public class DataSourceAdapter {
    public static LineageDataSourceVO dataSource2DataSourceVO(LineageDataSource lineageDataSource){
        LineageDataSourceVO dataSourceVO = new LineageDataSourceVO();
        dataSourceVO.setAppType(lineageDataSource.getAppType());
        Long sourceId = AppType.DATAASSETS.getType().equals(lineageDataSource.getAppType()) ? lineageDataSource.getId() : lineageDataSource.getSourceId();
        dataSourceVO.setSourceId(sourceId);
        dataSourceVO.setSourceName(lineageDataSource.getSourceName());
        dataSourceVO.setSourceType(lineageDataSource.getSourceType());
        return dataSourceVO;
    }
}
