package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.domain.po.ScheduleTaskShadeCountTaskPO;
import com.dtstack.engine.master.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/9/8
 */
@Mapper(componentModel = "spring")
public interface ScheduleTaskShadeStruct {

    ScheduleTaskShadeCountTaskVO toScheduleTaskShadeCountTaskVO(ScheduleTaskShadeCountTaskPO scheduleTaskShadeCountTaskPO);

    List<ScheduleTaskShadeCountTaskVO> toScheduleTaskShadeCountTaskVOs(List<ScheduleTaskShadeCountTaskPO> scheduleTaskShadeCountTaskPOs);
}
