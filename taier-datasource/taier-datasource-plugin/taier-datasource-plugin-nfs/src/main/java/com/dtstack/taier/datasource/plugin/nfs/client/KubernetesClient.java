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

package com.dtstack.taier.datasource.plugin.nfs.client;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.nfs.util.NfsUtil;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.emc.ecs.nfsclient.nfs.nfs3.Nfs3;
import lombok.extern.slf4j.Slf4j;
/**
 * nfs client
 *
 * @author ：wangchuan
 * date：Created in 下午2:08 2022/3/15
 * company: www.dtstack.com
 */
@Slf4j
public class KubernetesClient extends AbsNoSqlClient {

    @Override
    public Boolean testCon(ISourceDTO source) {
        Nfs3 client = NfsUtil.getClient(source);
        AssertUtils.notNull(client, "nfs client can't be null");
        return true;
    }
}