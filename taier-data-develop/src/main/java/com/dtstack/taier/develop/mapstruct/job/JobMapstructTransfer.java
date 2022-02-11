package com.dtstack.taier.develop.mapstruct.job;

import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.po.JobsStatusStatisticsPO;
import com.dtstack.taier.develop.vo.schedule.*;
import com.dtstack.taier.scheduler.dto.schedule.QueryJobDisplayDTO;
import com.dtstack.taier.scheduler.dto.schedule.QueryJobListDTO;
import com.dtstack.taier.scheduler.dto.schedule.QueryJobStatusStatisticsDTO;
import com.dtstack.taier.scheduler.dto.schedule.QueryTaskDisplayDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Auther: dazhi
 * @Date: 2021/12/23 4:35 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface JobMapstructTransfer {

    JobMapstructTransfer INSTANCE = Mappers.getMapper(JobMapstructTransfer.class);

    /**
     * 周期实例列表 vo -> dto
     */
    QueryJobListDTO queryJobListVOToQueryJobListDTO(QueryJobListVO vo);

    /**
     * 周期实例 domain -> vo
     */
    ReturnJobListVO scheduleJobToReturnJobListVO(ScheduleJob scheduleJob);

    /**
     * 周期实例 queryJobStatusStatisticsVO -> queryJobStatusStatisticsDTO
     */
    QueryJobStatusStatisticsDTO queryJobStatusStatisticsVOToQueryJobStatusStatisticsDTO(QueryJobStatusStatisticsVO vo);

    /**
     * 周期实例 dto -> JobsStatusStatistics
     */
    JobsStatusStatisticsPO queryJobStatusStatisticsDTOToJobsStatusStatistics(QueryJobStatusStatisticsDTO dto);

    /**
     * 任务依赖关系 vo -> dto
     */
    QueryTaskDisplayDTO queryTaskDisplayVOToQueryTaskDisplayDTO(QueryTaskDisplayVO vo);

    /**
     * 实例依赖关系 vo -> dto
     */
    QueryJobDisplayDTO queryJobDisplayVOToReturnJobDisplayVO(QueryJobDisplayVO vo);
}
