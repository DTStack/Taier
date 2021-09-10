package com.dtstack.engine.datasource.mapstruct;

import com.dtstack.engine.datasource.dao.po.datasource.DsClassify;
import com.dtstack.engine.datasource.vo.datasource.DsClassifyVO;
import org.mapstruct.Mapper;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/9/10
 */
@Mapper(componentModel = "spring")
public interface DsClassStruct {

    DsClassifyVO toDsClassifyVO(DsClassify t);
}
