package com.dtstack.batch.service.schedule;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.batch.mapstruct.task.ScheduleTaskMapstructTransfer;
import com.dtstack.batch.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.engine.common.enums.EScheduleStatus;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.mapper.ScheduleTaskShadeMapper;
import com.dtstack.engine.master.dto.schedule.QueryTaskListDTO;
import com.dtstack.engine.pager.PageResult;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 3:48 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class TaskService extends ServiceImpl<ScheduleTaskShadeMapper, ScheduleTaskShade> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    /**
     * 查询任务列表
     *
     * @param dto 查询条件
     * @return
     */
    public PageResult<List<ReturnScheduleTaskVO>> queryTasks(QueryTaskListDTO dto) {
        Page<ScheduleTaskShade> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        // 分页查询
        Page<ScheduleTaskShade> resultPage = this.lambdaQuery()
                .eq(ScheduleTaskShade::getFlowId,0L)
                .like(StringUtils.isNotBlank(dto.getName()), ScheduleTaskShade::getName, dto.getName())
                .eq(dto.getOwnerId() != null, ScheduleTaskShade::getOwnerUserId, dto.getOwnerId())
                .eq(dto.getTenantId() != null, ScheduleTaskShade::getTenantId, dto.getTenantId())
                .eq(dto.getScheduleStatus() != null, ScheduleTaskShade::getScheduleStatus, dto.getScheduleStatus())
                .in(CollectionUtils.isNotEmpty(dto.getTaskTypeList()), ScheduleTaskShade::getTaskType, dto.getTaskTypeList())
                .in(CollectionUtils.isNotEmpty(dto.getPeriodTypeList()), ScheduleTaskShade::getPeriodType, dto.getPeriodTypeList())
                .page(page);

        List<ReturnScheduleTaskVO> scheduleTaskVOS = ScheduleTaskMapstructTransfer.INSTANCE.beanToTaskVO(resultPage.getRecords());

        return new PageResult<>(dto.getCurrentPage(), dto.getPageSize(), resultPage.getTotal(), (int) resultPage.getPages(), scheduleTaskVOS);
    }

    /**
     * 修改任务运行状态
     *
     * @param taskIdList 任务id
     * @param scheduleStatus 调度状态
     * @return 是否更新成功
     */
    public Boolean frozenTask(List<Long> taskIdList, Integer scheduleStatus) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            LOGGER.error("taskIdList is null");
            return Boolean.FALSE;
        }

        if (EScheduleStatus.getStatus(scheduleStatus) == null) {
            LOGGER.error("scheduleStatus is null");
            return Boolean.FALSE;
        }

        ScheduleTaskShade scheduleTask = new ScheduleTaskShade();
        scheduleTask.setScheduleStatus(scheduleStatus);
        return this.lambdaUpdate()
                .in(ScheduleTaskShade::getTaskId,taskIdList)
                .eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                .update(scheduleTask);
    }

    /**
     * 查询工作流子节点
     *
     * @param taskId 任务id
     * @return
     */
    public List<ReturnScheduleTaskVO> dealFlowWorkTask(Long taskId) {
        return ScheduleTaskMapstructTransfer.INSTANCE.beanToTaskVO(findAllFlowTasks(taskId));
    }

    /**
     * 通过任务名称和所属idc哈希任务
     *
     * @param taskName 任务名称
     * @param userId 所属用户id
     * @return taskIds
     */
    public List<Long> findTaskIdByTaskName(String taskName, Long userId) {
        if (StringUtils.isBlank(taskName) && userId == null) {
            return Lists.newArrayList();
        }
        return this.lambdaQuery()
                .eq(userId != null, ScheduleTaskShade::getOwnerUserId, userId)
                .like(StringUtils.isNotBlank(taskName), ScheduleTaskShade::getName, taskName)
                .list().stream().map(ScheduleTaskShade::getTaskId).collect(Collectors.toList());
    }

    /**
     * 查询工作有下的所有任务
     *
     * @param taskId 任务i     * @return
     */
    public List<ScheduleTaskShade> findAllFlowTasks(Long taskId) {
        if (taskId == null) {
            return Lists.newArrayList();
        }
        return this.lambdaQuery()
                .eq(ScheduleTaskShade::getIsDeleted,IsDeletedEnum.NOT_DELETE.getType())
                .eq(ScheduleTaskShade::getFlowId,taskId)
                .list();
    }


}
