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

package com.dtstack.batch.vo;

import com.dtstack.batch.domain.Role;
import com.dtstack.engine.domain.User;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/10/26
 */
@Data
public class UserRoleVO {

    private Long userId = 0L;
    private User user;
    private List<Role> roles = new ArrayList<>();
    private Integer isSelf = 0;


    /**
     * 加入项目时间
     */
    private Timestamp gmtCreate;

    public void addRoles(Role role) {
        this.roles.add(role);
    }

    public int getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(int isSelf) {
        this.isSelf = isSelf;
    }

}
