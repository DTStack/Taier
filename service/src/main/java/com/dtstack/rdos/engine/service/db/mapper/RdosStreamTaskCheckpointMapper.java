package com.dtstack.rdos.engine.service.db.mapper;

import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
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
               @Param("triggerStart") Timestamp triggerStart,
               @Param("triggerEnd") Timestamp triggerEnd);

    List<RdosStreamTaskCheckpoint> listByTaskIdAndRangeTime(@Param("taskEngineId") String taskEngineId,
                                                                              @Param("triggerStart") Long triggerStart,
                                                                              @Param("triggerEnd") Long triggerEnd);

    RdosStreamTaskCheckpoint getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("taskEngineId") String taskEngineId);

    void deleteByEngineTaskIdAndCheckpointID(@Param("taskEngineId") String taskEngineId, @Param("checkpointId") String checkpointId);

    List<RdosStreamTaskCheckpoint> getByCheckpointIndexAndCount(@Param("startIndex") int startIndex, @Param("count") int count);


}
