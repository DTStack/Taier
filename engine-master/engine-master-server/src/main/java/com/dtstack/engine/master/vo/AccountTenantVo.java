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

package com.dtstack.engine.master.vo;


import com.dtstack.engine.domain.BaseEntity;
import io.swagger.annotations.ApiModel;

/**
 * @author yuebai
 * @date 2020-02-17
 */
@ApiModel
public class AccountTenantVo extends BaseEntity {

    private String name;

    private String password;

    private Integer engineType;

    private String modifyUserName;

    private Long modifyDtUicUserId;

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public Long getModifyDtUicUserId() {
        return modifyDtUicUserId;
    }

    public void setModifyDtUicUserId(Long modifyDtUicUserId) {
        this.modifyDtUicUserId = modifyDtUicUserId;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
