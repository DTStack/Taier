package com.dtstack.taier.develop.mapstruct.console;

import com.dtstack.taier.develop.dto.devlop.StreamTaskMetricDTO;
import com.dtstack.taier.develop.vo.develop.query.GetTaskMetricsVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:55 2021/1/6
 * @Description：数据源信息转化
 */
@Mapper(builder = @Builder(disableBuilder = true), nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StreamJobMetricTransfer {
    StreamJobMetricTransfer INSTANCE = Mappers.getMapper(StreamJobMetricTransfer.class);

    StreamTaskMetricDTO getTaskMetrics(GetTaskMetricsVO vo);
}
