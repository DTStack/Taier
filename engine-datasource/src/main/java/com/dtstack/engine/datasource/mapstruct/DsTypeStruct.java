package com.dtstack.engine.datasource.mapstruct;

import com.dtstack.engine.datasource.dao.po.datasource.DsType;
import com.dtstack.engine.datasource.vo.datasource.DsTypeListVO;
import com.dtstack.engine.datasource.vo.datasource.DsTypeVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/9/10
 */
@Mapper(componentModel = "spring")
public interface DsTypeStruct {

    DsTypeListVO toDsTypeListVO(DsType dsType);

    List<DsTypeListVO> toDsTypeListVOs(List<DsType> dsTypes);

    DsTypeVO toDsTypeVO(DsType dsType);
}
