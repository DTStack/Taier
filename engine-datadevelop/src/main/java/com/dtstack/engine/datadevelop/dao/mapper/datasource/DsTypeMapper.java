package com.dtstack.pubsvc.dao.mapper.datasource;

import com.dtstack.pubsvc.dao.mapper.IMapper;
import com.dtstack.pubsvc.dao.po.datasource.DsType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/10
 */
@Mapper
public interface DsTypeMapper extends IMapper<DsType> {
    /**
     * 添加数据源类型的权重值
     * 目前固定增加1
     * @param dataType
     * @param plusWeight
     * @return
     */
    Integer plusDataTypeWeight(@Param("dataType") String dataType, @Param("plusWeight") Integer plusWeight);
}
