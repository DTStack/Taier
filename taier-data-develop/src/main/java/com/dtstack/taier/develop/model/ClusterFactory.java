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

package com.dtstack.taier.develop.model;

import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.develop.model.datasource.ImmediatelyLoadDataSource;
import com.dtstack.taier.develop.model.system.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClusterFactory {

    @Autowired
    private Context context;

    @Autowired
    private ComponentFacade facade;

    public PartCluster newImmediatelyLoadCluster(Long clusterId) {
        if (null == clusterId) {
            throw new RdosDefineException(ErrorCode.CLUSTER_ID_EMPTY);
        }
        return new PartCluster(clusterId, context, new ImmediatelyLoadDataSource(clusterId, facade));
    }


}
