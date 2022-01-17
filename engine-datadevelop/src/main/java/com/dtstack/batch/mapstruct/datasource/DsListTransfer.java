package com.dtstack.batch.mapstruct.datasource;

import com.dtstack.batch.bo.datasource.DsListParam;
import com.dtstack.batch.vo.datasource.DsInfoVO;
import com.dtstack.batch.vo.datasource.DsListVO;
import com.dtstack.engine.domain.DsInfo;
import com.dtstack.engine.domain.po.DsListBO;
import com.dtstack.engine.domain.po.DsListQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DsListTransfer {

    DsListTransfer INSTANCE = Mappers.getMapper(DsListTransfer.class);


    DsListQuery toInfoQuery(DsListParam dsListParam);


    DsListVO toInfoVO(DsListBO dsListBO);


    DsListVO dsInfoToDsListVO(DsInfo dsInfo);

    @Mapping(source = "id",target = "dataInfoId")
    DsInfoVO toDsInfoVO(DsInfo dsInfo);

    List<DsInfoVO> toDsInfoVOS(List<DsInfo> dsInfoList);



}
