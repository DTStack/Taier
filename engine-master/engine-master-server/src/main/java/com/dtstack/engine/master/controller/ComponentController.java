package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.Component;
import com.dtstack.engine.api.domain.KerberosConfig;
import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.master.impl.ComponentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/component")
@Api(value = "/node/component", tags = {"组件接口"})
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    @RequestMapping(value="/listConfigOfComponents", method = {RequestMethod.POST})
    public String listConfigOfComponents(@RequestParam("tenantId") Long dtUicTenantId, @RequestParam("engineType") Integer engineType) {
        return componentService.listConfigOfComponents(dtUicTenantId, engineType);
    }

    @RequestMapping(value="/getOne", method = {RequestMethod.POST})
    public Component getOne(@RequestParam("id") Long id) {
        return componentService.getOne(id);
    }


    @RequestMapping(value="/getKerberosConfig", method = {RequestMethod.POST})
    public KerberosConfig getKerberosConfig(@RequestParam("clusterId") Long clusterId, @RequestParam("componentType") Integer componentType) {
        return componentService.getKerberosConfig(clusterId, componentType);
    }


    @RequestMapping(value="/addOrUpdateComponent", method = {RequestMethod.POST})
    public ComponentVO addOrUpdateComponent(@RequestParam("clusterId") Long clusterId, @RequestParam("componentConfig") String componentConfig,
                                            @RequestParam("resources") List<Resource> resources, @RequestParam("hadoopVersion") String hadoopVersion,
                                            @RequestParam("kerberosFileName") String kerberosFileName, @RequestParam("componentTemplate") String componentTemplate,
                                            @RequestParam("componentCode") Integer componentCode) {
        return componentService.addOrUpdateComponent(clusterId, componentConfig, resources, hadoopVersion, kerberosFileName, componentTemplate, componentCode);
    }

    @RequestMapping(value="/closeKerberos", method = {RequestMethod.POST})
    @ApiOperation(value="移除kerberos配置")
    public void closeKerberos(@RequestParam("componentId") Long componentId) {
        componentService.closeKerberos(componentId);
    }

    @RequestMapping(value="/addOrCheckClusterWithName", method = {RequestMethod.POST})
    public Map<String, Object> addOrCheckClusterWithName(@RequestParam("clusterName") String clusterName) {
        return componentService.addOrCheckClusterWithName(clusterName);
    }

    @RequestMapping(value="/config", method = {RequestMethod.POST})
    @ApiOperation(value = "解析zip中xml或者json")
    public List<Object> config(@RequestParam("resources") List<Resource> resources, @RequestParam("componentType") Integer componentType,@RequestParam("autoDelete") Boolean autoDelete) {
        return componentService.config(resources, componentType, autoDelete);
    }

    @RequestMapping(value="/downloadFile", method = {RequestMethod.POST})
    @ApiOperation(value = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name="type",value="0:kerberos配置文件 1:配置文件 2:模板文件",required=true, dataType = "int")
    })
    public File downloadFile(@RequestParam("componentId") Long componentId, @RequestParam("type") Integer downloadType, @RequestParam("componentType") Integer componentType,
                             @RequestParam("hadoopVersion") String hadoopVersion, @RequestParam("clusterName") String clusterName) {
        return componentService.downloadFile(componentId, downloadType, componentType, hadoopVersion, clusterName);
    }


    @RequestMapping(value="/loadTemplate", method = {RequestMethod.POST})
    @ApiOperation(value = "加载各个组件的默认值, 解析yml文件转换为前端渲染格式")
    public List<ClientTemplate> loadTemplate(@RequestParam("componentType") Integer componentType, @RequestParam("clusterName") String clusterName, @RequestParam("version") String version) {
        return componentService.loadTemplate(componentType, clusterName, version);
    }


    @RequestMapping(value="/delete", method = {RequestMethod.POST})
    @ApiOperation(value = "删除组件")
    public void delete(@RequestParam("componentIds") List<Long> componentIds) {
        componentService.delete(componentIds);
    }

    @RequestMapping(value="/getComponentVersion", method = {RequestMethod.POST})
    @ApiOperation(value = "获取对应的组件版本信息")
    public Map getComponentVersion() {
        return componentService.getComponentVersion();
    }


    @RequestMapping(value="/testConnects", method = {RequestMethod.POST})
    @ApiOperation(value = "测试所有组件连通性")
    public List<ComponentTestResult> testConnects(@RequestParam("clusterName") String clusterName) {
        return componentService.testConnects(clusterName);
    }
}
