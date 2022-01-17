package com.dtstack.batch.mapstruct.datasource;

import com.dtstack.batch.vo.datasource.DsClassifyVO;
import com.dtstack.engine.domain.DsClassify;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DsClassifyTransfer {

    DsClassifyTransfer INSTANCE = Mappers.getMapper(DsClassifyTransfer.class);

    @Mapping(source = "id",target = "classifyId")
    DsClassifyVO toInfoVO(DsClassify dsClassify);


}
