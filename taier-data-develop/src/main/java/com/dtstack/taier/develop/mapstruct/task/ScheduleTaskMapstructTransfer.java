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

package com.dtstack.taier.develop.mapstruct.task;

import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.develop.vo.schedule.QueryTaskListVO;
import com.dtstack.taier.develop.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.taier.scheduler.dto.schedule.QueryTaskListDTO;
import com.dtstack.taier.scheduler.dto.schedule.ScheduleTaskShadeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 3:53 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface ScheduleTaskMapstructTransfer {

    ScheduleTaskMapstructTransfer INSTANCE = Mappers.getMapper(ScheduleTaskMapstructTransfer.class);

    /**
     * vo -> dto
     */
    QueryTaskListDTO queryTasksVoToDto(QueryTaskListVO vo);

    @Mapping(source = "createUserId",target = "operatorId")
    ReturnScheduleTaskVO beanToTaskVO(ScheduleTaskShade records);

    /**
     * bean -> vo
     */
    List<ReturnScheduleTaskVO> beanToTaskVO(List<ScheduleTaskShade> records);

    /**
     * dto -> bean
     */
    ScheduleTaskShade dtoToBean(ScheduleTaskShadeDTO dto);
}
