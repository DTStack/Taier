package com.dtstack.engine.datasource.dao.mapper.datasource;

import com.dtstack.engine.datasource.dao.bo.datasource.DsAuthRefBO;
import com.dtstack.engine.datasource.dao.mapper.IMapper;
import com.dtstack.engine.datasource.dao.po.datasource.DsAuthRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Mapper
public interface DsAuthRefMapper extends IMapper<DsAuthRef> {

    List<DsAuthRefBO> mapDaIdName(@Param("dataInfoIds") List<Long> dataInfoIds);

    List<Long> getDataIdByAppTypes(@Param("appTypes") List<Integer> appTypes);
}
