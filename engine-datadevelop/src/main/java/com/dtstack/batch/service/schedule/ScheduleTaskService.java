package com.dtstack.batch.service.schedule;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.batch.mapstruct.task.ScheduleTaskMapstructTransfer;
import com.dtstack.batch.vo.schedule.ScheduleTaskVO;
import com.dtstack.engine.common.enums.EScheduleStatus;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.domain.ScheduleTask;
import com.dtstack.engine.mapper.ScheduleTaskMapper;
import com.dtstack.engine.master.dto.schedule.QueryTaskListDTO;
import com.dtstack.engine.pager.PageResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 3:48 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleTaskService extends ServiceImpl<ScheduleTaskMapper, ScheduleTask> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskService.class);

    public PageResult<ScheduleTaskVO> queryTasks(QueryTaskListDTO vo) {
        Page<ScheduleTask> page = new Page<>(vo.getCurrentPage(), vo.getPageSize());
        // 分页查询
        Page<ScheduleTask> resultPage = this.lambdaQuery()
                .like(StringUtils.isNotBlank(vo.getName()), ScheduleTask::getName, vo.getName())
                .eq(vo.getOwnerId() != null, ScheduleTask::getOwnerUserId, vo.getOwnerId())
                .eq(vo.getTenantId() != null, ScheduleTask::getTenantId, vo.getTenantId())
                .eq(vo.getScheduleStatus() != null, ScheduleTask::getScheduleStatus, vo.getScheduleStatus())
                .in(StringUtils.isNotBlank(vo.getTaskTypeList()), ScheduleTask::getTaskType, Arrays.asList(vo.getTaskTypeList().split(",")))
                .in(StringUtils.isNotBlank(vo.getPeriodTypeList()), ScheduleTask::getPeriodType, Arrays.asList(vo.getPeriodTypeList().split(",")))
                .page(page);
        List<ScheduleTaskVO> scheduleTaskVOS = ScheduleTaskMapstructTransfer.INSTANCE.beanToTaskVO(resultPage.getRecords());
        return new PageResult(vo.getCurrentPage(), vo.getPageSize(), resultPage.getTotal(), (int)resultPage.getPages(), scheduleTaskVOS);
    }

    public Boolean frozenTask(List<Long> taskIdList, Integer scheduleStatus) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            LOGGER.error("taskIdList is null");
            return Boolean.FALSE;
        }

        if (EScheduleStatus.getStatus(scheduleStatus) != null) {
            LOGGER.error("scheduleStatus is null");
            return Boolean.FALSE;
        }

        ScheduleTask scheduleTask = new ScheduleTask();
        scheduleTask.setScheduleStatus(scheduleStatus);
        return this.lambdaUpdate()
                .in(ScheduleTask::getTaskId)
                .eq(ScheduleTask::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                .update(scheduleTask);
    }
}
