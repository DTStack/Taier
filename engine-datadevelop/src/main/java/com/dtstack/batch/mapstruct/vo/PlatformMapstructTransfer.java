package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.vo.PlatformEventVO;
import com.dtstack.batch.web.platform.vo.BatchPlatformVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PlatformMapstructTransfer {

    PlatformMapstructTransfer INSTANCE = Mappers.getMapper(PlatformMapstructTransfer.class);

    /**
     * PlatformVO -> PlatformEventVO
     *
     * @param platformVO
     * @return
     */
    @Mappings({
        @Mapping(source="userId", target="dtUicUserId")
    })
    PlatformEventVO platformVOToPlatformEventVO(BatchPlatformVO platformVO);
}
