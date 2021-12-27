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

package com.dtstack.batch.service.auth;

import com.dtstack.batch.common.constant.PublicConstent;
import com.dtstack.engine.common.login.domain.LicenseProductComponent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Set;

/**
 * @author toutian
 */
public interface IAuthService {

    @Cacheable(value = AuthCode.AUTH_EH_CACHE)
    Set<String> getUserCodes(Long userId, Long projectId, Long tenantId);

    boolean clearCache(Long userId, Long project, Long tenantId);

    List<String> getPermissionCodesByRoleId(Long roleId);

    /**
     * 由于使用了Spring cache，所以必须让bean受Spring管理。如果配置了proxyTargetClass = true，使用cglib代理，其代理类，需要在子类上添加@Cacheable注解
     * 否则，使用jdk动态代理，其代理接口，因此需要在接口方法上添加@Cacheable注解。
     * @param uicUrl
     * @param componentCode
     * @return
     */
    @Cacheable(value = AuthCode.AUTH_EH_CACHE)
    LicenseProductComponent fetchLicense(String uicUrl,String componentCode);

    @CacheEvict(value = AuthCode.AUTH_EH_CACHE)
    boolean clearLicenseCache(String uicUrl,String componentCode);

    @Cacheable(value = PublicConstent.AUTH_EH_CACHE)
    Set<Long> getUserAdminProjectIds(Long userId, Long tenantId);


}
