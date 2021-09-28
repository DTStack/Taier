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

import com.dtstack.engine.domain.Engine;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestEngineDao
 * @Description TODO
 * @Date 2020/11/24 19:58
 * @Created chener@dtstack.com
 */
public interface TestEngineDao {

    @Insert({"INSERT INTO console_engine(id,cluster_id,engine_name,engine_type,total_node,total_memory,total_core,sync_type)" +
            "VALUES(#{engine.id},#{engine.clusterId},#{engine.engineName},#{engine.engineType},#{engine.totalNode},#{engine.totalMemory}," +
            "#{engine.totalCore},#{engine.syncType})"})
    @Options()
    void insert(@Param("engine") Engine engine);
}
