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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.dao.domain.JobGraphTrigger;
import com.dtstack.taier.dao.mapper.JobGraphTriggerMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/1/5 7:02 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobGraphTriggerService extends ServiceImpl<JobGraphTriggerMapper, JobGraphTrigger> {

    /**
     * 判断在triggerTime时间里是否存在JobGraphTrigger
     * @param triggerTime JobGraphTrigger生成时间
     * @return ture 存在，false 不存在
     */
    public boolean checkHasBuildJobGraph(Timestamp triggerTime) {
        return this.baseMapper.getByTriggerTimeAndTriggerType(triggerTime, EScheduleType.NORMAL_SCHEDULE.getType()) != null;

    }

    /**
     * 新增jobTrigger
     * @param timestamp 生成的时间搓
     */
    public void addJobTrigger(Timestamp timestamp) {
        JobGraphTrigger jobGraphTrigger = new JobGraphTrigger();
        jobGraphTrigger.setTriggerTime(timestamp);
        jobGraphTrigger.setTriggerType(0);
        //新增jobTrigger
        this.save(jobGraphTrigger);
    }
}
