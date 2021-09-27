package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.domain.po.JobTopErrorPO;
import com.dtstack.engine.domain.po.ScheduleJobCountPO;
import com.dtstack.engine.master.impl.vo.ScheduleJobCountVO;
import com.dtstack.engine.master.vo.JobTopErrorVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/9/8
 */
@Mapper(componentModel = "spring")
public interface ScheduleJobStruct {

    ScheduleJobCountVO toScheduleJobCountVO(ScheduleJobCountPO scheduleJobCountPO);

    JobTopErrorVO toJobTopErrorVO(JobTopErrorPO jobTopErrorPO);

    List<JobTopErrorVO> toJobTopErrorVOs(List<JobTopErrorPO> jobTopErrorPOs);
}
