package com.dtstack.engine.datadevelop.dao.mapper.datasource;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtstack.engine.datadevelop.dao.po.datasource.DsType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Mapper
public interface DsTypeMapper extends BaseMapper<DsType> {
    /**
     * 添加数据源类型的权重值
     * 目前固定增加1
     * @param dataType
     * @param plusWeight
     * @return
     */
    Integer plusDataTypeWeight(@Param("dataType") String dataType, @Param("plusWeight") Integer plusWeight);
}
