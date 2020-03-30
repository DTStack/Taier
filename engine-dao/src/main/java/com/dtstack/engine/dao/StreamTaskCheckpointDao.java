package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.StreamTaskCheckpoint;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface StreamTaskCheckpointDao {

    int insert(@Param("taskId")String taskId, @Param("engineTaskId")String engineTaskId,
               @Param("checkpointId") String checkpointId,
               @Param("checkpointTrigger") Timestamp checkpointTrigger,
               @Param("checkpointSavepath") String checkpointSavepath,
               @Param("checkpointCounts") String checkpointCounts);

    List<StreamTaskCheckpoint> listByTaskIdAndRangeTime(@Param("taskId") String taskEngineId,
                                                        @Param("triggerStart") Long triggerStart,
                                                        @Param("triggerEnd") Long triggerEnd);

    StreamTaskCheckpoint getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("taskEngineId") String taskEngineId);

    void batchDeleteByEngineTaskIdAndCheckpointId(@Param("taskEngineId") String taskEngineId, @Param("checkpointId") String checkpointId);

    List<StreamTaskCheckpoint> getByTaskEngineIdAndCheckpointIndexAndCount(@Param("taskEngineId") String taskEngineId,
                                                                           @Param("startIndex") int startIndex,
                                                                           @Param("count") int count);

    void cleanAllCheckpointByTaskEngineId(@Param("taskEngineId")  String taskEngineId);

    Integer updateCheckpoint(@Param("taskId") String taskId, @Param("checkpoint") String checkpoint);

    StreamTaskCheckpoint getByTaskId(@Param("taskId") String taskId);

    Integer deleteByTaskId(@Param("taskId") String taskId);
}
