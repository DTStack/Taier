package com.dtstack.taier.develop.mapstruct.datasource;

import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.dao.domain.po.DsListBO;
import com.dtstack.taier.dao.domain.po.DsListQuery;
import com.dtstack.taier.develop.bo.datasource.DsListParam;
import com.dtstack.taier.develop.vo.datasource.DsInfoVO;
import com.dtstack.taier.develop.vo.datasource.DsListVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DsListTransfer {

    DsListTransfer INSTANCE = Mappers.getMapper(DsListTransfer.class);


    DsListQuery toInfoQuery(DsListParam dsListParam);


    DsListVO toInfoVO(DsListBO dsListBO);

    DsInfoVO toDsInfoVO(DsListBO dsListBO);

    @Mapping(source = "id", target = "dataInfoId")
    DsInfoVO toDsInfoVO(DsInfo dsInfo);

    List<DsInfoVO> toDsInfoVOS(List<DsInfo> dsInfoList);


}
