package com.dtstack.rdos.engine.db.mapper;

/**
 *
 * Date: 2017/12/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface RdosStreamTaskCheckpointMapper {

    int insert(String taskId, String engineTaskId, String checkpoint);
}
