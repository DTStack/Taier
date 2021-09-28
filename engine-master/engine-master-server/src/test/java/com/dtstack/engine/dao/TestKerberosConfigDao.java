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

import com.dtstack.engine.domain.KerberosConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author chener
 * @Classname TestKerberosConfigDao
 * @Description TODO
 * @Date 2020/11/25 20:29
 * @Created chener@dtstack.com
 */
public interface TestKerberosConfigDao {

    @Insert({"INSERT INTO console_kerberos(cluster_id,name,open_kerberos,remote_path,principal,component_type,krb_name)VALUES(#{kerberosConfig.clusterId},#{kerberosConfig.name},#{kerberosConfig.openKerberos},#{kerberosConfig.remotePath},#{kerberosConfig.principal},#{kerberosConfig.componentType},#{kerberosConfig.krbName})"})
    @Options(useGeneratedKeys=true, keyProperty = "kerberosConfig.id", keyColumn = "id")
    Integer insert(@Param("kerberosConfig") KerberosConfig kerberosConfig);

    @Select({"select * from console_kerberos_config limit 1"})
    KerberosConfig getOne();
}
