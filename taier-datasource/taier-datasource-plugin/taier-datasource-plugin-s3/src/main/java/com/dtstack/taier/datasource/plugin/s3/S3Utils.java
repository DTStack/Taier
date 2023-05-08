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

package com.dtstack.taier.datasource.plugin.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.S3SourceDTO;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:01 2020/9/29
 * @Description：S3 工具类
 */
public class S3Utils {
    /**
     * 获取 S3 客户端
     *
     * @param source
     * @param queryDTO
     * @return
     */
    public static AmazonS3Client getClient(ISourceDTO source, SqlQueryDTO queryDTO) {
        S3SourceDTO s3SourceDTO = (S3SourceDTO) source;
        ClientConfiguration opts = new ClientConfiguration();
        //指定client的签名算法
//        opts.setSignerOverride("S3SignerType");
        opts.setRequestTimeout(60 * 1000);
        opts.setClientExecutionTimeout(60 * 1000);
        AWSCredentials credentials = new BasicAWSCredentials(s3SourceDTO.getUsername(), s3SourceDTO.getPassword());
        AmazonS3Client client = new AmazonS3Client(credentials, opts);
        client.setEndpoint(s3SourceDTO.getHostname());
        return client;
    }
}
