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
import com.dtstack.engine.dto.AccountDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yuebai
 * @date 2020-02-14
 */
@ApiModel
public class AccountVo  extends BaseEntity {

    private Long bindTenantId;

    /**
     * 操作用户ID
     */
    @ApiModelProperty(notes = "操作用户ID")
    private Long userId;

    /**
     * 数栈用户名
     */
    @ApiModelProperty(notes = "数栈用户名")
    private String username;

    /**
     * 数据库用户名
     */
    @ApiModelProperty(notes = "数据库用户名")
    private String name;

    private String phone;

    private String email;

    private String modifyUserName;

    private Integer engineType;

    @ApiModelProperty(notes = "账号类型")
    private Integer accountType;

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }


    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String password;

    /**
     *  绑定用户ID
     */
    @ApiModelProperty(notes = "绑定用户ID")
    private Long bindUserId;

    public Long getBindUserId() {
        return bindUserId;
    }

    public void setBindUserId(Long bindUserId) {
        this.bindUserId = bindUserId;
    }

    public Long getBindTenantId() {
        return bindTenantId;
    }

    public void setBindTenantId(Long bindTenantId) {
        this.bindTenantId = bindTenantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountVo() {
    }

    public AccountVo(AccountDTO accountDTO) {
        if(null != accountDTO){
            this.setName(accountDTO.getName());
            this.setUsername(accountDTO.getUsername());
            this.setModifyUserName(accountDTO.getModifyUserName());
            this.setGmtCreate(accountDTO.getGmtCreate());
            this.setGmtModified(accountDTO.getGmtModified());
            this.setId(accountDTO.getId());
        }
    }
}
