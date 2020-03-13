package com.dtstack.engine.service.db.mapper;

import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface RdosEngineJobCacheMapper {

    int insert(@Param("jobId")String jobId,  @Param("engineType") String engineType,
               @Param("computeType") Integer computeType, @Param("stage") int stage,
               @Param("jobInfo")String jobInfo, @Param("nodeAddress") String nodeAddress,
               @Param("jobName") String jobName, @Param("jobPriority") Long jobPriority, @Param("groupName") String groupName);

    int delete(@Param("jobId")String jobId);

    RdosEngineJobCache getOne(@Param("jobId")String jobId);

    int updateStage(@Param("jobId") String jobId, @Param("stage") Integer stage,@Param("nodeAddress") String nodeAddress,
            @Param("jobPriority") Long jobPriority, @Param("groupName") String groupName);

    List<RdosEngineJobCache> listByStage(@Param("startId") Long id, @Param("nodeAddress") String nodeAddress, @Param("stage") Integer stage, @Param("engineType") String engineType, @Param("groupName") String groupName);

    List<RdosEngineJobCache> getByJobIds(@Param("jobIds") List<String> jobIds);

    List<String> listNames(@Param("jobName") String jobName);

    int countByStage(@Param("engineType") String engineType, @Param("groupName") String groupName, @Param("stages") List<Integer> stages, @Param("nodeAddress") String nodeAddress);

    List<String> getAllNodeAddress();

    Long maxPriorityByStage(@Param("engineType") String engineType, @Param("groupName") String groupName,@Param("stage") Integer stage,@Param("nodeAddress") String nodeAddress);

    Long countByJobResource(@Param("engineType") String engineType, @Param("groupName") String groupName, @Param("stage") Integer stage, @Param("nodeAddress") String nodeAddress);

    List<RdosEngineJobCache> listByJobResource(@Param("engineType") String engineType, @Param("groupName") String groupName, @Param("stage") Integer stage, @Param("nodeAddress") String nodeAddress, @Param("start") Integer start, @Param("pageSize") Integer pageSize);

    List<String> getJobResources();

    List<Map<String,Object>> groupByJobResource(@Param("nodeAddress") String nodeAddress);

    Integer deleteByJobIds(@Param("jobIds") List<String>  jobIds);
}
