package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.EngineJobCache;
import com.dtstack.taier.dao.mapper.EngineJobCacheMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EngineJobCacheService extends ServiceImpl<EngineJobCacheMapper, EngineJobCache> {

    public EngineJobCache getByJobId(String jobId) {
        return getBaseMapper()
                .selectOne(Wrappers.lambdaQuery(EngineJobCache.class)
                        .eq(EngineJobCache::getJobId, jobId));
    }

    public List<EngineJobCache> listByStage(Long startId, String nodeAddress, Integer stage, String resource) {
        return getBaseMapper().listByStage(startId, nodeAddress, stage, resource);
    }

    public int updateNodeAddressFailover(String nodeAddress, List<String> jobIds, Integer stage) {
        EngineJobCache jobCache = new EngineJobCache();
        jobCache.setNodeAddress(nodeAddress);
        jobCache.setStage(stage);
        return getBaseMapper()
                .update(jobCache, Wrappers.lambdaQuery(EngineJobCache.class)
                        .in(EngineJobCache::getJobId, jobIds));

    }

    public int deleteByJobId(String jobId) {
        return getBaseMapper()
                .delete(Wrappers.lambdaQuery(EngineJobCache.class)
                        .eq(EngineJobCache::getJobId, jobId));
    }

    public List<EngineJobCache> getByJobIds(List<String> jobIds) {
        return getBaseMapper()
                .selectList(Wrappers.lambdaQuery(EngineJobCache.class)
                        .in(EngineJobCache::getJobId, jobIds));
    }

    public int updateStage(String jobId, int stage, String nodeAddress, long priority, String waitReason) {
        EngineJobCache engineJobCache = new EngineJobCache();
        engineJobCache.setStage(stage);
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setJobPriority(priority);
        if (StringUtils.isNotBlank(waitReason)) {
            engineJobCache.setWaitReason(waitReason);
        }
        engineJobCache.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        engineJobCache.setIsFailover(0);
        return getBaseMapper()
                .update(engineJobCache, Wrappers.lambdaQuery(EngineJobCache.class)
                        .eq(EngineJobCache::getJobId, jobId));
    }

    public int countByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return getBaseMapper()
                .selectCount(Wrappers.lambdaQuery(EngineJobCache.class)
                        .eq(EngineJobCache::getJobResource, jobResource)
                        .eq(EngineJobCache::getNodeAddress, nodeAddress)
                        .in(EngineJobCache::getStage, stages));
    }
}
