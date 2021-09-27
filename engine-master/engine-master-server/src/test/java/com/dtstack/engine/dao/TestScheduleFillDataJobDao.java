package com.dtstack.engine.dao;

import com.dtstack.engine.domain.ScheduleFillDataJob;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestScheduleFillDataJobDao
 * @Description TODO
 * @Date 2020/11/26 17:04
 * @Created chener@dtstack.com
 */
public interface TestScheduleFillDataJobDao {

    @Insert({"INSERT INTO schedule_fill_data_job(job_name,run_day,from_day,to_day,create_user_id,dtuic_tenant_id,tenant_id,project_id,app_type)VALUES(#{scheduleFillDataJob.jobName},#{scheduleFillDataJob.runDay},#{scheduleFillDataJob.fromDay},#{scheduleFillDataJob.toDay},#{scheduleFillDataJob.createUserId},#{scheduleFillDataJob.dtuicTenantId},#{scheduleFillDataJob.tenantId},#{scheduleFillDataJob.projectId},#{scheduleFillDataJob.appType})"})
    @Options(useGeneratedKeys=true, keyProperty = "scheduleFillDataJob.id", keyColumn = "id")
    Integer insert(@Param("scheduleFillDataJob") ScheduleFillDataJob scheduleFillDataJob);

}
