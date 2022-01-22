/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taiga.develop.service.task.impl;

import com.dtstack.taiga.dao.domain.BatchTask;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.develop.dao.BatchTaskTaskDao;
import com.dtstack.taiga.develop.domain.BatchTaskTask;
import com.dtstack.taiga.develop.service.console.TenantService;
import com.dtstack.taiga.develop.service.schedule.TaskService;
import com.dtstack.taiga.develop.service.user.UserService;
import com.dtstack.taiga.develop.vo.BatchTaskBatchVO;
import com.dtstack.taiga.scheduler.vo.ScheduleTaskVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private TenantService tenantService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

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
            if (taskTask.getParentTaskId().equals(batchTaskTask.getParentTaskId())) {
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
            taskTask.setParentTaskId(task.getId());
            taskTask.setTenantId(task.getTenantId());
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


    public List<BatchTaskTask> getByParentTaskId(long parentId) {
        return batchTaskTaskDao.listByParentTaskId(parentId);
    }

    public List<BatchTaskTask> getAllParentTask(long taskId) {
        return batchTaskTaskDao.listByTaskId(taskId);
    }

    /**
     * 根据任务Id，获取所有父任务的id
     * @param taskId
     * @return
     */
    public List<Long> getAllParentTaskId(Long taskId) {
        return batchTaskTaskDao.listParentTaskIdByTaskId(taskId);
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
        vo.setCreateUser(userService.getUserByDTO(task.getCreateUserId()));
        vo.setModifyUser(userService.getUserByDTO(task.getModifyUserId()));
        vo.setOwnerUser(userService.getUserByDTO(task.getOwnerUserId()));
        vo.setTenantName(tenantService.getTenantById(task.getTenantId()).getTenantName());

        List<BatchTaskTask> taskTasks = batchTaskTaskDao.listByTaskId(task.getId());
        if (CollectionUtils.isEmpty(taskTasks)) {
            return vo;
        }

        List<ScheduleTaskVO> fatherTaskVOs = Lists.newArrayList();
        for (BatchTaskTask taskTask : taskTasks) {
            Long parentTaskId = taskTask.getParentTaskId();
            ScheduleTaskShade taskShade = taskService.findTaskByTaskId(parentTaskId);
            if (taskShade != null) {
                ScheduleTaskVO scheduleTaskVO = new ScheduleTaskVO();
                BeanUtils.copyProperties(taskShade, scheduleTaskVO);
                scheduleTaskVO.setId(taskShade.getTaskId());
                scheduleTaskVO.setTenantName(tenantService.getByDtUicTenantId(taskShade.getTenantId()).getTenantName());
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
    public void deleteTaskTaskByParentId(Long parentId) {
        batchTaskTaskDao.deleteByParentId(parentId);
    }


}
