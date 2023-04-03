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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleJobRetry;
import com.dtstack.taier.dao.mapper.ScheduleJobRetryMapper;
import org.springframework.stereotype.Service;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 3:22 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service
public class JobRetryService extends ServiceImpl<ScheduleJobRetryMapper, ScheduleJobRetry> {

    public void removeByJobId(String jobId) {
        getBaseMapper().delete(Wrappers.lambdaQuery(ScheduleJobRetry.class).eq(
                ScheduleJobRetry::getJobId, jobId));
    }
}
