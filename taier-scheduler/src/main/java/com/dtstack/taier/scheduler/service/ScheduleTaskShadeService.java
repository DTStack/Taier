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
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.mapper.ScheduleTaskShadeMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ScheduleTaskShadeService extends ServiceImpl<ScheduleTaskShadeMapper, ScheduleTaskShade> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTaskShadeService.class);

    /**
     * 生成周期实例，扫描全部任务
     * @param startId 开始位置的taskId
     * @param scheduleStatusList 任务的状态
     * @param taskSize 查询出来最大的任务数
     * @return 任务集合
     */
    public List<ScheduleTaskShade> listRunnableTask(Long startId, List<Integer> scheduleStatusList, Integer taskSize) {
        if (startId == null) {
            return Lists.newArrayList();
        }

        if (startId < 0) {
            startId = 0L;
        }
        return this.baseMapper.listRunnableTask(startId,scheduleStatusList,taskSize);
    }


    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    public List<ScheduleTaskShade> getFlowWorkSubTasks( Long taskId) {
        return this.baseMapper.selectList(Wrappers.lambdaQuery(ScheduleTaskShade.class)
                .eq(ScheduleTaskShade::getFlowId, taskId)
                .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus()));
    }
}
