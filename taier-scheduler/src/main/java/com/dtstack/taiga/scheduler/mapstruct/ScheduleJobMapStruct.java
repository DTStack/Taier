package com.dtstack.taiga.scheduler.mapstruct;

import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.ScheduleJobExpand;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/31 2:35 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface ScheduleJobMapStruct {

    ScheduleJobMapStruct INSTANCE = Mappers.getMapper(ScheduleJobMapStruct.class);


    /**
     * ScheduleJob -> ScheduleJobExpand
     */
    List<ScheduleJobExpand> scheduleJobTOScheduleJobExpand(List<ScheduleJob> scheduleJobList);
}
