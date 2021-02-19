package com.dtstack.engine.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author yuebai
 * @date 2021-02-19
 */
public interface TestConsoleComponentTemplateDao {
    @Insert({"INSERT INTO console_component_template (type_name,template_text) \n" +
            "VALUES (#{typeName},#{templateText})"})
    void insert(@Param("typeName") String typeName, @Param("templateText") String templateText);
}
