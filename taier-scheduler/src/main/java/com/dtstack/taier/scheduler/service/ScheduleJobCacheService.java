package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.mapper.ScheduleEngineJobCacheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleJobCacheService extends ServiceImpl<ScheduleEngineJobCacheMapper, ScheduleEngineJobCache> {

    @Autowired
    private ScheduleEngineJobCacheMapper scheduleEngineJobCacheMapper;

    public ScheduleEngineJobCache getJobCacheByJobId(String jobId) {
        return scheduleEngineJobCacheMapper.selectOne(Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                .eq(ScheduleEngineJobCache::getJobId, jobId));
    }

    public int deleteByJobId(String jobId) {
        return scheduleEngineJobCacheMapper.delete(Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                .eq(ScheduleEngineJobCache::getJobId, jobId));
    }

    public List<ScheduleEngineJobCache> listByStage(long startId, String localAddress, Integer stage, String jobResource) {
        return scheduleEngineJobCacheMapper.listByStage(startId, localAddress, stage, jobResource);
    }

    public int updateStage(String jobId, int stage, String nodeAddress, long priority, String waitReason) {
        ScheduleEngineJobCache engineJobCache = new ScheduleEngineJobCache();
        engineJobCache.setJobId(jobId);
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setStage(stage);
        engineJobCache.setJobPriority(priority);
        engineJobCache.setWaitReason(waitReason);
        return scheduleEngineJobCacheMapper.update(engineJobCache, Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                .eq(ScheduleEngineJobCache::getJobId, jobId));
    }

    public List<String> getAllNodeAddress() {
        return this.lambdaQuery().select(ScheduleEngineJobCache::getNodeAddress).groupBy(ScheduleEngineJobCache::getNodeAddress)
                .list().stream().map(ScheduleEngineJobCache::getNodeAddress).collect(Collectors.toList());
    }

    public int countByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return scheduleEngineJobCacheMapper.selectCount(Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                .eq(ScheduleEngineJobCache::getJobResource, jobResource).eq(ScheduleEngineJobCache::getNodeAddress, nodeAddress)
                .in(ScheduleEngineJobCache::getStage, stages));
    }

    public Long minPriorityByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return scheduleEngineJobCacheMapper.minPriorityByStage(jobResource, stages, nodeAddress);

    }

    public int updateStageBatch(List<String> jobIds, int stage, String nodeAddress) {
        ScheduleEngineJobCache engineJobCache = new ScheduleEngineJobCache();
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setStage(stage);
        return scheduleEngineJobCacheMapper.update(engineJobCache, Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                .in(ScheduleEngineJobCache::getJobId, jobIds));
    }

    public void insert(String jobId, Integer computeType, int stage, String jobInfo, String nodeAddress, String jobName, long priority, String jobResource, Long tenantId) {
        ScheduleEngineJobCache engineJobCache = new ScheduleEngineJobCache();
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
}
