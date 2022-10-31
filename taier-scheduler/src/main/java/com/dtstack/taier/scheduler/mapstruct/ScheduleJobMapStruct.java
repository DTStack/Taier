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

package com.dtstack.taier.scheduler.mapstruct;

import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.scheduler.dto.scheduler.SimpleScheduleJobDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/31 2:35 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface ScheduleJobMapStruct {

    ScheduleJobMapStruct INSTANCE = Mappers.getMapper(ScheduleJobMapStruct.class);


    /**
     * ScheduleJob -> ScheduleJobExpand
     */
    List<ScheduleJobExpand> scheduleJobTOScheduleJobExpand(List<ScheduleJob> scheduleJobList);

    /**
     * ScheduleJob -> SimpleScheduleJobDTO
     */
    List<SimpleScheduleJobDTO> scheduleJobTOSimpleScheduleJobDTO(List<ScheduleJob> scheduleJobList);
}
