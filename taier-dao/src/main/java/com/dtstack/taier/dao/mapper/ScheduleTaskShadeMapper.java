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

package com.dtstack.taier.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 8:15 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleTaskShadeMapper extends BaseMapper<ScheduleTaskShade> {

    /**
     * 查询所有可以生成周期实例的任务
     *
     * @param startId 开始id
     * @param scheduleStatusList 任务状态
     * @param taskSize 获取的任务数
     * @return 任务列表
     */
    List<ScheduleTaskShade> listRunnableTask(@Param("startId") Long startId, @Param("scheduleStatusList") List<Integer> scheduleStatusList, @Param("taskSize") Integer taskSize);
}
