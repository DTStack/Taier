package com.dtstack.engine.dao;

import com.dtstack.engine.api.vo.JobTopErrorVO;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/8/11 4:05 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface ScheduleJobFailedDao {


    List<JobTopErrorVO> listTopError(@Param("appType") Integer appType,
                                     @Param("dtuicTenantId") Long dtuicTenantId,
                                     @Param("projectId") Long projectId,
                                     @Param("timeTo") Timestamp timeTo);
}
