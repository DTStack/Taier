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

package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * restful 数据源信息
 *
 * @author ：wangchuan
 * date：Created in 下午2:00 2021/8/9
 * company: www.dtstack.com
 */
@Data
@ToString
@SuperBuilder
public class RestfulSourceDTO extends AbstractSourceDTO {

    /**
     * 请求地址
     */
    private String url;

    /**
     * 协议 仅支持 HTTP/HTTPS
     */
    private String protocol;

    /**
     * 请求头信息
     */
    private Map<String, String> headers;

    /**
     * 连接超时时间，单位：秒
     */
    private Integer connectTimeout;

    /**
     * socket 超时时间，单位：秒
     */
    private Integer socketTimeout;

    /**
     * 是否开启本地缓存
     */
    private Boolean useCache;

    @Override
    public String getUsername() {
        throw new SourceException("The method is not supported");
    }

    @Override
    public String getPassword() {
        throw new SourceException("The method is not supported");
    }

    @Override
    public Integer getSourceType() {
        return DataSourceType.RESTFUL.getVal();
    }
}
