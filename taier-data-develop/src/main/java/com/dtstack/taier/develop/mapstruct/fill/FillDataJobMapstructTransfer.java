package com.dtstack.taier.develop.mapstruct.fill;

import com.dtstack.taier.dao.domain.ScheduleFillDataJob;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.develop.vo.fill.*;
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
            @Mapping(target = "fillDataName",source = "jobName")
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
