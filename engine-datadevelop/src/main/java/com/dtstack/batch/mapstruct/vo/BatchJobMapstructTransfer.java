package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.batch.web.job.vo.result.*;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface BatchJobMapstructTransfer {
    BatchJobMapstructTransfer INSTANCE = Mappers.getMapper(BatchJobMapstructTransfer.class);

    /**
     * ExecuteSqlParseVO --> BatchExecuteSqlParseResultVO
     *
     * @param executeSqlParseVO
     * @return
     */
    BatchExecuteSqlParseResultVO executeSqlParseVOToBatchExecuteSqlParseResultVO(ExecuteSqlParseVO executeSqlParseVO);

    /**
     * ExecuteResultVO --> BatchExecuteResultVO
     *
     * @param executeResultVO
     * @return
     */
    BatchExecuteResultVO executeResultVOToBatchExecuteResultVO(ExecuteResultVO executeResultVO);


    /**
     * ScheduleJobExeStaticsVO --> BatchScheduleJobExeStaticsResultVO
     *
     * @param scheduleJobExeStaticsVO
     * @return
     */
    BatchScheduleJobExeStaticsResultVO scheduleJobExeStaticsVOToBatchScheduleJobExeStaticsResultVO(ScheduleJobExeStaticsVO scheduleJobExeStaticsVO);

    /**
     * Map<String, ScheduleJob> --> BMap<String, BatchGetLabTaskRelationMapResultVO>
     *
     * @param scheduleJobMap
     * @return
     */
    Map<String, BatchGetLabTaskRelationMapResultVO> scheduleJobMapToBatchGetLabTaskRelationMapResultVOMap(Map<String, ScheduleJob> scheduleJobMap);

    /**
     * ScheduleDetailsVO --> BatchJobFindTaskRuleJobResultVO
     * @param scheduleDetailsVO
     * @return
     */
    BatchJobFindTaskRuleJobResultVO scheduleDetailsVOToBatchJobFindTaskRuleJobResultVO(ScheduleDetailsVO scheduleDetailsVO);

}
