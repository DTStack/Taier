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

package com.dtstack.engine.mapper;

import com.dtstack.engine.domain.JobGraphTrigger;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface JobGraphTriggerDao {

    Integer insert(JobGraphTrigger jobGraphTrigger);

    JobGraphTrigger getByTriggerTimeAndTriggerType(@Param("triggerTime") Timestamp timestamp, @Param("triggerType") int triggerType);

    /**
     * 测试时使用，上线前删除
     */
    void deleteToday();

    /**
     * 根据触发时间查询关联最小任务ID
     * @param triggerStartTime
     * @param triggerEndTime
     * @return
     */
    String  getMinJobIdByTriggerTime(@Param("triggerStartTime")String triggerStartTime,@Param("triggerEndTime")String triggerEndTime);
}
