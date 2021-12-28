package com.dtstack.batch.mapstruct.fill;

import com.dtstack.batch.vo.fill.*;
import com.dtstack.engine.domain.ScheduleFillDataJob;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.dto.fill.QueryFillDataJobListDTO;
import com.dtstack.engine.master.dto.fill.QueryFillDataListDTO;
import com.dtstack.engine.master.dto.fill.ScheduleFillJobParticipateDTO;
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
            @Mapping(target = "fillDataName",source = "jobName"),
            @Mapping(target = "userId",source = "createUserId")
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
