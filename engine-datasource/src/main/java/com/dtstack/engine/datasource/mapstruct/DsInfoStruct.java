package com.dtstack.engine.datasource.mapstruct;

import com.dtstack.engine.datasource.dao.bo.datasource.DsListBO;
import com.dtstack.engine.datasource.dao.bo.datasource.DsServiceListBO;
import com.dtstack.engine.datasource.dao.bo.query.DsListQuery;
import com.dtstack.engine.datasource.dao.po.datasource.DsInfo;
import com.dtstack.engine.datasource.param.datasource.DsListParam;
import com.dtstack.engine.datasource.vo.datasource.DsDetailVO;
import com.dtstack.engine.datasource.vo.datasource.DsListVO;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceInfoVO;
import com.dtstack.engine.datasource.vo.datasource.api.DsServiceListVO;
import org.mapstruct.Mapper;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/9/10
 */
@Mapper(componentModel = "spring")
public interface DsInfoStruct {

    DsListQuery toDsListQuery(DsListParam dsListParam);

    DsServiceInfoVO toDsServiceInfoVO(DsInfo dsInfo);

    DsListVO toDsListVO(DsListBO t);

    DsDetailVO toDsDetailVO(DsInfo dsInfo);

    DsServiceListVO toDsServiceInfoVO(DsServiceListBO dsServiceListBO);

    DsServiceListVO toDsServiceListVO(DsServiceListBO t);
}
