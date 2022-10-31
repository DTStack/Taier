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

package com.dtstack.taier.develop.service.schedule;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.dao.mapper.ScheduleJobExpandMapper;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 9:49 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobExpandService extends ServiceImpl<ScheduleJobExpandMapper, ScheduleJobExpand> {

    public ScheduleJobExpand selectOneByJobId(String jobId){
        // 查询当前日志
        return this.lambdaQuery()
                .eq(ScheduleJobExpand::getIsDeleted, Deleted.NORMAL.getStatus())
                .eq(ScheduleJobExpand::getJobId, jobId)
                .one();
    }

}
