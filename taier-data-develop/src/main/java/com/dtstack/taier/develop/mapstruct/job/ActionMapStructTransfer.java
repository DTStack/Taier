package com.dtstack.taier.develop.mapstruct.job;

import com.dtstack.taier.develop.vo.schedule.ActionJobKillVO;
import com.dtstack.taier.scheduler.dto.schedule.ActionJobKillDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 11:35 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Mapper
public interface ActionMapStructTransfer {

    ActionMapStructTransfer INSTANCE = Mappers.getMapper(ActionMapStructTransfer.class);

    /**
     * ActionJobKillVO -> ActionJobKillDTO
     */
    ActionJobKillDTO actionJobKillVOToActionJobKillDTO(ActionJobKillVO vo);
}
