package com.dtstack.taiga.develop.mapstruct.task;

import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.develop.vo.schedule.QueryTaskListVO;
import com.dtstack.taiga.develop.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.taiga.scheduler.dto.schedule.QueryTaskListDTO;
import com.dtstack.taiga.scheduler.dto.schedule.ScheduleTaskShadeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 3:53 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface ScheduleTaskMapstructTransfer {

    ScheduleTaskMapstructTransfer INSTANCE = Mappers.getMapper(ScheduleTaskMapstructTransfer.class);

    /**
     * vo -> dto
     */
    QueryTaskListDTO queryTasksVoToDto(QueryTaskListVO vo);

    /**
     * bean -> vo
     */
    List<ReturnScheduleTaskVO> beanToTaskVO(List<ScheduleTaskShade> records);

    /**
     * dto -> bean
     */
    ScheduleTaskShade dtoToBean(ScheduleTaskShadeDTO dto);
}
