package com.dtstack.batch.service.task.impl;

import com.dtstack.batch.dao.BatchTaskDao;
import com.dtstack.batch.dao.BatchTaskTaskDao;
import com.dtstack.engine.api.domain.BatchTask;
import com.dtstack.batch.domain.BatchTaskTask;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.service.impl.ProjectService;
import com.dtstack.batch.service.impl.TenantService;
import com.dtstack.batch.service.impl.BatchUserService;
import com.dtstack.batch.vo.BatchTaskBatchVO;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.Deleted;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.project.ScheduleEngineProjectVO;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 获得任务的关联关系
 * Date: 2017/5/5
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

@Service
public class BatchTaskTaskService {

    @Autowired
    private BatchTaskTaskDao batchTaskTaskDao;

    @Autowired
    private BatchTaskDao batchTaskDao;

    @Autowired
    private BatchUserService batchUserService;

    @Resource(name = "batchTenantService")
    private TenantService tenantService;

    @Resource(name = "batchProjectService")
    private ProjectService projectService;

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private com.dtstack.engine.master.impl.ProjectService engineProjectService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateTaskTask(Long taskId, List<BatchTask> dependencyTasks) {
        List<BatchTaskTask> taskTasks = batchTaskTaskDao.listByTaskId(taskId);
        List<BatchTaskTask> dependencyTaskTasks = getTaskTasksByTaskIdAndTasks(taskId, dependencyTasks);
        List<BatchTaskTask> existDependencyTasks = Lists.newArrayList();
        for (BatchTaskTask taskTask : taskTasks) {
            BatchTaskTask existTaskTask = existTaskTask(dependencyTaskTasks, taskTask);
            if (existTaskTask != null) {
                existDependencyTasks.add(existTaskTask);
                continue;
            }
            batchTaskTaskDao.delete(taskTask.getId());
        }

        dependencyTaskTasks.removeAll(existDependencyTasks);
        for (BatchTaskTask taskTask : dependencyTaskTasks) {
            addOrUpdate(taskTask);
        }
    }

    /**
     * 当前子任务是否已存在
     * @param dependencyTaskTasks
     * @param taskTask
     * @return
     */
    private BatchTaskTask existTaskTask(List<BatchTaskTask> dependencyTaskTasks, BatchTaskTask taskTask) {
        for (BatchTaskTask batchTaskTask : dependencyTaskTasks) {
            if (taskTask.getParentAppType().equals(batchTaskTask.getParentAppType()) &&
                    taskTask.getParentTaskId().equals(batchTaskTask.getParentTaskId())) {
                return batchTaskTask;
            }
        }
        return null;
    }

    /**
     * 根据taskId和List<BatchTask>生成List<BatchTaskTask>
     * @param taskId
     * @param tasks
     * @return
     */
    private List<BatchTaskTask> getTaskTasksByTaskIdAndTasks(Long taskId, List<BatchTask> tasks) {
        List<BatchTaskTask> taskTasks = Lists.newArrayList();
        for (BatchTask task : tasks) {
            BatchTaskTask taskTask = new BatchTaskTask();
            taskTask.setParentAppType(task.getAppType() == null ? AppType.RDOS.getType() : task.getAppType());
            taskTask.setParentTaskId(task.getId());
            if (taskTask.getParentAppType().equals(AppType.RDOS.getType())) {
                taskTask.setTenantId(task.getTenantId());
            } else {
                taskTask.setTenantId(task.getDtuicTenantId());
            }
            taskTask.setProjectId(task.getProjectId());
            taskTask.setTaskId(taskId);
            taskTasks.add(taskTask);
        }
        return taskTasks;
    }

    public BatchTaskTask addOrUpdate(BatchTaskTask batchTaskTask) {
        if (batchTaskTask.getId() > 0) {
            batchTaskTaskDao.update(batchTaskTask);
        } else {
            batchTaskTaskDao.insert(batchTaskTask);
        }
        return batchTaskTask;
    }

    public List<Long> getTopTask(long taskId) {
        List<BatchTaskTask> taskTaskList = batchTaskTaskDao.listByTaskId(taskId);
        if (CollectionUtils.isEmpty(taskTaskList)) {
            return Lists.newArrayList();
        }

        List<Long> parentTaskList = Lists.newArrayList();
        for (BatchTaskTask taskTask : taskTaskList) {
            if (taskTask.getParentTaskId() == -1) {
                parentTaskList.add(taskTask.getTaskId());
            } else {
                parentTaskList.addAll(getTopTask(taskTask.getParentTaskId()));
            }
        }

        return parentTaskList;
    }

    public void deleteByProjectId(Long projectId) {
        batchTaskTaskDao.deleteByProjectId(projectId, AppType.RDOS.getType());
        List<BatchTaskTask> taskTasks = listTaskTaskByProjectId(projectId);
        Set<Long> taskIds = taskTasks.stream().map(BatchTaskTask::getTaskId).collect(Collectors.toSet());
        taskIds.stream().forEach(taskId -> scheduleTaskTaskShadeService.clearDataByTaskId(taskId, AppType.RDOS.getType()));
    }

    public List<BatchTaskTask> getByParentTaskId(long parentId) {
        return batchTaskTaskDao.listByParentTaskId(parentId);
    }

    public List<BatchTaskTask> getAllParentTask(long taskId) {
        return batchTaskTaskDao.listByTaskId(taskId);
    }

    /**
     * 所有的任务关联关系的显示都是基于已经发布的任务数据（而不是已经保存的任务）
     *
     * @author toutian
     */
    public ScheduleTaskVO displayOffSpring(Long taskId,
                                           Long projectId,
                                           Long userId,
                                           Integer level,
                                           Integer type,
                                           Integer appType) {
        ScheduleTaskVO scheduleTaskVO = scheduleTaskTaskShadeService.displayOffSpring(taskId, projectId, level, type, appType);
        return scheduleTaskVO;
    }

    /**
     * 展开上一个父节点
     *
     * @author toutian
     */
    public ScheduleTaskVO getForefathers(BatchTask task) {

        BatchTaskBatchVO vo = new BatchTaskBatchVO();
        BeanUtils.copyProperties(task, vo);
        vo.setVersion(task.getVersion());
        vo.setCreateUser(batchUserService.getUserByDTO(task.getCreateUserId()));
        vo.setModifyUser(batchUserService.getUserByDTO(task.getModifyUserId()));
        vo.setOwnerUser(batchUserService.getUserByDTO(task.getOwnerUserId()));
        vo.setTenantName(tenantService.getTenantById(task.getTenantId()).getTenantName());
        vo.setProjectName(projectService.getProjectById(task.getProjectId()).getProjectName());

        List<BatchTaskTask> taskTasks = batchTaskTaskDao.listByTaskId(task.getId());
        if (CollectionUtils.isEmpty(taskTasks)) {
            return vo;
        }

        List<ScheduleTaskVO> fatherTaskVOs = Lists.newArrayList();
        for (BatchTaskTask taskTask : taskTasks) {
            Long parentTaskId = taskTask.getParentTaskId();
            Integer parentAppType = taskTask.getParentAppType();
            ScheduleTaskShade taskShade = scheduleTaskShadeService.findTaskId(parentTaskId, Deleted.NORMAL.getStatus(), parentAppType);
            if (taskShade != null) {
                ScheduleTaskVO scheduleTaskVO = new ScheduleTaskVO();
                BeanUtils.copyProperties(taskShade, scheduleTaskVO);
                scheduleTaskVO.setId(taskShade.getTaskId());
                ScheduleEngineProjectVO engineProjectVO= engineProjectService.findProject(taskShade.getProjectId(),taskShade.getAppType());
                if (engineProjectVO != null) {
                    scheduleTaskVO.setProjectName(engineProjectService.findProject(taskShade.getProjectId(),taskShade.getAppType()).getProjectName());
                }
                scheduleTaskVO.setTenantName(tenantService.getTenantByDtUicTenantId(taskShade.getDtuicTenantId()).getTenantName());
                fatherTaskVOs.add(scheduleTaskVO);
            }
        }

        vo.setTaskVOS(fatherTaskVOs);

        return vo;
    }

    /**
     * 删除任务的依赖关系
     *
     * @param taskId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTaskTaskByTaskId(Long taskId) {
        batchTaskTaskDao.deleteByTaskId(taskId);
    }

    /**
     * 删除被依赖关系
     *
     * @param parentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTaskTaskByParentId(Long parentId, Integer parentAppType) {
        batchTaskTaskDao.deleteByParentId(parentId, parentAppType);
    }

    public ScheduleDetailsVO findTaskRuleTask(Long taskId, Integer appType) {
        return scheduleTaskShadeService.findTaskRuleTask(taskId, appType);
    }

    /**
     * 根据项目ID查询出所有的任务依赖关系
     * @param projectId
     * @return
     */
    private List<BatchTaskTask> listTaskTaskByProjectId(Long projectId) {
        Project projectById = projectService.getProjectById(projectId);
        List<BatchTask> tasks = batchTaskService.getTasksByProjectId(projectById.getTenantId(), projectId, null);
        if (CollectionUtils.isNotEmpty(tasks)) {
            List<Long> taskIds = tasks.stream().map(BatchTask::getId).collect(Collectors.toList());
            return batchTaskTaskDao.listTaskTaskByTaskIds(taskIds);
        }
        return Lists.newArrayList();
    }

}
