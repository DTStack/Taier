package com.dtstack.batch.mapstruct.datasource;

import com.dtstack.batch.bo.datasource.DsListParam;
import com.dtstack.batch.vo.datasource.DsListVO;
import com.dtstack.engine.domain.datasource.DsInfo;
import com.dtstack.engine.domain.po.DsListBO;
import com.dtstack.engine.domain.po.DsListQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DsListTransfer {

    DsListTransfer INSTANCE = Mappers.getMapper(DsListTransfer.class);


    DsListQuery toInfoQuery(DsListParam dsListParam);


    DsListVO toInfoVO(DsListBO dsListBO);


    DsListVO dsInfoToDsListVO(DsInfo dsInfo);

    List<DsListVO> dsInfosToDsListVOS(List<DsInfo> dsInfoList);



}
