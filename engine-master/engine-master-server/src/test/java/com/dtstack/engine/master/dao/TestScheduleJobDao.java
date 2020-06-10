package com.dtstack.engine.master.dao;

import com.dtstack.engine.api.domain.ScheduleJob;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;


public interface TestScheduleJobDao {
    @Insert({ "INSERT INTO schedule_job\n" +
            "    (id, gmt_create, gmt_modified, tenant_id, project_id, dtuic_tenant_id, app_type, job_id, job_key, job_name, task_id,\n" +
            "    create_user_id, is_deleted, `type`, is_restart, business_date, cyc_time, dependency_type, flow_job_id,\n" +
            "    status, task_type, max_retry_num, node_address, version_id, source_type, compute_type)\n" +
            "   VALUES\n" +
            "   (#{id}, now(), now(), #{tenantId}, #{projectId}, #{dtuicTenantId}, #{appType}, #{jobId}, #{jobKey}, #{jobName}, #{taskId},\n" +
            "   #{createUserId}, #{isDeleted}, #{type}, #{isRestart}, #{businessDate}, #{cycTime}, #{dependencyType}, #{flowJobId},\n" +
            "   #{status}, #{taskType}, #{maxRetryNum}, #{nodeAddress}, #{versionId}, #{sourceType}, #{computeType})" })
    void insert(@Param("scheduleJob") ScheduleJob job);

    @Delete({"delete from schedule_job\n" +
            "    where id = #{id}"})
    void deleteById(@Param("id") Long id);
}
