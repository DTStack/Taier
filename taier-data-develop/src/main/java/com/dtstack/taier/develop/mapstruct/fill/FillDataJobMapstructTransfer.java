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

package com.dtstack.taier.develop.mapstruct.fill;

import com.dtstack.taier.dao.domain.ScheduleFillDataJob;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.develop.vo.fill.FillDataJobVO;
import com.dtstack.taier.develop.vo.fill.QueryFillDataJobListVO;
import com.dtstack.taier.develop.vo.fill.QueryFillDataListVO;
import com.dtstack.taier.develop.vo.fill.ReturnFillDataListVO;
import com.dtstack.taier.develop.vo.fill.ScheduleFillJobParticipateVO;
import com.dtstack.taier.scheduler.dto.fill.QueryFillDataJobListDTO;
import com.dtstack.taier.scheduler.dto.fill.QueryFillDataListDTO;
import com.dtstack.taier.scheduler.dto.fill.ScheduleFillJobParticipateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @Auther: dazhi
 * @Date: 2021/12/7 3:38 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface FillDataJobMapstructTransfer {

    FillDataJobMapstructTransfer INSTANCE = Mappers.getMapper(FillDataJobMapstructTransfer.class);

    /**
     * 补数据操作 vo -> dto
     */
    ScheduleFillJobParticipateDTO scheduleFillJobParticipateVoToScheduleFillJobParticipateDTO(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO);

    /**
     * 补数据列表 vo->dto
     */
    QueryFillDataListDTO fillDataListVOToFillDataListDTO(QueryFillDataListVO vo);

    /**
     * 补数据列表 domain->vo
     */
    @Mappings({
            @Mapping(target = "fillDataName", source = "jobName")
    })
    ReturnFillDataListVO fillDataListDTOToFillDataReturnListVO(ScheduleFillDataJob record);

    /**
     * 补数据实例 vo -> dto
     */
    QueryFillDataJobListDTO fillDataJobListVOToFillDataJobReturnListVO(QueryFillDataJobListVO vo);

    /**
     * 补数据实例 domain -> vo
     */
    FillDataJobVO scheduleJobToFillDataJobVO(ScheduleJob scheduleJob);
}
