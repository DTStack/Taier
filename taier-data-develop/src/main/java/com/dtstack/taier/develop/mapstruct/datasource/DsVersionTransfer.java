package com.dtstack.taier.develop.mapstruct.datasource;

import com.dtstack.taier.dao.domain.DsVersion;
import com.dtstack.taier.develop.vo.datasource.DsVersionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DsVersionTransfer {

    DsVersionTransfer INSTANCE = Mappers.getMapper(DsVersionTransfer.class);

    DsVersionVO toInfoVO(DsVersion dsVersion);


    List<DsVersionVO> toInfoVOList(List<DsVersion> dsVersion);


}
