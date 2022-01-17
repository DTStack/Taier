package com.dtstack.batch.mapstruct.datasource;

import com.dtstack.batch.vo.datasource.DsDetailVO;
import com.dtstack.engine.domain.DsInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DsDetailTransfer {

    DsDetailTransfer INSTANCE = Mappers.getMapper(DsDetailTransfer.class);


    DsDetailVO toInfoVO(DsInfo dsInfo);
}
