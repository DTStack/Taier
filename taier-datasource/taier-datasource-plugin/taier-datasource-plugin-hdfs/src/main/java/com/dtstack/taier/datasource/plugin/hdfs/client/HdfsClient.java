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

package com.dtstack.taier.datasource.plugin.hdfs.client;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.hdfs.HdfsConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:53 2020/2/27
 * @Description：hdfs 客户端
 */
public class HdfsClient extends AbsNoSqlClient {
    private HdfsConnFactory hdfsConnFactory = new HdfsConnFactory();

    @Override
    public Boolean testCon(ISourceDTO source) {
        return hdfsConnFactory.testConn(source);
    }
}