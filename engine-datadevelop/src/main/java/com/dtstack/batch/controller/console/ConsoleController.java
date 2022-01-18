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
import com.dtstack.engine.common.lang.web.R;
import com.dtstack.engine.master.service.ComponentConfigService;
import com.dtstack.engine.master.utils.LocalCacheUtil;
import com.dtstack.engine.master.vo.console.ConsoleJobVO;
import com.dtstack.engine.pager.PageResult;
import com.dtstack.engine.pluginapi.pojo.ClusterResource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/node/console")
@Api(value = "/node/console", tags = {"控制台接口"})
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private ComponentConfigService componentConfigService;

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
    public R<ClusterResource> clusterResources(@RequestParam("clusterName") String clusterName) {
        return R.ok(consoleService.clusterResources(clusterName));
    }

    // todo qiuyun test
    @PostMapping(value = "/testCache")
    public R<String> testCache(Long clusterId, Integer componentType, boolean isFilter, String componentVersion, Long componentId) {
        Map<String, Object> cacheComponentConfigMap = componentConfigService.getCacheComponentConfigMap(clusterId, componentType, isFilter, componentVersion, componentId);
        return R.ok("ok");
    }

    //模拟数据库
    private static final Map<String, Map<String, String>> db = new ConcurrentHashMap<>();

    //模拟数据库数据
    static {
        Map<String, String> map = new HashMap();
        map.put("uId", "1001");
        map.put("name", "name_" + "1001");
        db.put("1001", map);
    }

    // todo qiuyun test
    @PostMapping(value = "/testCache2")
    public R<String> testCache2() throws InterruptedException {
        //1.测试读取缓存
        for (int i = 0; i <= 1; i++) {
            System.out.println(findUser("1001"));
        }
        //2.测试缓存失效
        Thread.sleep(5000);
        System.out.println("--------------");
        System.out.println("过期后查询：" + findUser("1001"));
        //3.测试缓存更新
        updateUser("1001", "name_2");
        System.out.println("更新后查询：" + findUser("1001"));
        //4.测试缓存移除
        // deleteUser("1001");
        // System.out.println("删除后查询：" + findUser("1001"));
        return R.ok("ok");
    }

    /**
     * 读取缓存测试
     *
     * @param uId
     * @return
     */
    private static Map<String, String> findUser(String uId) {
        String group = "user";
        Object o = LocalCacheUtil.get(group, uId);
        if (o != null) {
            System.out.println("------->缓存命中，key：" + uId);
            return (Map<String, String>) o;
        }
        System.out.println("------->缓存未命中，key：" + uId);
        Map<String, String> user = db.get(uId);
        if (user != null) {
            System.out.println("------->放缓存，key：" + uId);
            LocalCacheUtil.put(group, uId, user, 3000L);
        }
        return user;
    }

    private static void updateUser(String uId, String name) {
        String group = "user";
        Map<String, String> user = findUser(uId);
        if (user == null) {
            throw new RuntimeException("user not exist,uId:" + uId);
        }
        System.out.println("------->删除缓存，key：" + uId);
        LocalCacheUtil.remove(group, uId);
        user.put("name", name);
        db.put(uId, user);
        //如果担心此期间其他请求刷新缓存，可以在db修改后再remove一次缓存（缓存双淘汰）
    }

    private static void deleteUser(String uId) {
        String group = "user";
        Map<String, String> user = findUser(uId);
        if (user == null) {
            //不存在直接认为成功
            return;
        }
        System.out.println("------->删除缓存，key：" + uId);
        LocalCacheUtil.remove(group, uId);
        db.remove(uId);
        //如果担心此期间其他请求刷新缓存，可以在db删除后再remove一次缓存（缓存双淘汰）
    }
}
