package com.dtstack.taier.scheduler.service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.mapper.ScheduleEngineJobCacheMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EngineJobCacheService extends ServiceImpl<ScheduleEngineJobCacheMapper, ScheduleEngineJobCache> {

    public ScheduleEngineJobCache getByJobId(String jobId) {
        return getBaseMapper()
                .selectOne(Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                        .eq(ScheduleEngineJobCache::getJobId, jobId));
    }

    public List<ScheduleEngineJobCache> listByStage(Long startId, String nodeAddress, Integer stage, String resource) {
        return getBaseMapper().listByStage(startId, nodeAddress, stage, resource);
    }

    public int updateNodeAddressFailover(String nodeAddress, List<String> jobIds, Integer stage) {
        ScheduleEngineJobCache jobCache = new ScheduleEngineJobCache();
        jobCache.setNodeAddress(nodeAddress);
        jobCache.setStage(stage);
        return getBaseMapper()
                .update(jobCache, Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                        .in(ScheduleEngineJobCache::getJobId, jobIds));

    }

    public int deleteByJobId(String jobId) {
        return getBaseMapper()
                .delete(Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                        .eq(ScheduleEngineJobCache::getJobId, jobId));
    }

    public List<ScheduleEngineJobCache> getByJobIds(List<String> jobIds) {
        return getBaseMapper()
                .selectList(Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                        .in(ScheduleEngineJobCache::getJobId, jobIds));
    }

    public int updateStage(String jobId, int stage, String nodeAddress, long priority, String waitReason) {
        ScheduleEngineJobCache engineJobCache = new ScheduleEngineJobCache();
        engineJobCache.setStage(stage);
        engineJobCache.setNodeAddress(nodeAddress);
        engineJobCache.setJobPriority(priority);
        if (StringUtils.isNotBlank(waitReason)) {
            engineJobCache.setWaitReason(waitReason);
        }
        engineJobCache.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        engineJobCache.setIsFailover(0);
        return getBaseMapper()
                .update(engineJobCache, Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                        .eq(ScheduleEngineJobCache::getJobId, jobId));
    }

    public int countByStage(String jobResource, List<Integer> stages, String nodeAddress) {
        return getBaseMapper()
                .selectCount(Wrappers.lambdaQuery(ScheduleEngineJobCache.class)
                        .eq(ScheduleEngineJobCache::getJobResource, jobResource)
                        .eq(ScheduleEngineJobCache::getNodeAddress, nodeAddress)
                        .in(ScheduleEngineJobCache::getStage, stages));
    }
}
