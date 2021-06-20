package com.dtstack.engine.datadevelop.dao.mapper.datasource;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.engine.datadevelop.dao.bo.datasource.DsAuthRefBO;
import com.dtstack.engine.datadevelop.dao.po.datasource.DsAuthRef;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Mapper
public interface DsAuthRefMapper extends BaseMapper<DsAuthRef> {

    List<DsAuthRefBO> mapDaIdName(@Param("dataInfoIds") List<Long> dataInfoIds);

    List<Long> getDataIdByAppTypes(@Param("appTypes") List<Integer> appTypes);
}
