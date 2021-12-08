package com.dtstack.batch.mapstruct.task;

import com.dtstack.batch.vo.schedule.QueryTaskListVO;
import com.dtstack.batch.vo.schedule.ScheduleTaskVO;
import com.dtstack.engine.domain.ScheduleTask;
import com.dtstack.engine.master.dto.schedule.QueryTaskListDTO;
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


    QueryTaskListDTO queryTasksVoToDto(QueryTaskListVO vo);

    List<ScheduleTaskVO> beanToTaskVO(List<ScheduleTask> records);
}
