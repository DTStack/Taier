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

package com.dtstack.taier.datasource.api.dto.restful;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * http response
 *
 * @author ：wangchuan
 * date：Created in 下午4:39 2021/8/10
 * company: www.dtstack.com
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    /**
     * 返回状态码
     */
    private Integer statusCode;

    /**
     * 具体的返回信息
     */
    private String content;

    /**
     * 调用失败时的异常信息
     */
    private String errorMsg;

}
