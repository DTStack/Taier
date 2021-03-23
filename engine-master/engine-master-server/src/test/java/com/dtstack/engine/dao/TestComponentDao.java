package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Component;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author chener
 * @Classname TestComponentDao
 * @Description TODO
 * @Date 2020/11/25 16:49
 * @Created chener@dtstack.com
 */
public interface TestComponentDao {

    @Insert({"INSERT INTO console_component(engine_id,component_name,component_type_code,hadoop_version,upload_file_name,kerberos_file_name,store_type)VALUES(#{component.engineId},#{component.componentName},#{component.componentTypeCode},#{component.hadoopVersion},#{component.uploadFileName},#{component.kerberosFileName},#{component.storeType})"})
    @Options(useGeneratedKeys=true, keyProperty = "component.id", keyColumn = "id")
    Integer insert(@Param("component") Component component);

    @Select({"select * from console_component limit 1"})
    Component getOne();
}
