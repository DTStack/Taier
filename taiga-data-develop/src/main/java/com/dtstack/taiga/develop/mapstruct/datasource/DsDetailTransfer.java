package com.dtstack.taiga.develop.mapstruct.datasource;

import com.dtstack.taiga.dao.domain.DsInfo;
import com.dtstack.taiga.develop.vo.datasource.DsDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DsDetailTransfer {

    DsDetailTransfer INSTANCE = Mappers.getMapper(DsDetailTransfer.class);


    DsDetailVO toInfoVO(DsInfo dsInfo);
}
