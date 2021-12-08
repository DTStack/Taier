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

import com.dtstack.batch.mapstruct.console.KerberosConfigTransfer;
import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.KerberosConfig;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.pojo.ClientTemplate;
import com.dtstack.engine.master.impl.pojo.ComponentMultiTestResult;
import com.dtstack.engine.master.vo.KerberosConfigVO;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/node/component")
@Api(value = "/node/component", tags = {"组件接口"})
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    @ApiOperation(value = "getKerberosConfig", notes = "获取kerberos配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long"),
            @ApiImplicitParam(name = "componentType", value = "组件code", required = true, dataType = "int"),
            @ApiImplicitParam(name = "componentVersion", value = "组件版本", required = true, dataType = "String")
    })
    @PostMapping(value = "/getKerberosConfig")
    public KerberosConfigVO getKerberosConfig(@RequestParam("clusterId") Long clusterId, @RequestParam("componentType") Integer componentType, @RequestParam("componentVersion") String componentVersion) {
        KerberosConfig kerberosConfig = componentService.getKerberosConfig(clusterId, componentType, componentVersion);
        return KerberosConfigTransfer.INSTANCE.toVO(kerberosConfig);
    }

    @PostMapping(value = "/updateKrb5Conf")
    @ApiOperation(value = "updateKrb5Conf", notes = "更新krb5配置内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "krb5Content", value = "krb5配置内容", required = true, dataType = "String")
    })
    public void updateKrb5Conf(@RequestParam("krb5Content") String krb5Content) {
        componentService.updateKrb5Conf(krb5Content);
    }

    @PostMapping(value = "/closeKerberos")
    @ApiOperation(value = "关闭kerberos配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentId", value = "组件id", required = true, dataType = "long")
    })
    public void closeKerberos(@RequestParam("componentId") Long componentId) {
        componentService.closeKerberos(componentId);
    }

    @PostMapping(value = "/loadTemplate")
    @ApiOperation(value = "加载各个组件的前端渲染模版")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentType", value = "组件code", required = true, dataType = "int"),
            @ApiImplicitParam(name = "clusterName", value = "集群名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "version", value = "组件版本", required = true, dataType = "String"),
            @ApiImplicitParam(name = "storeType", value = "存储组件code", required = true, dataType = "int"),
            @ApiImplicitParam(name = "originVersion", value = "组件版本值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "deployType", value = "组件deployType类型", required = true, dataType = "int"),
    })
    public List<ClientTemplate> loadTemplate(@RequestParam("componentType") Integer componentType, @RequestParam("clusterName") String clusterName,
                                             @RequestParam("version") String version, @RequestParam("storeType") Integer storeType,
                                             @RequestParam("originVersion") String originVersion, @RequestParam("deployType") Integer deployType) {
        return componentService.loadTemplate(componentType, clusterName, version, storeType, originVersion, deployType);
    }


    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除组件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentId", value = "组件id", required = true, dataType = "long")
    })
    public void delete(@RequestParam("componentId") long componentId) {
        componentService.delete(componentId);
    }


    @GetMapping(value = "/getComponentVersion")
    @ApiOperation(value = "获取对应的组件能版本信息")
    public Map getComponentVersion() {
        return componentService.getComponentVersion();
    }

    @RequestMapping(value = "/getComponentStore", method = {RequestMethod.POST})
    @ApiOperation(value = "获取对应的组件能选择的存储组件类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterName", value = "集群名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "componentType", value = "组件code", required = true, dataType = "int")
    })
    public List<Integer> getComponentStore(@RequestParam("clusterName") String clusterName, @RequestParam("componentType") Integer componentType) {
        List<Component> componentStore = componentService.getComponentStore(clusterName, componentType);
        if (CollectionUtils.isEmpty(componentStore)) {
            return new ArrayList<>();
        }
        return componentStore.stream()
                .map(Component::getComponentTypeCode)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/testConnects")
    @ApiOperation(value = "测试所有组件连通性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterName", value = "集群名称", required = true, dataType = "String")
    })
    public List<ComponentMultiTestResult> testConnects(@RequestParam("clusterName") String clusterName) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("clusterName is null");
        }
        return componentService.testConnects(clusterName);
    }

    @PostMapping(value = "/testConnect")
    @ApiOperation(value = "测试单个组件连通性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentType", value = "组件code", required = true, dataType = "int"),
            @ApiImplicitParam(name = "clusterName", value = "集群名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "componentVersion", value = "组件版本", required = true, dataType = "String"),
    })
    public ComponentTestResult testConnect(@RequestParam("clusterName") String clusterName, @RequestParam("componentType") Integer componentType, @RequestParam("componentVersion") String componentVersion) {
        return componentService.testConnect(clusterName, componentType, StringUtils.isBlank(componentVersion) ? null : Collections.singletonMap(componentType, componentVersion));
    }

}



