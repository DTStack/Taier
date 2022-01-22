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

package com.dtstack.taiga.develop.controller.console;

import com.dtstack.taiga.common.constrant.Cookies;
import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.common.lang.web.R;
import com.dtstack.taiga.dao.domain.Cluster;
import com.dtstack.taiga.dao.domain.Tenant;
import com.dtstack.taiga.dao.pager.PageResult;
import com.dtstack.taiga.develop.mapstruct.console.TenantTransfer;
import com.dtstack.taiga.develop.service.console.TenantService;
import com.dtstack.taiga.develop.vo.console.ClusterTenantVO;
import com.dtstack.taiga.develop.vo.console.TenantVO;
import com.dtstack.taiga.scheduler.service.ClusterService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/node/tenant")
@Api(value = "/node/tenant", tags = {"租户接口"})
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ClusterService clusterService;

    @PostMapping(value = "/pageQuery")
    public R<PageResult<List<ClusterTenantVO>>> pageQuery(@RequestParam("clusterId") Long clusterId,
                                                          @RequestParam("tenantName") String tenantName,
                                                          @RequestParam("pageSize") int pageSize,
                                                          @RequestParam("currentPage") int currentPage) {
        Cluster cluster = clusterService.getCluster(clusterId);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        tenantName = tenantName == null ? "" : tenantName;
        return R.ok(tenantService.pageQuery(clusterId, tenantName, pageSize, currentPage));
    }


    @PostMapping(value = "/bindingTenant")
    public R<Void> bindingTenant(@RequestParam("tenantId") Long tenantId, @RequestParam("clusterId") Long clusterId,
                              @RequestParam("queueId") Long queueId) throws Exception {
        Cluster cluster = clusterService.getCluster(clusterId);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        tenantService.bindingTenant(tenantId, clusterId, queueId, cluster.getClusterName());
        return R.empty();
    }

    @PostMapping(value = "/bindingQueue")
    public R<Void> bindingQueue(@RequestParam("queueId") Long queueId,
                             @RequestParam("tenantId") Long tenantId) {
        tenantService.bindingQueue(queueId, tenantId);
        return R.empty();
    }

    @GetMapping(value = "/listTenant")
    public R<List<TenantVO>> listTenant() {
        List<Tenant> tenants = tenantService.listAllTenant();
        return R.ok(TenantTransfer.INSTANCE.toVOs(tenants));
    }

    @PostMapping(value = "/addTenant")
    public R<Void> bindingTenant(@RequestParam("tenantName") String tenantName, @CookieValue(Cookies.USER_ID) Long userId) throws Exception {
        if(StringUtils.isBlank(tenantName)){
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        Tenant tenant = tenantService.findByName(tenantName.trim());
        if(null != tenant){
            throw new RdosDefineException("tenant has exist");
        }
        tenantService.addTenant(tenantName,userId);
        return R.empty();
    }

}
