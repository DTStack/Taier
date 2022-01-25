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

package com.dtstack.taiga.dao.mapper;

import com.dtstack.taiga.dao.domain.TenantComponent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Reason:
 * Date: 2019/6/1
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public interface BatchTenantComponentDao {

    /**
     * 根据 tenantId 查询租户下所有的组件
     * @param tenantId
     * @return
     */
    List<TenantComponent> getByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 插入租户组件信息
     * @param tenantComponent
     * @return
     */
    boolean insert(TenantComponent tenantComponent);

    /**
     * 根据 tenantId、taskType 查询组件信息
     * @param tenantId
     * @param taskType
     * @return
     */
    TenantComponent getByTenantAndTaskType(@Param("tenantId") Long tenantId, @Param("taskType") Integer taskType);

    /**
     * 根据 tenantId 查询组件信息
     * @param tenantId
     * @return
     */
    List<Integer> getUsedTaskTypeList(@Param("tenantId") Long tenantId);

    /**
     * 根据 tenantId、componentIdentity、taskType
     * @param tenantId
     * @param componentIdentity 组件标识
     * @param taskType
     * @return
     */
    TenantComponent getByTenantIdAndComponentIdentity(@Param("tenantId") Long tenantId, @Param("componentIdentity") String componentIdentity, @Param("taskType") Integer taskType);


    /**
     * 根据租户Id删除租户下的组件
     * @param tenantId
     * @param modifyUserId
     * @return
     */
    Integer deleteByTenantId(@Param("tenantId") Long tenantId, @Param("modifyUserId") Long modifyUserId);

}
