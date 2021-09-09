package com.dtstack.engine.master.controller;

import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.master.vo.console.ConsoleJobVO;
import com.dtstack.engine.common.pojo.ClusterResource;
import com.dtstack.engine.master.impl.ConsoleService;
import com.dtstack.engine.master.impl.vo.ResourceTemplateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/console")
@Api(value = "/node/console", tags = {"控制台接口"})
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @RequestMapping(value="/nodeAddress", method = {RequestMethod.POST})
    public List<String> nodeAddress() {
        return consoleService.nodeAddress();
    }

    @RequestMapping(value="/searchJob", method = {RequestMethod.POST})
    public ConsoleJobVO searchJob(@RequestParam("jobName") String jobName) {
        return consoleService.searchJob(jobName);
    }

    @RequestMapping(value="/listNames", method = {RequestMethod.POST})
    public List<String> listNames(@RequestParam("jobName") String jobName) {
        return consoleService.listNames(jobName);
    }

    @RequestMapping(value="/jobResources", method = {RequestMethod.POST})
    public List<String> jobResources() {
        return consoleService.jobResources();
    }

    @RequestMapping(value="/overview", method = {RequestMethod.POST})
    @ApiOperation(value = "根据计算引擎类型显示任务")
    public Collection<Map<String, Object>> overview(@RequestParam("nodeAddress") String nodeAddress, @RequestParam("clusterName") String clusterName) {
        return consoleService.overview(nodeAddress, clusterName);
    }

    @RequestMapping(value="/groupDetail", method = {RequestMethod.POST})
    public PageResult groupDetail(@RequestParam("jobResource") String jobResource,
                                  @RequestParam("nodeAddress") String nodeAddress,
                                  @RequestParam("stage") Integer stage,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("currentPage") Integer currentPage, @RequestParam("dtToken") String dtToken) {
        return consoleService.groupDetail(jobResource, nodeAddress, stage, pageSize, currentPage, dtToken);
    }

    @RequestMapping(value="/jobStick", method = {RequestMethod.POST})
    public Boolean jobStick(@RequestParam("jobId") String jobId) {
        return consoleService.jobStick(jobId);
    }

    @RequestMapping(value="/stopJob", method = {RequestMethod.POST})
    public void stopJob(@RequestParam("jobId") String jobId) throws Exception {
        consoleService.stopJob(jobId);
    }

    @ApiOperation(value = "概览，杀死全部")
    @RequestMapping(value="/stopAll", method = {RequestMethod.POST})
    public void stopAll(@RequestParam("jobResource") String jobResource,
                        @RequestParam("nodeAddress") String nodeAddress) throws Exception {
        consoleService.stopAll(jobResource, nodeAddress);
    }

    @RequestMapping(value="/stopJobList", method = {RequestMethod.POST})
    public void stopJobList(@RequestParam("jobResource") String jobResource,
                            @RequestParam("nodeAddress") String nodeAddress,
                            @RequestParam("stage") Integer stage,
                            @RequestParam("jobIdList") List<String> jobIdList) throws Exception {
        consoleService.stopJobList(jobResource, nodeAddress, stage, jobIdList);
    }

    @RequestMapping(value="/clusterResources", method = {RequestMethod.POST})
    public ClusterResource clusterResources(@RequestParam("clusterName") String clusterName) {
        return consoleService.clusterResources(clusterName,null);
    }

    @ApiOperation(value = "获取任务类型及对应的资源模板")
    @RequestMapping(value = "/getTaskResourceTemplate",method = {RequestMethod.POST})
    public List<ResourceTemplateVO> getTaskResourceTemplate(){

        return consoleService.getTaskResourceTemplate();
    }
}
