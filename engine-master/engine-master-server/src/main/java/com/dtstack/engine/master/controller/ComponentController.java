package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.router.DtRequestParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/component")
@Api(value = "/node/component", tags = {"组件接口"})
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    @RequestMapping(value="/listConfigOfComponents", method = {RequestMethod.POST})
    public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(@DtRequestParam("tenantId") Long dtUicTenantId, @DtRequestParam("engineType") Integer engineType) {
        return componentService.listConfigOfComponents(dtUicTenantId, engineType,null);
    }

    @RequestMapping(value="/getOne", method = {RequestMethod.POST})
    public Component getOne(@DtRequestParam("id") Long id) {
        return componentService.getOne(id);
    }


    @RequestMapping(value="/getKerberosConfig", method = {RequestMethod.POST})
    public KerberosConfig getKerberosConfig(@DtRequestParam("clusterId") Long clusterId, @DtRequestParam("componentType") Integer componentType,@DtRequestParam("componentVersion") String componentVersion) {
        return componentService.getKerberosConfig(clusterId, componentType,componentVersion);
    }

    @RequestMapping(value="/updateKrb5Conf", method = {RequestMethod.POST})
    public void updateKrb5Conf(@DtRequestParam("krb5Content") String krb5Content) {
        componentService.updateKrb5Conf(krb5Content);
    }

    @RequestMapping(value="/closeKerberos", method = {RequestMethod.POST})
    @ApiOperation(value="移除kerberos配置")
    public void closeKerberos(@DtRequestParam("componentId") Long componentId) {
        componentService.closeKerberos(componentId);
    }

    @RequestMapping(value="/addOrCheckClusterWithName", method = {RequestMethod.POST})
    public ComponentsResultVO addOrCheckClusterWithName(@DtRequestParam("clusterName") String clusterName) {
        return componentService.addOrCheckClusterWithName(clusterName);
    }


    @RequestMapping(value="/loadTemplate", method = {RequestMethod.POST})
    @ApiOperation(value = "加载各个组件的默认值, 解析yml文件转换为前端渲染格式")
    public List<ClientTemplate> loadTemplate(@DtRequestParam("componentType") Integer componentType, @DtRequestParam("clusterName") String clusterName,
                                             @DtRequestParam("version") String version,@DtRequestParam("storeType")Integer storeType) {
        return componentService.loadTemplate(componentType, clusterName, version,storeType);
    }


    @RequestMapping(value="/delete", method = {RequestMethod.POST})
    @ApiOperation(value = "删除组件")
    public void delete(@DtRequestParam("componentIds") List<Integer> componentIds) {
        componentService.delete(componentIds);
    }


    @RequestMapping(value="/getComponentVersion", method = {RequestMethod.POST})
    @ApiOperation(value = "获取对应的组件版本信息")
    public Map getComponentVersion() {
        return componentService.getComponentVersion();
    }

    @RequestMapping(value="/getComponentStore", method = {RequestMethod.POST})
    @ApiOperation(value = "获取对应的组件版本信息")
    public List<Component> getComponentStore(@DtRequestParam("clusterName") String clusterName,@DtRequestParam("componentType") Integer componentType) {
        return componentService.getComponentStore(clusterName,componentType);
    }

    @RequestMapping(value="/testConnects", method = {RequestMethod.POST})
    @ApiOperation(value = "测试所有组件连通性")
    public List<ComponentTestResult> testConnects(@DtRequestParam("clusterName") String clusterName) {
        return componentService.testConnects(clusterName);
    }

    @RequestMapping(value="/testConnect", method = {RequestMethod.POST})
    @ApiOperation(value = "测试单个组件连通性")
    public ComponentTestResult testConnect(@DtRequestParam("clusterName") String clusterName,@DtRequestParam("componentType") Integer componentType) {
        return componentService.testConnect(clusterName,componentType,null);
    }

    @RequestMapping(value="/refresh", method = {RequestMethod.POST})
    @ApiOperation(value = "刷新组件信息")
    public List<ComponentTestResult> refresh(@DtRequestParam("clusterName") String clusterName) {
        return componentService.refresh(clusterName);
    }

    @RequestMapping(value="/isYarnSupportGpus", method = {RequestMethod.POST})
    @ApiOperation(value = "判断集群是否支持gpu")
    public Boolean isYarnSupportGpus(@DtRequestParam("clusterName") String clusterName) {
        return componentService.isYarnSupportGpus(clusterName);
    }
}



