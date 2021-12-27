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

package com.dtstack.batch.controller.console;

import com.dtstack.batch.mapstruct.console.TenantTransfer;
import com.dtstack.batch.vo.console.TenantVO;
import com.dtstack.engine.common.constrant.Cookies;
import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.batch.service.console.TenantService;
import com.dtstack.engine.master.vo.ClusterTenantVO;
import com.dtstack.engine.pager.PageResult;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
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
    public PageResult<List<ClusterTenantVO>> pageQuery(@RequestParam("clusterId") Long clusterId,
                                                       @RequestParam("tenantName") String tenantName,
                                                       @RequestParam("pageSize") int pageSize,
                                                       @RequestParam("currentPage") int currentPage) {
        Cluster cluster = clusterService.getCluster(clusterId);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        tenantName = tenantName == null ? "" : tenantName;
        return tenantService.pageQuery(clusterId, tenantName, pageSize, currentPage);
    }


    @PostMapping(value = "/bindingTenant")
    public void bindingTenant(@RequestParam("tenantId") Long tenantId, @RequestParam("clusterId") Long clusterId,
                              @RequestParam("queueId") Long queueId) throws Exception {
        Cluster cluster = clusterService.getCluster(clusterId);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        tenantService.bindingTenant(tenantId, clusterId, queueId, cluster.getClusterName());
    }

    @PostMapping(value = "/bindingQueue")
    public void bindingQueue(@RequestParam("queueId") Long queueId,
                             @RequestParam("tenantId") Long tenantId) {
        tenantService.bindingQueue(queueId, tenantId);
    }

    @GetMapping(value = "/listTenant")
    public List<TenantVO> listTenant() {
        List<Tenant> tenants = tenantService.listAllTenant();
        return TenantTransfer.INSTANCE.toVOs(tenants);
    }

    @PostMapping(value = "/addTenant")
    public void bindingTenant(@RequestParam("tenantName") String tenantName, @CookieValue(Cookies.DT_USER_ID) Long userId) throws Exception {
        if(StringUtils.isBlank(tenantName)){
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        Tenant tenant = tenantService.findByName(tenantName.trim());
        if(null != tenant){
            throw new RdosDefineException("tenant has exist");
        }
        tenantService.addTenant(tenantName,userId);
    }

}
