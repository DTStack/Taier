package com.dtstack.rdos.engine.db.mapper;

import com.dtstack.rdos.engine.db.dataobject.RdosEngineJobCache;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface RdosEngineJobCacheMapper {

    int insert(@Param("jobId")String jobId,  @Param("engineType") String engineType,
               @Param("computeType") Integer computeType, @Param("stage") int stage,
               @Param("jobInfo")String jobInfo);

    int delete(@Param("jobId")String jobId);

    RdosEngineJobCache getOne(@Param("jobId")String jobId);

    int updateStage(@Param("jobId") String jobId, @Param("stage") Integer stage);

    List<RdosEngineJobCache> listByStage(@Param("stage") int stage, @Param("engineType") String engineType);
}
