package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.EngineJobCache;
import com.dtstack.taier.dao.mapper.EngineJobCacheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleJobCacheService extends ServiceImpl<EngineJobCacheMapper, EngineJobCache> {

    @Autowired
    private EngineJobCacheMapper engineJobCacheMapper;

    public EngineJobCache getJobCacheByJobId(String jobId) {
        return engineJobCacheMapper.selectOne(Wrappers.lambdaQuery(EngineJobCache.class)
                .eq(EngineJobCache::getJobId, jobId));
    }

    public int deleteByJobId(String jobId) {
        return engineJobCacheMapper.delete(Wrappers.lambdaQuery(EngineJobCache.class)
                .eq(EngineJobCache::getJobId, jobId));
    }

    public List<EngineJobCache> listByStage(long startId, String localAddress, Integer stage, String jobResource) {
        return engineJobCacheMapper.listByStage(startId, localAddress, stage, jobResource);
    }

    public int updateStage(String jobId, int stage, String nodeAddress, long priority, String waitReason) {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setJobId(jobId);
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setStage(stage);
        engineJobCache.setJobPriority(priority);
        engineJobCache.setWaitReason(waitReason);
        return engineJobCacheMapper.update(engineJobCache, Wrappers.lambdaQuery(EngineJobCache.class)
                .eq(EngineJobCache::getJobId, jobId));
    }

    public List<String> getAllNodeAddress() {
        return this.lambdaQuery().select(EngineJobCache::getNodeAddress).groupBy(EngineJobCache::getNodeAddress)
                .list().stream().map(EngineJobCache::getNodeAddress).collect(Collectors.toList());
    }

    public int countByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return engineJobCacheMapper.selectCount(Wrappers.lambdaQuery(EngineJobCache.class)
                .eq(EngineJobCache::getJobResource, jobResource).eq(EngineJobCache::getNodeAddress, nodeAddress)
                .in(EngineJobCache::getStage, stages));
    }

    public Long minPriorityByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return engineJobCacheMapper.minPriorityByStage(jobResource, stages, nodeAddress);

    }

    public int updateStageBatch(List<String> jobIds, int stage, String nodeAddress) {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setStage(stage);
        return engineJobCacheMapper.update(engineJobCache, Wrappers.lambdaQuery(EngineJobCache.class)
                .in(EngineJobCache::getJobId, jobIds));
    }

    public void insert(String jobId, Integer computeType, int stage, String jobInfo, String nodeAddress, String jobName, long priority, String jobResource, Long tenantId) {
        EngineJobCache engineJobCache = new EngineJobCache();
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
