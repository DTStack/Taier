package com.dtstack.engine.dao;

import com.dtstack.engine.domain.ScheduleJobFailed;
import com.dtstack.engine.domain.po.JobTopErrorPO;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/8/11 4:05 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobFailedDao {


    List<JobTopErrorPO> listTopError(@Param("appType") Integer appType,
                                     @Param("dtuicTenantId") Long dtuicTenantId,
                                     @Param("projectId") Long projectId,
                                     @Param("timeTo") Timestamp timeTo);

    Integer insertBatch(@Param("scheduleJobFaileds") List<ScheduleJobFailed> scheduleJobFaileds);

    Integer deleteByGmtCreate(@Param("appType") Integer appType,
                              @Param("uicTenantId") Long uicTenantId,
                              @Param("projectId") Long projectId,
                              @Param("toDate") Date toDate);
}
