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

package com.dtstack.engine.master.impl;

import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.Engine;
import com.dtstack.engine.domain.EngineTenant;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.engine.master.vo.ComponentVO;
import com.dtstack.engine.master.vo.components.ComponentsResultVO;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.EngineDao;
import com.dtstack.engine.dao.EngineTenantDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.pluginapi.enums.ComputeType;
import com.dtstack.engine.pluginapi.enums.EDeployMode;
import com.dtstack.engine.pluginapi.enums.EngineType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * @author yuebai
 * @date 2021-06-21
 */
public class FlinkStandaloneTest extends AbstractTest {

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private EngineDao engineDao;

    @Test
    @Rollback
    public void testGetCluster() throws Exception {
        //创建集群
        String flinkStandalone = "testFlinkStandalone";
        ComponentsResultVO componentsResultVO = componentService.addOrCheckClusterWithName(flinkStandalone);
        Long clusterId = componentsResultVO.getClusterId();
        ComponentVO componentVo180 = testAddFlinkStandalone(clusterId, "180");
        Tenant tenant = Template.getTenantTemplate();
        tenant.setDtUicTenantId(-107L);
        tenantDao.insert(tenant);
        tenant = tenantDao.getByDtUicTenantId(tenant.getDtUicTenantId());
        Assert.assertNotNull(tenant);
        Assert.assertNotNull(tenant.getId());
        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.FLINK_ON_STANDALONE.getType());
        Assert.assertNotNull(engine);

        EngineTenant engineTenant = new EngineTenant();
        engineTenant.setTenantId(tenant.getId());
        engineTenant.setEngineId(engine.getId());
        engineTenantDao.insert(engineTenant);
        EDeployMode eDeployMode = taskParamsService.parseDeployTypeByTaskParams("", ComputeType.BATCH.getType(), EngineType.Flink.name(), tenant.getDtUicTenantId());
        Assert.assertEquals(EDeployMode.STANDALONE,eDeployMode);
        ComponentVO componentVO = testAddFlinkStandalone(clusterId, "110");
        Component one = componentDao.getOne(componentVO.getId());
        Assert.assertTrue(one.getIsDefault());
        Component component180 = componentDao.getOne(componentVo180.getId());
        Assert.assertFalse(component180.getIsDefault());
    }



    private ComponentVO testAddFlinkStandalone(Long clusterId,String version) {
        String componentTemplate = "[{\"dependencyKey\":\"\",\"dependencyValue\":\"\",\"key\":\"deploymode\",\"required\":true,\"type\":\"CHECKBOX\",\"value\":\"standalone\"}]\n";
        return componentService.addOrUpdateComponent(clusterId, "", null,
                version, "",
                componentTemplate, EComponentType.FLINK.getTypeCode(),null,"","",true,true, EDeployType.STANDALONE.getType());
    }


}
