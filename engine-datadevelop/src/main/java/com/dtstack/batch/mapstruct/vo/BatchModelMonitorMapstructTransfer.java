package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.BatchTableSearchVO;
import com.dtstack.batch.vo.ChartDataVO;
import com.dtstack.batch.web.model.vo.query.BatchModelMonitorDataPageQueryVO;
import com.dtstack.batch.web.model.vo.result.BatchChartDataResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchModelMonitorMapstructTransfer {

    BatchModelMonitorMapstructTransfer INSTANCE = Mappers.getMapper(BatchModelMonitorMapstructTransfer.class);

    /**
     * BatchModelMonitorDataPageQueryVO -> BatchTableSearchVO
     *
     * @param pageQueryVO
     * @return
     */
    @Mappings({
            @Mapping(source = "sizeOrder",target = "tableSizeOrder")
    })
    BatchTableSearchVO PageQueryVOToTableSearchVO(BatchModelMonitorDataPageQueryVO pageQueryVO);

    /**
     * ChartDataVO -> BatchChartDataResultVO
     *
     * @param chartDataVO
     * @return
     */
    BatchChartDataResultVO chartDataVOToBatchChartDataResultVO(ChartDataVO chartDataVO);
}
