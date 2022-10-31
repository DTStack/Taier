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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleTaskShadeInfo;
import com.dtstack.taier.dao.mapper.ScheduleTaskShadeInfoMapper;
import org.springframework.stereotype.Service;


@Service
public class ScheduleTaskShadeInfoService extends ServiceImpl<ScheduleTaskShadeInfoMapper, ScheduleTaskShadeInfo> {


    public void update(ScheduleTaskShadeInfo scheduleTaskShadeInfo,Long taskId){
        getBaseMapper().update(scheduleTaskShadeInfo,
                Wrappers.lambdaQuery(ScheduleTaskShadeInfo.class)
                        .eq(ScheduleTaskShadeInfo::getTaskId,taskId));
    }

    public void insert(ScheduleTaskShadeInfo scheduleTaskShadeInfo){
        getBaseMapper().insert(scheduleTaskShadeInfo);
    }

    public JSONObject getInfoJSON(Long taskId) {
        ScheduleTaskShadeInfo scheduleTaskShadeInfo = getBaseMapper().selectOne(Wrappers.lambdaQuery(ScheduleTaskShadeInfo.class)
                .eq(ScheduleTaskShadeInfo::getTaskId, taskId));
        if(null == scheduleTaskShadeInfo){
            return null;
        }
        return JSONObject.parseObject(scheduleTaskShadeInfo.getInfo());
    }
}
