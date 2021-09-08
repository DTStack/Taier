package com.dtstack.engine.dao;


import com.dtstack.engine.domain.ScheduleFillDataJob;
import com.dtstack.engine.dto.ScheduleJobDTO;
import com.dtstack.engine.common.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleFillDataJobDao {

    ScheduleFillDataJob getByJobName(@Param("jobName") String jobName, @Param("projectId") Long projectId);

    Integer insert(ScheduleFillDataJob fillDataJob);

    List<ScheduleFillDataJob> listFillJob(@Param("nameList") List<String> nameList, @Param("projectId") long projectId);

    List<ScheduleFillDataJob> listFillJobByPageQuery( PageQuery<ScheduleJobDTO> pageQuery);

    List<ScheduleFillDataJob> getFillJobList(@Param("fillIdList") List<Long> fillIdList, @Param("projectId") Long projectId, @Param("tenantId") Long tenantId,@Param("dtuicTenantId") Long dtuicTenantId);

}
