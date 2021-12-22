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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dtstack.batch.mapstruct.console.ClusterTransfer;
import com.dtstack.batch.vo.console.ClusterInfoVO;
import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.vo.ClusterEngineVO;
import com.dtstack.engine.master.vo.ClusterVO;
import com.dtstack.engine.pager.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/node/cluster")
@Api(value = "/node/cluster", tags = {"集群接口"})
public class ClusterController {

    @Autowired
    private ClusterService clusterService;

    @ApiOperation(value = "addCluster", notes = "创建集群")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterName", value = "集群名称", required = true, dataType = "String")
    })
    @PostMapping(value = "/addCluster")
    public Boolean addCluster(@RequestParam("clusterName") String clusterName) {
        return clusterService.addCluster(clusterName);
    }


    @ApiOperation(value = "pageQuery", notes = "集群列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页", required = true, dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataType = "int")
    })
    @PostMapping(value = "/pageQuery")
    public PageResult<List<ClusterInfoVO>> pageQuery(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize) {
        IPage<Cluster> clusterIPage = clusterService.pageQuery(currentPage, pageSize);
        List<Cluster> clusters = clusterIPage.getRecords();
        List<ClusterInfoVO> clusterInfoVOS = ClusterTransfer.INSTANCE.toInfoVOs(clusters);
        PageResult<List<ClusterInfoVO>> pageResult = new PageResult<>(currentPage, pageSize, clusterIPage.getTotal(), clusterInfoVOS);
        return pageResult;
    }

    @ApiOperation(value = "deleteCluster", notes = "删除集群")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long")
    })
    @PostMapping(value = "/deleteCluster")
    public Boolean deleteCluster(@RequestParam("clusterId") Long clusterId) {
        return clusterService.deleteCluster(clusterId);
    }

    @ApiOperation(value = "getCluster", notes = "获取集群详细信息 包含组件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long")
    })
    @GetMapping(value = "/getCluster")
    public ClusterVO getCluster(@RequestParam("clusterId") Long clusterId) {
        return clusterService.getConsoleClusterInfo(clusterId);
    }

    @ApiOperation(value = "getAllCluster", notes = "获取所有集群名称")
    @GetMapping(value = "/getAllCluster")
    public List<ClusterInfoVO> getAllCluster() {
        List<Cluster> clusters = clusterService.getAllCluster();
        return ClusterTransfer.INSTANCE.toInfoVOs(clusters);
    }

    @ApiOperation(value = "getClusterEngine", notes = "获取单个集群详细信息包含引擎")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "clusterId", value = "集群id", required = true, dataType = "long")
    })
    @GetMapping(value = "/getClusterEngine")
    public ClusterEngineVO getClusterEngine(@RequestParam("clusterId") Long clusterId) {
        return clusterService.getClusterEngine(clusterId);
    }

}
