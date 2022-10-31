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
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.mapper.ScheduleJobJobMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:15 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class ScheduleJobJobService extends ServiceImpl<ScheduleJobJobMapper, ScheduleJobJob> {

    /**
     * 查询父实例信息
     *
     * @param jobKeys 实例key
     * @return 实例关系信息
     */
    public List<ScheduleJobJob> listByJobKeys(List<String> jobKeys) {
        if (CollectionUtils.isNotEmpty(jobKeys)) {
            return this.lambdaQuery().in(ScheduleJobJob::getJobKey,jobKeys)
                    .eq(ScheduleJobJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list();
        }
        return Lists.newArrayList();
    }
}
