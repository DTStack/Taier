/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.Component;
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

    @Insert({"INSERT INTO console_component(engine_id,component_name,component_type_code,hadoop_version,upload_file_name,kerberos_file_name,store_type,is_default)VALUES(#{component.engineId},#{component.componentName},#{component.componentTypeCode},#{component.hadoopVersion},#{component.uploadFileName},#{component.kerberosFileName},#{component.storeType},#{component.isDefault})"})
    @Options(useGeneratedKeys=true, keyProperty = "component.id", keyColumn = "id")
    Integer insert(@Param("component") Component component);

    @Select({"select * from console_component limit 1"})
    Component getOne();
}
