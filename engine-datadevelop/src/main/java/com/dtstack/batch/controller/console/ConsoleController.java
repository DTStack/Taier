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

import com.dtstack.batch.service.console.ConsoleService;
import com.dtstack.engine.master.vo.console.ConsoleJobVO;
import com.dtstack.engine.pager.PageResult;
import com.dtstack.engine.pluginapi.pojo.ClusterResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/console")
@Api(value = "/node/console", tags = {"控制台接口"})
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @PostMapping(value="/nodeAddress")
    public List<String> nodeAddress() {
        return consoleService.nodeAddress();
    }

    @PostMapping(value="/searchJob")
    public ConsoleJobVO searchJob(@RequestParam("jobName") String jobName) {
        return consoleService.searchJob(jobName);
    }

    @PostMapping(value="/listNames")
    public List<String> listNames(@RequestParam("jobName") String jobName) {
        return consoleService.listNames(jobName);
    }

    @PostMapping(value="/jobResources")
    public List<String> jobResources() {
        return consoleService.jobResources();
    }

    @PostMapping(value="/overview")
    @ApiOperation(value = "根据计算引擎类型显示任务")
    public Collection<Map<String, Object>> overview(@RequestParam("nodeAddress") String nodeAddress, @RequestParam("clusterName") String clusterName) {
        return consoleService.overview(nodeAddress, clusterName);
    }

    @PostMapping(value="/groupDetail")
    public PageResult groupDetail(@RequestParam("jobResource") String jobResource,
                                  @RequestParam("nodeAddress") String nodeAddress,
                                  @RequestParam("stage") Integer stage,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("currentPage") Integer currentPage) {
        return consoleService.groupDetail(jobResource, nodeAddress, stage, pageSize, currentPage);
    }

    @PostMapping(value="/jobStick")
    public Boolean jobStick(@RequestParam("jobId") String jobId) {
        return consoleService.jobStick(jobId);
    }

    @PostMapping(value="/stopJob")
    public void stopJob(@RequestParam("jobId") String jobId) throws Exception {
        consoleService.stopJob(jobId);
    }

    @ApiOperation(value = "概览，杀死全部")
    @PostMapping(value="/stopAll")
    public void stopAll(@RequestParam("jobResource") String jobResource,
                        @RequestParam("nodeAddress") String nodeAddress) throws Exception {
        consoleService.stopAll(jobResource, nodeAddress);
    }

    @PostMapping(value="/stopJobList")
    public void stopJobList(@RequestParam("jobResource") String jobResource,
                            @RequestParam("nodeAddress") String nodeAddress,
                            @RequestParam("stage") Integer stage,
                            @RequestParam("jobIdList") List<String> jobIdList) throws Exception {
        consoleService.stopJobList(jobResource, nodeAddress, stage, jobIdList);
    }

    @PostMapping(value="/clusterResources")
    public ClusterResource clusterResources(@RequestParam("clusterName") String clusterName) {
        return consoleService.clusterResources(clusterName);
    }

}
