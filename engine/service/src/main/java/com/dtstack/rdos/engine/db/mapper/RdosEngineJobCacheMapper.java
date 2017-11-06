package com.dtstack.rdos.engine.db.mapper;

import com.dtstack.rdos.engine.db.dataobject.RdosEngineJobCache;
import org.apache.ibatis.annotations.Param;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public interface RdosEngineJobCacheMapper {

    int insert(@Param("jobId")String jobId, @Param("jobInfo")String jobInfo);

    int delete(@Param("jobId")String jobId);

    RdosEngineJobCache getOne(@Param("jobId")String jobId);
}
