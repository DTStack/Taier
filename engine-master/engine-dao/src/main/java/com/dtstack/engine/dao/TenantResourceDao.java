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

package com.dtstack.engine.dao;

import com.dtstack.engine.domain.TenantResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 11:01 上午 2020/10/15
 */
public interface TenantResourceDao {

    /**
    * @author zyd
    * @Description 插入
    * @Date 11:02 上午 2020/10/15
    * @Param [tenantResource]
    * @retrun java.lang.Integer
    **/
    Integer insert(TenantResource tenantResource);

    /**
    * @author zyd
    * @Description 根据租户id删除
    * @Date 11:05 上午 2020/10/15
    * @Param [tenantId, dtUicTenantId]
    * @retrun java.lang.Integer
    **/
    Integer delete(@Param("tenantId") Long tenantId, @Param("dtUicTenantId") Long dtUicTenantId);

    /**
    * @author zyd
    * @Description 根据uic租户id和任务类型查找
    * @Date 11:48 上午 2020/10/15
    * @Param [dtUicTenantId, taskType]
    * @retrun com.dtstack.engine.domain.TenantResource
    **/
    TenantResource selectByUicTenantIdAndTaskType(@Param("dtUicTenantId") Long dtUicTenantId,@Param("taskType") Integer taskType);

    /**
    * @author zyd
    * @Description 根据uic租户id查找
    * @Date 5:42 下午 2020/10/15
    * @Param [dtUicTenantId]
    * @retrun java.util.List<com.dtstack.engine.domain.TenantResource>
    **/
    List<TenantResource> selectByUicTenantId(Long dtUicTenantId);
}
