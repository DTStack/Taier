package com.dtstack.batch.mapstruct.fill;

import com.dtstack.batch.vo.fill.*;
import com.dtstack.engine.domain.ScheduleFillDataJob;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.dto.fill.FillDataJobListDTO;
import com.dtstack.engine.master.dto.fill.FillDataListDTO;
import com.dtstack.engine.master.dto.fill.ScheduleFillJobParticipateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/7 3:38 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface FillDataJobMapstructTransfer {

    FillDataJobMapstructTransfer INSTANCE = Mappers.getMapper(FillDataJobMapstructTransfer.class);


    ScheduleFillJobParticipateDTO scheduleFillJobParticipateVoToScheduleFillJobParticipateDTO(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO);

    FillDataListDTO fillDataListVOToFillDataListDTO(FillDataListVO vo);

    @Mappings({
            @Mapping(target = "fillDataName",source = "jobName"),
            @Mapping(target = "userId",source = "createUserId")
    })
    FillDataReturnListVO fillDataListDTOToFillDataReturnListVO(ScheduleFillDataJob record);

    FillDataJobListDTO fillDataJobListVOToFillDataJobReturnListVO(FillDataJobListVO vo);

    @Mappings({
            @Mapping(target = "userId",source = "createUserId")
    })
    FillDataJobVO scheduleJobToFillDataJobVO(ScheduleJob scheduleJob);
}
