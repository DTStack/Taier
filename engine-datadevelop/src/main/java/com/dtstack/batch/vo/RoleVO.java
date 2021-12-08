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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @author: toutian
 * @create: 2017/10/25
 */
@Slf4j
@Data
public class RoleVO extends Role {

    private List<Long> permissionIds;

    private String modifyUserName;

    public static RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        try {
            BeanUtils.copyProperties(role, vo);
        } catch (Exception e) {
            log.error("", e);
        }
        return vo;
    }

}
