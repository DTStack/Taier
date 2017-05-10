package com.dtstack.rdos.engine.db.mapper;

import com.dtstack.rdos.engine.db.dataobject.RdosStreamActionLog;
import org.apache.ibatis.annotations.Param;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface RdosStreamActionLogMapper {

    void updateActionStatus(@Param("actionLogId")Long actionLogId, @Param("status") int status);

    RdosStreamActionLog findActionLogById(@Param("actionLogId")Long actionLogId);


}
