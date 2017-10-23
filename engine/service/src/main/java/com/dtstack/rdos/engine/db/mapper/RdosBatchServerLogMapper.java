package com.dtstack.rdos.engine.db.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * Reason:
 * Date: 2017/3/7
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public interface RdosBatchServerLogMapper {

    int insertSvrLog(@Param("jobId") String jobId, @Param("engineTaskId") String engineTaskId,
                     @Param("actionLogId") Long actionLogId, @Param("logInfo") String logInfo);

    void updateEngineLog(@Param("jobId") String jobId, @Param("engineLog") String engineLog);
}
