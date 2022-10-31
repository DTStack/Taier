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

package com.dtstack.taier.flink.client;

import com.dtstack.taier.pluginapi.JobIdentifier;
import org.apache.flink.client.program.ClusterClient;
import org.apache.hadoop.yarn.client.api.YarnClient;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public interface IClientManager {

    /**
     * get flink clusterClient
     * @param jobIdentifier jobIndentifier
     * @return yarnClient
     */
    ClusterClient getClusterClient(JobIdentifier jobIdentifier);


    /**
     * get yarn client
     * @return yarnClient
     */
    YarnClient getYarnClient();

    /**
     * do something when job submit failed
     */
    default void dealWithClientError() {}
}
