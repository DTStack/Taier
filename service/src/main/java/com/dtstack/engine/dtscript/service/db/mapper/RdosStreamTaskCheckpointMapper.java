package com.dtstack.engine.dtscript.service.db.mapper;

import com.dtstack.engine.dtscript.service.db.dataobject.RdosStreamTaskCheckpoint;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface RdosStreamTaskCheckpointMapper {

    int insert(@Param("taskId")String taskId, @Param("engineTaskId")String engineTaskId,
               @Param("checkpointId") String checkpointId,
               @Param("checkpointTrigger") Timestamp checkpointTrigger,
               @Param("checkpointSavepath") String checkpointSavepath,
               @Param("checkpointCounts") String checkpointCounts);

    List<RdosStreamTaskCheckpoint> listByTaskIdAndRangeTime(@Param("taskId") String taskEngineId,
                                                                              @Param("triggerStart") Long triggerStart,
                                                                              @Param("triggerEnd") Long triggerEnd);

    RdosStreamTaskCheckpoint getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("taskEngineId") String taskEngineId);

    void batchDeleteByEngineTaskIdAndCheckpointID(@Param("taskEngineId") String taskEngineId, @Param("checkpointId") String checkpointId);

    List<RdosStreamTaskCheckpoint> getByTaskEngineIDAndCheckpointIndexAndCount(@Param("taskEngineID") String taskEngineID,
                                                                        @Param("startIndex") int startIndex,
                                                                        @Param("count") int count);

    void cleanAllCheckpointByTaskEngineId(@Param("taskEngineId")  String taskEngineId);

    Integer updateCheckpoint(@Param("taskId") String taskId, @Param("checkpoint") String checkpoint);

    RdosStreamTaskCheckpoint getByTaskId(@Param("taskId") String taskId);

    Integer deleteByTaskId(@Param("taskId") String taskId);
}
