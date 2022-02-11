package com.dtstack.taiga.develop.mapstruct.datasource;

import com.dtstack.taiga.dao.domain.DsClassify;
import com.dtstack.taiga.develop.vo.datasource.DsClassifyVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DsClassifyTransfer {

    DsClassifyTransfer INSTANCE = Mappers.getMapper(DsClassifyTransfer.class);

    @Mapping(source = "id",target = "classifyId")
    DsClassifyVO toInfoVO(DsClassify dsClassify);


}
