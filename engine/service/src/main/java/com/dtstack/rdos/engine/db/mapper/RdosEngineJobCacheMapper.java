package com.dtstack.rdos.engine.db.mapper;

import com.dtstack.rdos.engine.db.dataobject.RdosEngineJobCache;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public interface RdosEngineJobCacheMapper {

    void insert(String jobId, String jobInfo);

    void delete(String jobId);

    RdosEngineJobCache getOne(String jobId);
}
