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

package com.dtstack.engine.master.controller;

import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.KerberosConfig;
import com.dtstack.engine.master.impl.pojo.ClientTemplate;
import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import com.dtstack.engine.pluginapi.pojo.DtScriptAgentLabel;
import com.dtstack.engine.master.impl.pojo.ComponentMultiTestResult;
import com.dtstack.engine.master.vo.ComponentUserVO;
import com.dtstack.engine.master.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.master.vo.components.ComponentsResultVO;
import com.dtstack.engine.master.vo.task.TaskGetSupportJobTypesResultVO;
import com.dtstack.engine.master.impl.ComponentService;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/component")
@Api(value = "/node/component", tags = {"组件接口"})
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    @RequestMapping(value="/listConfigOfComponents", method = {RequestMethod.POST})
    public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") Integer engineType) {
        return componentService.listConfigOfComponents(dtUicTenantId, engineType,null);
    }

    @RequestMapping(value="/listComponents", method = {RequestMethod.POST})
    public List<Component> listComponents(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") Integer engineType) {
        return componentService.listComponents(dtUicTenantId,engineType);
    }

    @RequestMapping(value="/getOne", method = {RequestMethod.POST})
    public Component getOne(@RequestParam("id") Long id) {
        return componentService.getOne(id);
    }


    @RequestMapping(value="/getKerberosConfig", method = {RequestMethod.POST})
    public KerberosConfig getKerberosConfig(@RequestParam("clusterId") Long clusterId, @RequestParam("componentType") Integer componentType, @RequestParam("componentVersion") String componentVersion) {
        return componentService.getKerberosConfig(clusterId, componentType,componentVersion);
    }

    @RequestMapping(value="/updateKrb5Conf", method = {RequestMethod.POST})
    public void updateKrb5Conf(@RequestParam("krb5Content") String krb5Content) {
        componentService.updateKrb5Conf(krb5Content);
    }

    @RequestMapping(value="/closeKerberos", method = {RequestMethod.POST})
    @ApiOperation(value="移除kerberos配置")
    public void closeKerberos(@RequestParam("componentId") Long componentId) {
        componentService.closeKerberos(componentId);
    }

    @RequestMapping(value="/addOrCheckClusterWithName", method = {RequestMethod.POST})
    public ComponentsResultVO addOrCheckClusterWithName(@RequestParam("clusterName") String clusterName) {
        return componentService.addOrCheckClusterWithName(clusterName);
    }


    @RequestMapping(value="/loadTemplate", method = {RequestMethod.POST})
    @ApiOperation(value = "加载各个组件的默认值, 解析yml文件转换为前端渲染格式")
    public List<ClientTemplate> loadTemplate(@RequestParam("componentType") Integer componentType, @RequestParam("clusterName") String clusterName,
                                             @RequestParam("version") String version, @RequestParam("storeType")Integer storeType,
                                             @RequestParam("originVersion") String originVersion, @RequestParam("deployType") Integer deployType) {
        return componentService.loadTemplate(componentType, clusterName, version,storeType,originVersion,deployType);
    }


    @RequestMapping(value="/delete", method = {RequestMethod.POST})
    @ApiOperation(value = "删除组件")
    public void delete(@RequestParam("componentIds") List<Integer> componentIds) {
        componentService.delete(componentIds);
    }


    @RequestMapping(value="/getComponentVersion", method = {RequestMethod.POST})
    @ApiOperation(value = "获取对应的组件版本信息")
    public Map getComponentVersion() {
        return componentService.getComponentVersion();
    }

    @RequestMapping(value="/getComponentStore", method = {RequestMethod.POST})
    @ApiOperation(value = "获取对应的组件版本信息")
    public List<Component> getComponentStore(@RequestParam("clusterName") String clusterName, @RequestParam("componentType") Integer componentType) {
        return componentService.getComponentStore(clusterName,componentType);
    }

    @RequestMapping(value="/testConnects", method = {RequestMethod.POST})
    @ApiOperation(value = "测试所有组件连通性")
    public List<ComponentMultiTestResult> testConnects(@RequestParam("clusterName") String clusterName) {
        return componentService.testConnects(clusterName);
    }

    @RequestMapping(value="/testConnect", method = {RequestMethod.POST})
    @ApiOperation(value = "测试单个组件连通性")
    public ComponentTestResult testConnect(@RequestParam("clusterName") String clusterName, @RequestParam("componentType") Integer componentType, @RequestParam("componentVersion")String componentVersion) {
        return componentService.testConnect(clusterName,componentType, StringUtils.isBlank(componentVersion)?null: Collections.singletonMap(componentType,componentVersion));
    }

    @RequestMapping(value="/refresh", method = {RequestMethod.POST})
    @ApiOperation(value = "刷新组件信息")
    public List<ComponentTestResult> refresh(@RequestParam("clusterName") String clusterName) {
        return componentService.refresh(clusterName);
    }

    @RequestMapping(value="/isYarnSupportGpus", method = {RequestMethod.POST})
    @ApiOperation(value = "判断集群是否支持gpu")
    public Boolean isYarnSupportGpus(@RequestParam("clusterName") String clusterName) {
        return componentService.isYarnSupportGpus(clusterName);
    }

    @RequestMapping(value="/getDtScriptAgentLabel", method = {RequestMethod.POST})
    @ApiOperation(value = "获取dtScript agent label信息")
    public List<DtScriptAgentLabel> getDtScriptAgentLabel(@RequestParam("agentAddress")String agentAddress){
        return componentService.getDtScriptAgentLabel(agentAddress);
    }

    @RequestMapping(value = "/getComponentVersionByEngineType",method = {RequestMethod.POST})
    @ApiOperation(value = "租户和engineType获取集群组件信息")
    public List<Component> getComponentVersionByEngineType(@RequestParam("uicTenantId") Long tenantId, @RequestParam("engineType")String  engineType){
        return componentService.getComponentVersionByEngineType(tenantId,engineType);
    }

    @RequestMapping(value = "/addOrUpdateComponentUser",method = {RequestMethod.POST})
    public void addOrUpdateComponentUser(@RequestParam("componentUserList")List<ComponentUserVO> componentUserList){
        componentService.addOrUpdateComponentUser(componentUserList);
    }

    @RequestMapping(value = "/getClusterComponentUser",method = {RequestMethod.POST})
    public List<ComponentUserVO> getClusterComponentUser(@RequestParam("clusterId")Long clusterId,
                                                         @RequestParam("componentTypeCode")Integer componentTypeCode,
                                                         @RequestParam("needRefresh") Boolean needRefresh,
                                                         @RequestParam("agentAddress")String agentAddress){
        return componentService.getClusterComponentUser(clusterId,componentTypeCode,needRefresh,agentAddress,false);
    }

    @RequestMapping(value = "/getComponentUserByUic",method = {RequestMethod.POST})
    public List<ComponentUserVO> getComponentUserByUic(@RequestParam("uicId")Long uicId,
                                                         @RequestParam("componentTypeCode")Integer componentTypeCode,
                                                         @RequestParam("needRefresh") Boolean needRefresh,
                                                         @RequestParam("agentAddress")String agentAddress){
        return componentService.getClusterComponentUser(uicId,componentTypeCode,needRefresh,agentAddress,true);
    }


    @RequestMapping(value="/getSupportJobTypes", method = {RequestMethod.POST})
    public List<TaskGetSupportJobTypesResultVO>  getSupportJobTypes(@RequestParam("appType") Integer appType,
                                                              @RequestParam("projectId") Long projectId,
                                                              @RequestParam("dt_tenant_id") Long dtuicTenantId){
        return componentService.getSupportJobTypes(appType,projectId,dtuicTenantId);
    }

}



