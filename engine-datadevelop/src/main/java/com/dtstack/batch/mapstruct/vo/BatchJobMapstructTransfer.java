package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.BatchOperatorVO;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.batch.vo.KillJobVo;
import com.dtstack.batch.web.job.vo.query.BatchJobGetFillDataDetailInfoVO;
import com.dtstack.batch.web.job.vo.query.BatchJobGetRestartChildJobVO;
import com.dtstack.batch.web.job.vo.query.BatchJobKillJobVO;
import com.dtstack.batch.web.job.vo.query.BatchJobQueryJobVO;
import com.dtstack.batch.web.job.vo.query.BatchJobQueryJobsStatusStatisticsVO;
import com.dtstack.batch.web.job.vo.result.BatchExecuteResultVO;
import com.dtstack.batch.web.job.vo.result.BatchExecuteSqlParseResultVO;
import com.dtstack.batch.web.job.vo.result.BatchFillDataRecordResultVO;
import com.dtstack.batch.web.job.vo.result.BatchGetLabTaskRelationMapResultVO;
import com.dtstack.batch.web.job.vo.result.BatchJobFindTaskRuleJobResultVO;
import com.dtstack.batch.web.job.vo.result.BatchJobTopErrorResultVO;
import com.dtstack.batch.web.job.vo.result.BatchJobTopOrderResultVO;
import com.dtstack.batch.web.job.vo.result.BatchOperatorResultVO;
import com.dtstack.batch.web.job.vo.result.BatchRestartJobResultVO;
import com.dtstack.batch.web.job.vo.result.BatchScheduleFillDataJobDetailResultVO;
import com.dtstack.batch.web.job.vo.result.BatchScheduleGetByJobIdResultVO;
import com.dtstack.batch.web.job.vo.result.BatchScheduleJobExeStaticsResultVO;
import com.dtstack.batch.web.job.vo.result.BatchScheduleJobResultVO;
import com.dtstack.batch.web.job.vo.result.BatchSchedulePeriodInfoResultVO;
import com.dtstack.batch.web.job.vo.result.BatchScheduleRunDetailResultVO;
import com.dtstack.batch.web.job.vo.result.ScheduleFillDataJobPreViewResultVO;
import com.dtstack.batch.web.model.vo.result.BatchChartDataResultVO;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.dto.QueryJobDTO;
import com.dtstack.engine.api.vo.JobTopErrorVO;
import com.dtstack.engine.api.vo.JobTopOrderVO;
import com.dtstack.engine.api.vo.RestartJobVO;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobDetailVO;
import com.dtstack.engine.api.vo.ScheduleFillDataJobPreViewVO;
import com.dtstack.engine.api.vo.ScheduleJobChartVO;
import com.dtstack.engine.api.vo.ScheduleJobExeStaticsVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.api.vo.SchedulePeriodInfoVO;
import com.dtstack.engine.api.vo.ScheduleRunDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper
public interface BatchJobMapstructTransfer {
    BatchJobMapstructTransfer INSTANCE = Mappers.getMapper(BatchJobMapstructTransfer.class);

    /**
     * BatchJobKillJobVO -> KillJobVo
     *
     * @param killJobVO
     * @return
     */
    KillJobVo stopJobByConditionVOToKillJobVo(BatchJobKillJobVO killJobVO);

    /**
     * BatchJobQueryJobVO --> QueryJobDTO
     *
     * @param killJobVO
     * @return
     */
    QueryJobDTO queryJobVOToQueryJobDTO(BatchJobQueryJobVO killJobVO);

    /**
     * BatchJobGetRestartChildJobVO --> ScheduleJob
     *
     * @param jobVO
     * @return
     */
    ScheduleJob jobGetRestartChildJobVOToScheduleJob(BatchJobGetRestartChildJobVO jobVO);

    /**
     * BatchJobGetFillDataDetailInfoVO --> QueryJobDTO
     *
     * @param jobVO
     * @return
     */
    QueryJobDTO jobGetFillDataDetailInfoVOToQueryJobDTO(BatchJobGetFillDataDetailInfoVO jobVO);

    /**
     * BatchJobQueryJobsStatusStatisticsVO --> QueryJobDTO
     *
     * @param killJobVO
     * @return
     */
    QueryJobDTO JobsStatusStatisticsVOToQueryJobDTO(BatchJobQueryJobsStatusStatisticsVO killJobVO);

    /**
     * ScheduleJob --> BatchScheduleJobResultVO
     *
     * @param scheduleJob
     * @return
     */
    BatchScheduleGetByJobIdResultVO scheduleJobToBatchScheduleGetByJobIdResultVO(ScheduleJob scheduleJob);

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
     * List<RestartJobVO> --> List<BatchRestartJobResultVO>
     *
     * @param restartJobVOS
     * @return
     */
    List<BatchRestartJobResultVO> restartJobVOSToBatchRestartJobResultVOs(List<RestartJobVO> restartJobVOS);

    /**
     * BatchOperatorVO --> BatchOperatorResultVO
     *
     * @param batchOperatorVO
     * @return
     */
    BatchOperatorResultVO batchOperatorVOToBatchOperatorResultVO(BatchOperatorVO batchOperatorVO);

    /**
     *  ScheduleFillDataJobDetailVO.FillDataRecord --> BatchFillDataRecordResultVO
     *
     * @param fillDataRecord
     * @return
     */
    BatchFillDataRecordResultVO fillDataRecordToBatchFillDataRecordResultVO(ScheduleFillDataJobDetailVO.FillDataRecord fillDataRecord);

    /**
     * List<ScheduleRunDetailVO> --> List<BatchScheduleRunDetailResultVO>
     *
     * @param scheduleRunDetailVOS
     * @return
     */
    List<BatchScheduleRunDetailResultVO> scheduleRunDetailVOSToBatchScheduleRunDetailResultVOs(List<ScheduleRunDetailVO> scheduleRunDetailVOS);

    /**
     * ScheduleJobVO --> BatchScheduleJobResultVO
     *
     * @param scheduleJobVO
     * @return
     */
    BatchScheduleJobResultVO scheduleJobVOToBatchScheduleJobResultVO(ScheduleJobVO scheduleJobVO);

    /**
     * List<BatchSchedulePeriodInfoResultVO> --> List<SchedulePeriodInfoVO>
     *
     * @param schedulePeriodInfoVOS
     * @return
     */
    List<BatchSchedulePeriodInfoResultVO> schedulePeriodInfoVOSToBatchSchedulePeriodInfoResultVOs(List<SchedulePeriodInfoVO> schedulePeriodInfoVOS);

    /**
     * List<JobTopErrorVO> --> List<BatchJobTopErrorResultVO>
     *
     * @param jobTopErrorVOS
     * @return
     */
    List<BatchJobTopErrorResultVO> jobTopErrorVOSToBatchJobTopErrorResultVOs(List<JobTopErrorVO> jobTopErrorVOS);

    /**
     * List<JobTopOrderVO> --> List<BatchJobTopErrorResultVO>
     *
     * @param jobTopOrderVOS
     * @return
     */
    List<BatchJobTopOrderResultVO> jobTopOrderVOSToBatchJobTopOrderResultVO(List<JobTopOrderVO> jobTopOrderVOS);

    /**
     * scheduleJobChartVO -> BatchChartDataResultVO
     *
     * @param scheduleJobChartVO
     * @return
     */
    BatchChartDataResultVO scheduleJobChartVOToBatchChartDataResultVO(ScheduleJobChartVO scheduleJobChartVO);

    /**
     * PageResult<List<ScheduleJobVO>> ->  PageResult<List<BatchScheduleJobResultVO>>
     *
     * @param pageResult
     * @return
     */
    PageResult<List<BatchScheduleJobResultVO>> scheduleJobVOToBatchScheduleJobResultVO(PageResult<List<ScheduleJobVO>> pageResult);

    /**
     * PageResult<List<ScheduleFillDataJobPreViewVO>> -> PageResult<List<ScheduleFillDataJobPreViewResultVO>>
     *
     * @param pageResult
     * @return
     */
    PageResult<List<ScheduleFillDataJobPreViewResultVO>> pageScheduleFillDataJobPreViewVOToScheduleFillDataJobPreViewResultVO(PageResult<List<ScheduleFillDataJobPreViewVO>> pageResult);

    /**
     * PageResult<ScheduleFillDataJobDetailVO> ->  PageResult<BatchScheduleFillDataJobDetailResultVO>
     *
     * @param pageResult
     * @return
     */
    PageResult<BatchScheduleFillDataJobDetailResultVO> scheduleFillDataJobDetailVOToBatchScheduleFillDataJobDetailResultVO (PageResult<ScheduleFillDataJobDetailVO> pageResult);

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
