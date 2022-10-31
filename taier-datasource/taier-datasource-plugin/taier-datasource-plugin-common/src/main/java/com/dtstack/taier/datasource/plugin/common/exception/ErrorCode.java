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

package com.dtstack.taier.datasource.plugin.common.exception;

/**
 * common-loader 错误码
 *
 * @author ：wangchuan
 * date：Created in 上午11:09 2021/3/23
 * company: www.dtstack.com
 */
public enum ErrorCode implements IErrorCode {

    /**
     * The method is not supported
     */
    NOT_SUPPORT(0, "This method is not supported by this data source...");

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    ErrorCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误描述
     */
    private final String desc;
}
