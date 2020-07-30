package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.master.impl.ComponentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.dtstack.engine.master.router.DtRequestParam;
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
        return componentService.listConfigOfComponents(dtUicTenantId, engineType);
    }

    @RequestMapping(value="/getOne", method = {RequestMethod.POST})
    public Component getOne(@DtRequestParam("id") Long id) {
        return componentService.getOne(id);
    }


    @RequestMapping(value="/getKerberosConfig", method = {RequestMethod.POST})
    public KerberosConfig getKerberosConfig(@DtRequestParam("clusterId") Long clusterId, @DtRequestParam("componentType") Integer componentType) {
        return componentService.getKerberosConfig(clusterId, componentType);
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
    public List<ClientTemplate> loadTemplate(@DtRequestParam("componentType") Integer componentType, @DtRequestParam("clusterName") String clusterName, @DtRequestParam("version") String version) {
        return componentService.loadTemplate(componentType, clusterName, version);
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

    @RequestMapping(value="/testConnects", method = {RequestMethod.POST})
    @ApiOperation(value = "测试所有组件连通性")
    public List<ComponentTestResult> testConnects(@DtRequestParam("clusterName") String clusterName) {
        return componentService.testConnects(clusterName);
    }
}
