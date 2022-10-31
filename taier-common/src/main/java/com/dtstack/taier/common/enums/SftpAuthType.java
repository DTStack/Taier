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

package com.dtstack.taier.common.enums;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : hanbeikai
 * Date: 2021/12/16 12:13 上午
 * Description: No Description
 */
public enum SftpAuthType {

    /**
     * 密码校验
     */
    PASSWORD(1),
    /**
     * 密钥登录校验
     */
    RSA(2);

    private Integer type;

    SftpAuthType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
