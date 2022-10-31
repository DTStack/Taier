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
import com.dtstack.taier.dao.domain.JobGraphTrigger;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2022/1/5 6:59 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface JobGraphTriggerMapper extends BaseMapper<JobGraphTrigger> {

    /**
     * 按照时间查询JobGraphTrigger
     *
     * @param timestamp JobGraphTrigger的生成具体时间
     * @param triggerType JobGraph类型： 周期，立即，补数据
     * @return JobGraphTrigger
     */
    JobGraphTrigger getByTriggerTimeAndTriggerType(@Param("triggerTime") Timestamp timestamp, @Param("triggerType") int triggerType);
}
