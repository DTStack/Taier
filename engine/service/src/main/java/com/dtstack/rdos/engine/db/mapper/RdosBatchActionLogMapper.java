package com.dtstack.rdos.engine.db.mapper;

import org.apache.ibatis.annotations.Param;

import com.dtstack.rdos.engine.db.dataobject.RdosBatchActionLog;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosBatchActionLogMapper {

    void updateActionStatus(@Param("actionLogId")Long actionLogId, @Param("status") int status);

    RdosBatchActionLog findActionLogById(@Param("actionLogId")Long actionLogId);


}
