package com.dtstack.rdos.engine.service.db.mapper;

import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

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
}
