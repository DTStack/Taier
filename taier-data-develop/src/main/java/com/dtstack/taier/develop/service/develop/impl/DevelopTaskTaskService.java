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

package com.dtstack.taier.develop.service.develop.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.DevelopTaskTask;
import com.dtstack.taier.dao.mapper.DevelopTaskTaskMapper;
import com.dtstack.taier.develop.dto.devlop.TaskVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务依赖关系处理
 *
 * @author zhaiyue
 */
@Service
public class DevelopTaskTaskService extends ServiceImpl<DevelopTaskTaskMapper, DevelopTaskTask> {

    @Autowired
    private DevelopTaskTaskMapper developTaskTaskDao;

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateTaskTask(Long taskId, List<TaskVO> dependencyTasks) {
        this.remove(Wrappers.lambdaQuery(DevelopTaskTask.class)
                .eq(DevelopTaskTask::getTaskId, taskId));

        if (CollectionUtils.isEmpty(dependencyTasks)) {
            return;
        }

        List<DevelopTaskTask> taskTaskList = dependencyTasks.stream().map(taskVO -> {
            DevelopTaskTask taskTask = new DevelopTaskTask();
            taskTask.setParentTaskId(taskVO.getId());
            taskTask.setTenantId(taskVO.getTenantId());
            taskTask.setTaskId(taskId);
            return taskTask;
        }).collect(Collectors.toList());

        this.saveBatch(taskTaskList);
    }

    /**
     * 获取任务所有父任务
     *
     * @param taskId
     * @return
     */
    public List<DevelopTaskTask> getAllParentTask(Long taskId) {
        return developTaskTaskDao.selectList(Wrappers.lambdaQuery(DevelopTaskTask.class)
                .eq(DevelopTaskTask::getTaskId, taskId)
                .eq(DevelopTaskTask::getIsDeleted, Deleted.NORMAL.getStatus())
                .orderBy(true, false, DevelopTaskTask::getGmtModified));
    }

    /**
     * 删除任务的依赖关系
     *
     * @param taskId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTaskTaskByTaskId(Long taskId) {
        developTaskTaskDao.delete(Wrappers.lambdaQuery(DevelopTaskTask.class)
                .eq(DevelopTaskTask::getTaskId, taskId));
    }

    /**
     * 删除任务依赖关系
     *
     * @param parentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTaskTaskByParentId(Long parentId) {
        developTaskTaskDao.delete(Wrappers.lambdaQuery(DevelopTaskTask.class)
                .eq(DevelopTaskTask::getParentTaskId, parentId));
    }

}
