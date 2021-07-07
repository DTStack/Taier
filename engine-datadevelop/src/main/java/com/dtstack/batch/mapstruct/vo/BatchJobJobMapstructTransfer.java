package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.web.job.vo.result.BatchScheduleJobResultVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BatchJobJobMapstructTransfer {

    BatchJobJobMapstructTransfer INSTANCE = Mappers.getMapper(BatchJobJobMapstructTransfer.class);

    /**
     * ScheduleJobVO --> BatchScheduleJobResultVO
     *
     * @param scheduleJobVO
     * @return
     */
    BatchScheduleJobResultVO scheduleJobVOToBatchScheduleJobResultVO(ScheduleJobVO scheduleJobVO);
}
