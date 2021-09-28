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

package com.dtstack.engine.datasource.vo.datasource.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 电话
     */
    private String phoneNumber;

    /**
     * DtUIC 用户 ID
     */
    private Long dtuicUserId;

    /**
     * 邮件
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer status;

    /**
     * 默认项目 ID
     */
    private Long defaultProjectId;


}
