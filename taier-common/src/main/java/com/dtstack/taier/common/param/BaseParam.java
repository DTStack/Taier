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

package com.dtstack.taier.common.param;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Auther: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/17 11:28
 * @Description: SDK 基类
 */
@Data
@ToString
class BaseParam {
    /**
     * 通道 -- 默认是 RDOS_SDK
     */
    @ApiModelProperty(hidden = true)
    private String channel = "Taier";

    /**
     * SDK 版本
     */
    @ApiModelProperty(hidden = true)
    private String sdkVersion = "v1.0";

    /**
     * 当前时间戳
     */
    @ApiModelProperty(hidden = true)
    private Long timestamp = System.currentTimeMillis();

    /**
     * 鉴权方式
     */
    @ApiModelProperty(hidden = true)
    private String signType = "DEFAULT";
}
