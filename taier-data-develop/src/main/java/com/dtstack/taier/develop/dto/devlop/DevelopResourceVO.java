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

package com.dtstack.taier.develop.dto.devlop;

import com.dtstack.taier.dao.domain.DevelopResource;
import com.dtstack.taier.dao.domain.User;
import org.springframework.beans.BeanUtils;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/4
 */
public class DevelopResourceVO extends DevelopResource {

    public static DevelopResourceVO toVO(DevelopResource origin) {
        DevelopResourceVO vo = new DevelopResourceVO();
        try {
            BeanUtils.copyProperties(origin, vo);
        } catch (Exception e) {
        }
        return vo;
    }

    private User createUser;

    private User modifyUser;

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(User modifyUser) {
        this.modifyUser = modifyUser;
    }
}
