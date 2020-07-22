package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.impl.ConsoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/console")
@Api(value = "/node/console", tags = {"控制台接口"})
public class ConsoleController implements com.dtstack.engine.api.service.ConsoleService {

    @Autowired
    private ConsoleService consoleService;

    @RequestMapping(value="/nodeAddress", method = {RequestMethod.POST})
    public List<String> nodeAddress() {
        return consoleService.nodeAddress();
    }

    @RequestMapping(value="/searchJob", method = {RequestMethod.POST})
    public Map<String, Object> searchJob(@DtRequestParam("jobName") String jobName) {
        return consoleService.searchJob(jobName);
    }

    @RequestMapping(value="/listNames", method = {RequestMethod.POST})
    public List<String> listNames(@DtRequestParam("jobName") String jobName) {
        return consoleService.listNames(jobName);
    }

    @RequestMapping(value="/jobResources", method = {RequestMethod.POST})
    public List<String> jobResources() {
        return consoleService.jobResources();
    }

    @RequestMapping(value="/overview", method = {RequestMethod.POST})
    @ApiOperation(value = "根据计算引擎类型显示任务")
    public Collection<Map<String, Object>> overview(@DtRequestParam("nodeAddress") String nodeAddress, @DtRequestParam("clusterName") String clusterName) {
        return consoleService.overview(nodeAddress, clusterName);
    }

    @RequestMapping(value="/groupDetail", method = {RequestMethod.POST})
    public Map<String, Object> groupDetail(@DtRequestParam("jobResource") String jobResource,
                                           @DtRequestParam("nodeAddress") String nodeAddress,
                                           @DtRequestParam("stage") Integer stage,
                                           @DtRequestParam("pageSize") Integer pageSize,
                                           @DtRequestParam("currentPage") Integer currentPage,@DtRequestParam("dtToken") String dtToken) {
        return consoleService.groupDetail(jobResource, nodeAddress, stage, pageSize, currentPage, dtToken);
    }

    @RequestMapping(value="/jobStick", method = {RequestMethod.POST})
    public Boolean jobStick(@DtRequestParam("jobId") String jobId) {
        return consoleService.jobStick(jobId);
    }

    @RequestMapping(value="/stopJob", method = {RequestMethod.POST})
    public void stopJob(@DtRequestParam("jobId") String jobId) throws Exception {
        consoleService.stopJob(jobId);
    }

    @ApiOperation(value = "概览，杀死全部")
    @RequestMapping(value="/stopAll", method = {RequestMethod.POST})
    public void stopAll(@DtRequestParam("jobResource") String jobResource,
                        @DtRequestParam("nodeAddress") String nodeAddress) throws Exception {
        consoleService.stopAll(jobResource, nodeAddress);
    }

    @RequestMapping(value="/stopJobList", method = {RequestMethod.POST})
    public void stopJobList(@DtRequestParam("jobResource") String jobResource,
                            @DtRequestParam("nodeAddress") String nodeAddress,
                            @DtRequestParam("stage") Integer stage,
                            @DtRequestParam("jobIdList") List<String> jobIdList) throws Exception {
        consoleService.stopJobList(jobResource, nodeAddress, stage, jobIdList);
    }

    @RequestMapping(value="/clusterResources", method = {RequestMethod.POST})
    public Map<String, Object> clusterResources(@DtRequestParam("clusterName") String clusterName) {
        return consoleService.clusterResources(clusterName);
    }
}
