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

package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.DevelopTaskTask;
import com.dtstack.taier.dao.domain.ScheduleTaskTaskShade;
import com.dtstack.taier.dao.mapper.ScheduleTaskTaskShadeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 11:01 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleTaskTaskService extends ServiceImpl<ScheduleTaskTaskShadeMapper, ScheduleTaskTaskShade> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskTaskService.class);



    /**
     * 获取任务所有父任务
     *
     * @param taskId
     * @return
     */
    public List<ScheduleTaskTaskShade> getAllParentTask(Long taskId) {
        return baseMapper.selectList(Wrappers.lambdaQuery(ScheduleTaskTaskShade.class)
                .eq(ScheduleTaskTaskShade::getTaskId, taskId)
                .eq(ScheduleTaskTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                .orderBy(true, false, ScheduleTaskTaskShade::getGmtModified));
    }

    /**
     * 清除任务相关依赖
     *
     * @param taskId
     * @return
     */
    public void deleteByTaskId( Long taskId) {
        baseMapper.delete(Wrappers.lambdaQuery(ScheduleTaskTaskShade.class)
                .eq(ScheduleTaskTaskShade::getTaskId, taskId)
        );
    }

    public Integer insert(ScheduleTaskTaskShade scheduleTaskTaskShade){
       return baseMapper.insert(scheduleTaskTaskShade);
    }

}
