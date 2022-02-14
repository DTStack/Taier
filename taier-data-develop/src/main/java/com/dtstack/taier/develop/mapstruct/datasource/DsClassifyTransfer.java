package com.dtstack.taier.develop.mapstruct.datasource;

import com.dtstack.taier.dao.domain.DsClassify;
import com.dtstack.taier.develop.vo.datasource.DsClassifyVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DsClassifyTransfer {

    DsClassifyTransfer INSTANCE = Mappers.getMapper(DsClassifyTransfer.class);

    @Mapping(source = "id",target = "classifyId")
    DsClassifyVO toInfoVO(DsClassify dsClassify);


}
