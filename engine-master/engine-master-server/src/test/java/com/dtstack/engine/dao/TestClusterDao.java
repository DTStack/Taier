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

import com.dtstack.engine.domain.Cluster;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * @author chener
 * @Classname TestClusterDao
 * @Description TODO
 * @Date 2020/11/24 20:22
 * @Created chener@dtstack.com
 */
public interface TestClusterDao {

    @Insert({"INSERT INTO console_cluster(id,cluster_name,hadoop_version)VALUES(#{cluster.id},#{cluster.clusterName},#{cluster.hadoopVersion})"})
    @Options()
    Integer insert(@Param("cluster") Cluster cluster);

    @Select({"select * from console_cluster limit 1"})
    Cluster getOne();
}
