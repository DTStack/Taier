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

package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * job接口相关入参
 *
 * @author luming
 * @date 2022/2/25
 */
@Builder
@Data
public class JobParam {

    private String jobId;

    private Boolean retry;

    private String cubeName;

    /**
     * Supported build type: ‘BUILD’, ‘MERGE’, ‘REFRESH’
     */
    private BuildType buildType;

    private Long startTime;

    private Long endTime;

    private JobParam.RequestConfig requestConfig;

    private String sql;

    /**
     * http连接属性设置
     */
    @Builder
    @Data
    public static class RequestConfig {
        private Integer socketTimeout;
        private Integer connectTimeout;
        private Integer connectionRequestTimeout;
    }

    public enum BuildType {
        BUILD,
        MERGE,
        REFRESH;
    }
}
