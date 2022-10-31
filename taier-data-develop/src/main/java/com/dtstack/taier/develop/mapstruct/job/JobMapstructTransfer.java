/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.mapstruct.job;

import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobHistory;
import com.dtstack.taier.dao.domain.po.JobsStatusStatisticsPO;
import com.dtstack.taier.develop.vo.schedule.JobHistoryVO;
import com.dtstack.taier.develop.vo.schedule.QueryJobDisplayVO;
import com.dtstack.taier.develop.vo.schedule.QueryJobListVO;
import com.dtstack.taier.develop.vo.schedule.QueryJobStatusStatisticsVO;
import com.dtstack.taier.develop.vo.schedule.QueryTaskDisplayVO;
import com.dtstack.taier.develop.vo.schedule.ReturnJobListVO;
import com.dtstack.taier.scheduler.dto.schedule.QueryJobDisplayDTO;
import com.dtstack.taier.scheduler.dto.schedule.QueryJobListDTO;
import com.dtstack.taier.scheduler.dto.schedule.QueryJobStatusStatisticsDTO;
import com.dtstack.taier.scheduler.dto.schedule.QueryTaskDisplayDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

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


    List<JobHistoryVO> toHistoryVOS(List<ScheduleJobHistory> scheduleJobHistoryList);
}
