package com.dtstack.taiga.develop.mapstruct.datasource;

import com.dtstack.taiga.dao.domain.DsInfo;
import com.dtstack.taiga.dao.domain.po.DsListBO;
import com.dtstack.taiga.dao.domain.po.DsListQuery;
import com.dtstack.taiga.develop.bo.datasource.DsListParam;
import com.dtstack.taiga.develop.vo.datasource.DsInfoVO;
import com.dtstack.taiga.develop.vo.datasource.DsListVO;
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
