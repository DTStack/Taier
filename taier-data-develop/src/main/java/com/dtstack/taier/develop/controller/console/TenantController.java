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

package com.dtstack.taier.develop.controller.console;

import com.dtstack.taier.common.constant.Cookies;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.common.util.RegexUtils;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.domain.Tenant;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.mapstruct.console.TenantTransfer;
import com.dtstack.taier.develop.service.console.TenantService;
import com.dtstack.taier.develop.vo.console.ClusterTenantVO;
import com.dtstack.taier.develop.vo.console.ComponentBindTenantVO;
import com.dtstack.taier.develop.vo.console.TenantVO;
import com.dtstack.taier.scheduler.service.ClusterService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tenant")
@Api(value = "/tenant", tags = {"租户接口"})
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ClusterService clusterService;

    @PostMapping(value = "/pageQuery")
    public R<PageResult<List<ClusterTenantVO>>> pageQuery(@RequestParam("clusterId") Long clusterId,
                                                          @RequestParam("name") String tenantName,
                                                          @RequestParam("pageSize") int pageSize,
                                                          @RequestParam("currentPage") int currentPage) {
        Cluster cluster = clusterService.getCluster(clusterId);
        if (cluster == null) {
            throw new TaierDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        tenantName = tenantName == null ? "" : tenantName;
        return R.ok(tenantService.pageQuery(clusterId, tenantName, pageSize, currentPage));
    }


    @PostMapping(value = "/bindingTenant")
    public R<Void> bindingTenant(@RequestBody ComponentBindTenantVO vo) throws Exception {
        Cluster cluster = clusterService.getCluster(vo.getClusterId());
        if (cluster == null) {
            throw new TaierDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        tenantService.bindingTenant(vo.getTenantId(), vo.getClusterId(), vo.getQueueName());
        return R.empty();
    }

    @PostMapping(value = "/bindingQueue")
    public R<Void> bindingQueue(@RequestParam("queueName") String queueName, @RequestParam("clusterId") Long clusterId, @RequestParam("tenantId") Long tenantId) {
        tenantService.updateTenantQueue(tenantId, clusterId, queueName);
        return R.empty();
    }

    @GetMapping(value = "/listTenant")
    public R<List<TenantVO>> listTenant() {
        List<Tenant> tenants = tenantService.listAllTenant();
        return R.ok(TenantTransfer.INSTANCE.toVOs(tenants));
    }

    @PostMapping(value = "/addTenant")
    public R<Void> addTenant(@RequestParam("tenantName") String tenantName, @RequestParam("tenantIdentity") String tenantIdentity, @CookieValue(Cookies.USER_ID) Long userId) throws Exception {
        if (StringUtils.isBlank(tenantName)) {
            throw new TaierDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        if (StringUtils.isBlank(tenantIdentity)) {
            throw new TaierDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        if (!RegexUtils.tenantName(tenantIdentity))
            throw new TaierDefineException(ErrorCode.TENANT_NAME_VERIFICATION_ERROR);
        Tenant tenant = tenantService.findByName(tenantName.trim());
        if (null != tenant) {
            throw new TaierDefineException("tenant has exist");
        }
        tenantService.addTenant(tenantName, userId, tenantIdentity);
        return R.empty();
    }

}
