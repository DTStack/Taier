package com.dtstack.batch.mapstruct.datasource;

import com.dtstack.batch.vo.datasource.DsVersionVO;
import com.dtstack.engine.domain.DsVersion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface DsVersionTransfer {

    DsVersionTransfer INSTANCE = Mappers.getMapper(DsVersionTransfer.class);

    DsVersionVO toInfoVO(DsVersion dsVersion);


    List<DsVersionVO> toInfoVOList(List<DsVersion> dsVersion);


}
