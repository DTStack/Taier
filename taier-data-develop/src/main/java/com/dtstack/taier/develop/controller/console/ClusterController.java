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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.mapstruct.console.ClusterTransfer;
import com.dtstack.taier.develop.service.console.ConsoleClusterService;
import com.dtstack.taier.develop.vo.console.ClusterEngineVO;
import com.dtstack.taier.develop.vo.console.ClusterInfoVO;
import com.dtstack.taier.develop.vo.console.ClusterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cluster")
@Api(value = "/cluster", tags = {"集群接口"})
public class ClusterController {

    @Autowired
    private ConsoleClusterService consoleClusterService;

    @ApiOperation(value = "addCluster", notes = "创建集群")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterName", value = "集群名称", required = true, dataType = "String")
    })
    @PostMapping(value = "/addCluster")
    public R<Long> addCluster(@RequestParam("clusterName") String clusterName) {
        return R.ok(consoleClusterService.addCluster(clusterName));
    }


    @ApiOperation(value = "pageQuery", notes = "集群列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataType = "int")
    })
    @PostMapping(value = "/pageQuery")
    public R<PageResult<List<ClusterInfoVO>>> pageQuery(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize) {
        IPage<Cluster> clusterIPage = consoleClusterService.pageQuery(currentPage, pageSize);
        List<Cluster> clusters = clusterIPage.getRecords();
        List<ClusterInfoVO> clusterInfoVOS = ClusterTransfer.INSTANCE.toInfoVOs(clusters);
        PageResult<List<ClusterInfoVO>> pageResult = new PageResult<>(currentPage, pageSize, clusterIPage.getTotal(), clusterInfoVOS);
        return R.ok(pageResult);
    }

    @ApiOperation(value = "deleteCluster", notes = "删除集群")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long")
    })
    @PostMapping(value = "/deleteCluster")
    public R<Boolean> deleteCluster(@RequestParam("clusterId") Long clusterId) {
        return R.ok(consoleClusterService.deleteCluster(clusterId));
    }

    @ApiOperation(value = "getCluster", notes = "获取集群包含组件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long")
    })
    @GetMapping(value = "/getCluster")
    public R<ClusterVO> getCluster(@RequestParam("clusterId") Long clusterId) {
        return R.ok(consoleClusterService.getConsoleClusterInfo(clusterId));
    }

    @ApiOperation(value = "getAllCluster", notes = "获取所有集群名称")
    @GetMapping(value = "/getAllCluster")
    public R<List<ClusterInfoVO>> getAllCluster() {
        List<Cluster> clusters = consoleClusterService.getAllCluster();
        return R.ok(ClusterTransfer.INSTANCE.toInfoVOs(clusters));
    }

    @ApiOperation(value = "getClusterEngine", notes = "获取单个集群详细信息包含引擎")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long")
    })
    @GetMapping(value = "/getClusterEngine")
    public R<ClusterEngineVO> getClusterEngine(@RequestParam("clusterId") Long clusterId) {
        return R.ok(consoleClusterService.getClusterEngine(clusterId));
    }

}
