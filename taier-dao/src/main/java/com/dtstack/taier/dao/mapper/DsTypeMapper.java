package com.dtstack.taier.dao.mapper;


import com.dtstack.taier.dao.domain.DsType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tengzhen
 */
public interface DsTypeMapper {

    List<DsType> dsTypeList();


    List<DsType> queryDsTypeByClassify(@Param("classifyId") Long classifyId, @Param("search") String search);

    /**
     * 添加数据源类型的权重值
     * 目前固定增加1
     *
     * @param dataType
     * @param plusWeight
     * @return
     */
    Integer plusDataTypeWeight(@Param("dataType") String dataType, @Param("plusWeight") Integer plusWeight);
}
