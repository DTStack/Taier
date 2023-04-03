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

package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleJobCache;
import com.dtstack.taier.dao.mapper.ScheduleJobCacheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleJobCacheService extends ServiceImpl<ScheduleJobCacheMapper, ScheduleJobCache> {

    @Autowired
    private ScheduleJobCacheMapper scheduleJobCacheMapper;

    public ScheduleJobCache getJobCacheByJobId(String jobId) {
        return scheduleJobCacheMapper.selectOne(Wrappers.lambdaQuery(ScheduleJobCache.class)
                .eq(ScheduleJobCache::getJobId, jobId));
    }

    public int deleteByJobId(String jobId) {
        return scheduleJobCacheMapper.delete(Wrappers.lambdaQuery(ScheduleJobCache.class)
                .eq(ScheduleJobCache::getJobId, jobId));
    }

    public List<ScheduleJobCache> listByStage(long startId, String localAddress, Integer stage, String jobResource, Boolean selectJobInfo) {
        return scheduleJobCacheMapper.listByStage(startId, localAddress, stage, jobResource, selectJobInfo);
    }

    public int updateStage(String jobId, int stage, String nodeAddress, long priority, String waitReason) {
        ScheduleJobCache engineJobCache = new ScheduleJobCache();
        engineJobCache.setJobId(jobId);
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setStage(stage);
        engineJobCache.setJobPriority(priority);
        engineJobCache.setWaitReason(waitReason);
        return scheduleJobCacheMapper.update(engineJobCache, Wrappers.lambdaQuery(ScheduleJobCache.class)
                .eq(ScheduleJobCache::getJobId, jobId));
    }

    public List<String> getAllNodeAddress() {
        return this.lambdaQuery().select(ScheduleJobCache::getNodeAddress).groupBy(ScheduleJobCache::getNodeAddress)
                .list().stream().map(ScheduleJobCache::getNodeAddress).collect(Collectors.toList());
    }

    public int countByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return scheduleJobCacheMapper.selectCount(Wrappers.lambdaQuery(ScheduleJobCache.class)
                .eq(ScheduleJobCache::getJobResource, jobResource).eq(ScheduleJobCache::getNodeAddress, nodeAddress)
                .in(ScheduleJobCache::getStage, stages));
    }

    public Long minPriorityByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return scheduleJobCacheMapper.minPriorityByStage(jobResource, stages, nodeAddress);

    }

    public int updateStageBatch(List<String> jobIds, int stage, String nodeAddress) {
        ScheduleJobCache engineJobCache = new ScheduleJobCache();
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setStage(stage);
        return scheduleJobCacheMapper.update(engineJobCache, Wrappers.lambdaQuery(ScheduleJobCache.class)
                .in(ScheduleJobCache::getJobId, jobIds));
    }

    public void insert(String jobId, Integer computeType, int stage, String jobInfo, String nodeAddress, String jobName, long priority, String jobResource, Long tenantId) {
        ScheduleJobCache engineJobCache = new ScheduleJobCache();
        engineJobCache.setJobId(jobId);
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setStage(stage);
        engineJobCache.setJobPriority(priority);
        engineJobCache.setJobInfo(jobInfo);
        engineJobCache.setComputeType(computeType);
        engineJobCache.setJobResource(jobResource);
        engineJobCache.setJobName(jobName);
        engineJobCache.setTenantId(tenantId);
        this.save(engineJobCache);
    }


    public ScheduleJobCache getByJobId(String jobId) {
        return getBaseMapper()
                .selectOne(Wrappers.lambdaQuery(ScheduleJobCache.class)
                        .eq(ScheduleJobCache::getJobId, jobId));
    }

    public List<ScheduleJobCache> listByStage(Long startId, String nodeAddress, Integer stage, String resource) {
        return getBaseMapper().listByStage(startId, nodeAddress, stage, resource, Boolean.FALSE);
    }

    public int updateNodeAddressFailover(String nodeAddress, List<String> jobIds, Integer stage) {
        ScheduleJobCache jobCache = new ScheduleJobCache();
        jobCache.setNodeAddress(nodeAddress);
        jobCache.setStage(stage);
        return getBaseMapper()
                .update(jobCache, Wrappers.lambdaQuery(ScheduleJobCache.class)
                        .in(ScheduleJobCache::getJobId, jobIds));

    }


    public List<ScheduleJobCache> getByJobIds(List<String> jobIds) {
        return getBaseMapper()
                .selectList(Wrappers.lambdaQuery(ScheduleJobCache.class)
                        .in(ScheduleJobCache::getJobId, jobIds));
    }


}
