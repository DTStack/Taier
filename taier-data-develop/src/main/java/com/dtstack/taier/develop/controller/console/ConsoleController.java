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

import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.service.console.ConsoleService;
import com.dtstack.taier.develop.vo.console.ConsoleJobVO;
import com.dtstack.taier.pluginapi.pojo.ClusterResource;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/console")
@Api(value = "/console", tags = {"控制台接口"})
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @PostMapping(value = "/nodeAddress")
    public R<List<String>> nodeAddress() {
        return R.ok(consoleService.nodeAddress());
    }

    @PostMapping(value="/searchJob")
    public R<ConsoleJobVO> searchJob(@RequestParam("jobName") String jobName) {
        Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
        return R.ok(consoleService.searchJob(jobName));
    }

    @PostMapping(value="/listNames")
    public R<List<String>> listNames(@RequestParam("jobName") String jobName) {
        Preconditions.checkNotNull(jobName, "parameters of jobName not be null.");
        return R.ok(consoleService.listNames(jobName));
    }

    @PostMapping(value="/jobResources")
    public R<List<String>> jobResources() {
        return R.ok(consoleService.jobResources());
    }

    @PostMapping(value="/overview")
    @ApiOperation(value = "根据计算引擎类型显示任务")
    public R<Collection<Map<String, Object>>> overview(@RequestParam("nodeAddress") String nodeAddress, @RequestParam("clusterName") String clusterName) {
        Preconditions.checkNotNull(clusterName, "clusterName can not be null.");
        return R.ok(consoleService.overview(nodeAddress, clusterName));
    }

    @PostMapping(value="/groupDetail")
    public R<PageResult> groupDetail(@RequestParam("jobResource") String jobResource,
                                  @RequestParam("nodeAddress") String nodeAddress,
                                  @RequestParam("stage") Integer stage,
                                  @RequestParam("pageSize") Integer pageSize,
                                  @RequestParam("currentPage") Integer currentPage) {
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        Preconditions.checkNotNull(stage, "parameters of stage is required");
        Preconditions.checkArgument(currentPage != null && currentPage > 0, "parameters of currentPage is required");
        Preconditions.checkArgument(pageSize != null && pageSize > 0, "parameters of pageSize is required");
        return R.ok(consoleService.groupDetail(jobResource, nodeAddress, stage, pageSize, currentPage));
    }

    @PostMapping(value="/jobStick")
    public R<Boolean> jobStick(@RequestParam("jobId") String jobId) {
        Preconditions.checkNotNull(jobId, "parameters of jobId is required");
        return R.ok(consoleService.jobStick(jobId));
    }

    @PostMapping(value="/stopJob")
    public R<Void> stopJob(@RequestParam("jobId") String jobId) throws Exception {
        Preconditions.checkArgument(StringUtils.isNotBlank(jobId), "parameters of jobId is required");
        consoleService.stopJob(jobId);
        return R.empty();
    }

    @ApiOperation(value = "概览，杀死全部")
    @PostMapping(value="/stopAll")
    public R<Void> stopAll(@RequestParam("jobResource") String jobResource,
                        @RequestParam("nodeAddress") String nodeAddress) throws Exception {
        Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
        consoleService.stopAll(jobResource, nodeAddress);
        return R.empty();
    }

    @PostMapping(value="/stopJobList")
    public R<Void> stopJobList(@RequestParam("jobResource") String jobResource,
                            @RequestParam("nodeAddress") String nodeAddress,
                            @RequestParam("stage") Integer stage,
                            @RequestParam("jobIdList") List<String> jobIdList) throws Exception {
        if (CollectionUtils.isEmpty(jobIdList)){
            Preconditions.checkNotNull(jobResource, "parameters of jobResource is required");
            Preconditions.checkNotNull(stage, "parameters of stage is required");
        }
        consoleService.stopJobList(jobResource, nodeAddress, stage, jobIdList);
        return R.empty();
    }

    @PostMapping(value="/clusterResources")
    public R<ClusterResource> clusterResources(@RequestParam("clusterId") Long clusterId) {
        return R.ok(consoleService.clusterResources(clusterId));
    }
}
