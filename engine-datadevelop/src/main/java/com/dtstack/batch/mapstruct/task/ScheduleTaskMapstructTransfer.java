package com.dtstack.batch.mapstruct.task;

import com.dtstack.batch.vo.schedule.QueryTaskListVO;
import com.dtstack.batch.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.master.dto.schedule.QueryTaskListDTO;
import com.dtstack.engine.master.dto.schedule.ScheduleTaskShadeDTO;
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
