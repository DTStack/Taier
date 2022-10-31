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

package com.dtstack.taier.scheduler.server.builder.dependency;

import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleCorn;

import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 3:55 PM
 * @Email: dazhi@dtstack.com
 * @Description: 依赖处理器
 */
public interface JobDependency {

    /**
     * 生成jobJob
     *
     * @param corn 生成周期
     * @param currentDate 当期执行时间
     * @param currentJobKey 当期实例key
     * @return currentJobKey的父实例
     */
    List<ScheduleJobJob> generationJobJobForTask(ScheduleCorn corn, Date currentDate,String currentJobKey);
}
