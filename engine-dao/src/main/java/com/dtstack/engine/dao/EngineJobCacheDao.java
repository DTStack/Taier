package com.dtstack.engine.dao;

import com.dtstack.engine.domain.EngineJobCache;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface EngineJobCacheDao {

    int insert(@Param("jobId")String jobId, @Param("engineType") String engineType,
               @Param("computeType") Integer computeType, @Param("stage") int stage,
               @Param("jobInfo")String jobInfo, @Param("nodeAddress") String nodeAddress,
               @Param("jobName") String jobName, @Param("jobPriority") Long jobPriority, @Param("jobResource") String jobResource);

    int delete(@Param("jobId")String jobId);

    EngineJobCache getOne(@Param("jobId")String jobId);

    int updateStage(@Param("jobId") String jobId, @Param("stage") Integer stage,@Param("nodeAddress") String nodeAddress, @Param("jobPriority") Long jobPriority);

    List<EngineJobCache> listByStage(@Param("startId") Long id, @Param("nodeAddress") String nodeAddress, @Param("stage") Integer stage, @Param("jobResource") String jobResource);

    List<EngineJobCache> getByJobIds(@Param("jobIds") List<String> jobIds);

    List<String> listNames(@Param("jobName") String jobName);

    int countByStage(@Param("jobResource") String jobResource, @Param("stages") List<Integer> stages, @Param("nodeAddress") String nodeAddress);

    Long maxPriorityByStage(@Param("jobResource") String jobResource, @Param("stage") Integer stages, @Param("nodeAddress") String nodeAddress);

    List<String> getAllNodeAddress();

    Integer updateNodeAddressFailover(@Param("nodeAddress") String nodeAddress, @Param("jobIds") List<String> ids);

    List<EngineJobCache> listByFailover(@Param("startId") Long id, @Param("nodeAddress") String nodeAddress, @Param("stage") Integer stage);

    List<String> getJobResources();

    List<Map<String,Object>> groupByJobResource(@Param("nodeAddress") String nodeAddress);

    Long countByJobResource(@Param("jobResource") String jobResource, @Param("stage") Integer stage, @Param("nodeAddress") String nodeAddress);

    List<EngineJobCache> listByJobResource(@Param("jobResource") String jobResource, @Param("stage") Integer stage, @Param("nodeAddress") String nodeAddress, @Param("start") Integer start, @Param("pageSize") Integer pageSize);

    Integer deleteByJobIds(@Param("jobIds") List<String> jobIds);
}
