package com.dtstack.batch.mapstruct.datasource;


import com.dtstack.batch.vo.datasource.DsTypeListVO;
import com.dtstack.batch.vo.datasource.DsTypeVO;
import com.dtstack.engine.domain.DsType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface DsTypeTransfer {

    DsTypeTransfer INSTANCE = Mappers.getMapper(DsTypeTransfer.class);


    @Mapping(source = "id",target = "typeId")
    DsTypeVO toInfoVO(DsType dsType);

    List<DsTypeVO> toInfoVOs(List<DsType> dsTypeList);

    List<DsTypeListVO> toDsTypeListVOs(List<DsType> dsTypeList);


}
