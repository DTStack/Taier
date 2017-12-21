package com.dtstack.rdos.engine.db.mapper;

import org.apache.ibatis.annotations.Param;

/**
 *
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface RdosStreamTaskCheckpointMapper {

    int insert(@Param("taskId")String taskId, @Param("engineTaskId")String engineTaskId, @Param("checkpoint") String checkpoint);
}
