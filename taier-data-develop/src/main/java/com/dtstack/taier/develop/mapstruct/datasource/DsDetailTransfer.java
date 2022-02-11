package com.dtstack.taier.develop.mapstruct.datasource;

import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.vo.datasource.DsDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DsDetailTransfer {

    DsDetailTransfer INSTANCE = Mappers.getMapper(DsDetailTransfer.class);


    DsDetailVO toInfoVO(DsInfo dsInfo);
}
