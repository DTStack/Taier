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
               @Param("checkpoint") String checkpoint, @Param("triggerStart") Timestamp triggerStart,
               @Param("triggerEnd") Timestamp triggerEnd);

    List<RdosStreamTaskCheckpoint> listByTaskIdAndRangeTime(@Param("taskId") String taskId, @Param("triggerStart") Long triggerStart, @Param("triggerEnd") Long triggerEnd);

    RdosStreamTaskCheckpoint getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("taskEngineId") String taskEngineId);

    Integer updateCheckpoint(@Param("taskId") String taskId, @Param("checkpoint") String checkpoint);

    RdosStreamTaskCheckpoint getByTaskId(@Param("taskId") String taskId);
}
