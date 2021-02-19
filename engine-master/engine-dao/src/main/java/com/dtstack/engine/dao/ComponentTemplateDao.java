package com.dtstack.engine.dao;

import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2021-02-19
 */
public interface ComponentTemplateDao {

    String getByTypeName(@Param("typeName") String typeName);

}
