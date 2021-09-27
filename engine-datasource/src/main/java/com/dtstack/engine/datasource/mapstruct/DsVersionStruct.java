package com.dtstack.engine.datasource.mapstruct;

import com.dtstack.engine.datasource.dao.po.datasource.DsVersion;
import com.dtstack.engine.datasource.vo.datasource.DsVersionVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/9/10
 */
@Mapper(componentModel = "spring")
public interface DsVersionStruct {

    DsVersionVO toDsVersionVO(DsVersion dsVersion);

    List<DsVersionVO> toDsVersionVOs(List<DsVersion> dsVersions);
}
