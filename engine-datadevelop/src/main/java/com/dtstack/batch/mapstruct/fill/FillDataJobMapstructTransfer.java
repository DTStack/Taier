package com.dtstack.batch.mapstruct.fill;

import com.dtstack.batch.vo.fill.ScheduleFillJobParticipateVO;
import com.dtstack.engine.master.dto.fill.ScheduleFillJobParticipateDTO;
import org.mapstruct.Mapper;
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


    ScheduleFillJobParticipateDTO scheduleFillJobParticipateVoToScheduleFillJobParticipateDTO(ScheduleFillJobParticipateVO scheduleFillJobParticipateVO);



}
