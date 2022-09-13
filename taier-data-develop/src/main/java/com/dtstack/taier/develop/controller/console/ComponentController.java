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

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.KerberosConfig;
import com.dtstack.taier.develop.mapstruct.console.KerberosConfigTransfer;
import com.dtstack.taier.develop.service.console.ConsoleComponentService;
import com.dtstack.taier.develop.vo.console.ComponentModelVO;
import com.dtstack.taier.develop.vo.console.KerberosConfigVO;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.scheduler.impl.pojo.ClientTemplate;
import com.dtstack.taier.scheduler.vo.ComponentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/component")
@Api(value = "/component", tags = {"组件接口"})
public class ComponentController {

    @Autowired
    private ConsoleComponentService consoleComponentService;

    @ApiOperation(value = "getKerberosConfig", notes = "获取kerberos配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long"),
            @ApiImplicitParam(name = "componentType", value = "组件code", required = true, dataType = "int"),
            @ApiImplicitParam(name = "componentVersion", value = "组件版本", required = true, dataType = "String")
    })
    @PostMapping(value = "/getKerberosConfig")
    public R<KerberosConfigVO> getKerberosConfig(@RequestParam("clusterId") Long clusterId, @RequestParam("componentType") Integer componentType, @RequestParam("componentVersion") String componentVersion) {
        KerberosConfig kerberosConfig = consoleComponentService.getKerberosConfig(clusterId, componentType, componentVersion);
        return R.ok(KerberosConfigTransfer.INSTANCE.toVO(kerberosConfig));
    }

    @PostMapping(value = "/updateKrb5Conf")
    @ApiOperation(value = "updateKrb5Conf", notes = "更新krb5配置内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "krb5Content", value = "krb5配置内容", required = true, dataType = "String")
    })
    public R<Void> updateKrb5Conf(@RequestParam("krb5Content") String krb5Content) {
        consoleComponentService.updateKrb5Conf(krb5Content);
        return R.empty();
    }

    @PostMapping(value = "/closeKerberos")
    @ApiOperation(value = "关闭kerberos配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentId", value = "组件id", required = true, dataType = "long")
    })
    public R<Void> closeKerberos(@RequestParam("componentId") Long componentId) {
        consoleComponentService.closeKerberos(componentId);
        return R.empty();
    }

    @PostMapping(value = "/loadTemplate")
    @ApiOperation(value = "加载各个组件的前端渲染模版")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long", example = "-1L"),
            @ApiImplicitParam(name = "versionName", value = "组件版本名称", required = true, dataType = "string"),
            @ApiImplicitParam(name = "componentType", value = "组件code", required = true, dataType = "int"),
    })
    public R<List<ClientTemplate>> loadTemplate(@RequestParam("componentType") Integer componentType, @RequestParam("clusterId") Long clusterId,
                                                @RequestParam("versionName") String versionName, @RequestParam("deployType") Integer deployType) {
        EComponentType type = EComponentType.getByCode(componentType);
        return R.ok(consoleComponentService.loadTemplate(clusterId, type, versionName, EComponentType.HDFS, deployType));
    }


    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除组件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentId", value = "组件id", required = true, dataType = "long")
    })
    public R<Void> delete(@RequestParam("componentId") long componentId) {
        consoleComponentService.delete(componentId);
        return R.empty();
    }


    @PostMapping(value = "/getComponentStore")
    @ApiOperation(value = "获取对应的组件能选择的存储组件类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterName", value = "集群名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "componentType", value = "组件code", required = true, dataType = "int")
    })
    public R<List<Integer>> getComponentStore(@RequestParam("clusterName") String clusterName, @RequestParam("componentType") Integer componentType) {
        List<Component> componentStore = consoleComponentService.getComponentStore(clusterName, componentType);
        if (CollectionUtils.isEmpty(componentStore)) {
            return R.ok(new ArrayList<>());
        }
        return R.ok(componentStore.stream()
                .map(Component::getComponentTypeCode)
                .collect(Collectors.toList()));
    }

    @PostMapping(value = "/testConnects")
    @ApiOperation(value = "测试所有组件连通性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterName", value = "集群名称", required = true, dataType = "String")
    })
    public R<List<ComponentTestResult>> testConnects(@RequestParam("clusterId") Long clusterId) {
        return R.ok(consoleComponentService.testConnects(clusterId));
    }

    @PostMapping(value = "/testConnect")
    @ApiOperation(value = "测试单个组件连通性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentType", value = "组件code", required = true, dataType = "int"),
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "versionName", value = "组件版本", required = true, dataType = "String"),
    })
    public R<ComponentTestResult> testConnect(@RequestParam("clusterId") Long clusterId, @RequestParam("componentType") Integer componentType, @RequestParam("versionName") String versionName) {
        return R.ok(consoleComponentService.testConnect(clusterId, componentType, versionName));
    }

    @GetMapping(value = "/componentModels")
    @ApiOperation(value = "获取能配置的组件信息")
    public R<List<ComponentModelVO>> componentModels() {
        return R.ok(consoleComponentService.getComponentModels());
    }


    @GetMapping(value = "/getComponentInfo")
    @ApiOperation(value = "获取组件详细配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "componentId", value = "组件id", required = true, dataType = "long")
    })
    public R<ComponentVO> getComponentInfo(@RequestParam("componentId") Long componentId) {
        ComponentVO componentVO = consoleComponentService.getComponentInfo(componentId);
        return R.ok(componentVO);
    }


}



