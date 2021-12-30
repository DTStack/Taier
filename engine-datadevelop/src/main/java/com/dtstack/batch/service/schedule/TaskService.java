package com.dtstack.batch.service.schedule;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.batch.mapstruct.task.ScheduleTaskMapstructTransfer;
import com.dtstack.batch.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.batch.vo.schedule.ReturnTaskSupportTypesVO;
import com.dtstack.engine.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleStatus;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.mapper.ScheduleTaskShadeMapper;
import com.dtstack.engine.master.dto.schedule.QueryTaskListDTO;
import com.dtstack.engine.pager.PageResult;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private TaskTaskService tasktaskService;

    /**
     * 根据任务id获得任务
     *
     * @param taskId 任务id
     * @return 任务
     */
    public ScheduleTaskShade findTaskByTaskId(Long taskId){
        return taskId != null ? this.lambdaQuery()
                .eq(ScheduleTaskShade::getTaskId, taskId)
                .eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                .one() : null;
    }

    /**
     * 修改任务名称
     *
     * @param taskId 任务id
     * @param name   任务名称
     * @return 是否修改成功
     */
    public Boolean updateTaskName(Long taskId, String name) {
        if (taskId==null || StringUtils.isBlank(name)) {
            return Boolean.FALSE;
        }

        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setName(name);
        return this.lambdaUpdate()
                .eq(ScheduleTaskShade::getTaskId, taskId)
                .eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType()).update(scheduleTaskShade);
    }

    /**
     * 删除任务
     */
    public Boolean deleteTask(Long taskId, Long modifyUserId) {
        if (taskId == null) {
            return Boolean.FALSE;
        }

        // 逻辑删除任务
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        scheduleTaskShade.setIsDeleted(IsDeletedEnum.DELETE.getType());
        scheduleTaskShade.setModifyUserId(modifyUserId);
        this.lambdaUpdate().eq(ScheduleTaskShade::getTaskId, taskId).update(scheduleTaskShade);

        // 直接删除任务依赖
        return tasktaskService.lambdaUpdate().eq(ScheduleTaskTaskShade::getTaskId, taskId).remove();
    }

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
                .eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                .like(StringUtils.isNotBlank(dto.getName()), ScheduleTaskShade::getName, dto.getName())
                .eq(dto.getOwnerId() != null, ScheduleTaskShade::getOwnerUserId, dto.getOwnerId())
                .eq(dto.getTenantId() != null, ScheduleTaskShade::getTenantId, dto.getTenantId())
                .eq(dto.getScheduleStatus() != null, ScheduleTaskShade::getScheduleStatus, dto.getScheduleStatus())
                .between((dto.getStartModifiedTime() != null && dto.getEndModifiedTime() != null), ScheduleTaskShade::getGmtModified, dto.getStartModifiedTime(), dto.getEndModifiedTime())
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
     * @param ownerId 所属用户id
     * @return taskIds
     */
    public List<Long> findTaskByTaskName(String taskName, Long ownerId) {
        if (StringUtils.isBlank(taskName) && ownerId == null) {
            return Lists.newArrayList();
        }
        return this.lambdaQuery()
                .eq(ownerId != null, ScheduleTaskShade::getOwnerUserId, ownerId)
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

    /**
     * 获得所有任务类型
     *
     * @return 任务类型
     */
    public List<ReturnTaskSupportTypesVO> querySupportJobTypes() {
        List<ReturnTaskSupportTypesVO> returnTaskSupportTypesVOS = Lists.newArrayList();
        EScheduleJobType[] eScheduleJobTypes = EScheduleJobType.values();

        for (EScheduleJobType eScheduleJobType : eScheduleJobTypes) {
            ReturnTaskSupportTypesVO vo = new ReturnTaskSupportTypesVO();
            vo.setTaskTypeName(eScheduleJobType.getName());
            vo.setTaskTypeCode(eScheduleJobType.getType());
            returnTaskSupportTypesVOS.add(vo);
        }
        return returnTaskSupportTypesVOS;
    }
}
