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

package com.dtstack.taier.datasource.plugin.csp_s3;

import com.dtstack.taier.datasource.api.dto.source.CspS3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * csp s3 工具类
 *
 * @author ：wangchuan
 * date：Created in 上午10:07 2021/12/6
 * company: www.dtstack.com
 */
public class CspS3Util {

    private static final Integer TIMEOUT = 60 * 1000;

    private static final String DEFAULT_DOMAIN = "cos.yun.unionpay.com";

    /**
     * 获取 csp s3 客户端
     *
     * @param sourceDTO 数据源信息
     * @return csp s3客户端
     */
    public static COSClient getClient(CspS3SourceDTO sourceDTO) {
        String domain = StringUtils.isBlank(sourceDTO.getDomain()) ? DEFAULT_DOMAIN : sourceDTO.getDomain();
        COSCredentials credentials = new BasicCOSCredentials(sourceDTO.getAccessKey(), sourceDTO.getSecretKey());
        SelfDefinedEndpointBuilder selfDefinedEndpointBuilder = StringUtils.isNotBlank(sourceDTO.getEndPoint()) ?
                new SelfDefinedEndpointBuilder(sourceDTO.getEndPoint()): new SelfDefinedEndpointBuilder(sourceDTO.getRegion(), domain);
        Region region = StringUtils.isNotBlank(sourceDTO.getRegion()) ? new Region(sourceDTO.getRegion()) : null;
        ClientConfig configuration = new ClientConfig(region);
        configuration.setConnectionRequestTimeout(TIMEOUT);
        configuration.setConnectionTimeout(TIMEOUT);
        configuration.setEndpointBuilder(selfDefinedEndpointBuilder);
        if (StringUtils.isNotBlank(sourceDTO.getEndPoint()) && sourceDTO.getEndPoint().startsWith("http://")) {
            configuration.setHttpProtocol(HttpProtocol.http);
        } else {
            configuration.setHttpProtocol(HttpProtocol.https);
        }
        return new COSClient(credentials, configuration);
    }

    /**
     * 关闭 csp s3
     *
     * @param cosClient csp s3客户端
     */
    public static void closeAmazonS3(COSClient cosClient) {
        if (Objects.nonNull(cosClient)) {
            cosClient.shutdown();
        }
    }

    /**
     * 强转 sourceDTO 为 CspS3SourceDTO
     *
     * @param sourceDTO csp s3 sourceDTO
     * @return 转换后的 csp s3 sourceDTO
     */
    public static CspS3SourceDTO convertSourceDTO(ISourceDTO sourceDTO) {
        if (!(sourceDTO instanceof CspS3SourceDTO)) {
            throw new SourceException("please pass in CspS3SourceDTO...");
        }
        return (CspS3SourceDTO) sourceDTO;
    }
}
