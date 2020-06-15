package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJob;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

public interface TestScheduleJobDao {
    @Insert({ "REPLACE INTO schedule_job\n" +
            "    (id, gmt_create, gmt_modified, tenant_id, project_id, dtuic_tenant_id, app_type, job_id, job_key, job_name, task_id,\n" +
            "    create_user_id, is_deleted, `type`, is_restart, business_date, cyc_time, dependency_type, flow_job_id,\n" +
            "    status, task_type, max_retry_num, node_address, version_id, source_type, compute_type, exec_start_time, exec_end_time, log_info, engine_log)\n" +
            "   VALUES\n" +
            "   (#{scheduleJob.id}, now(), now(), #{scheduleJob.tenantId}, #{scheduleJob.projectId}, #{scheduleJob.dtuicTenantId}, #{scheduleJob.appType}, #{scheduleJob.jobId}, #{scheduleJob.jobKey}, #{scheduleJob.jobName}, #{scheduleJob.taskId},\n" +
            "   #{scheduleJob.createUserId}, #{scheduleJob.isDeleted}, #{scheduleJob.type}, #{scheduleJob.isRestart}, #{scheduleJob.businessDate}, #{scheduleJob.cycTime}, #{scheduleJob.dependencyType}, #{scheduleJob.flowJobId},\n" +
            "   #{scheduleJob.status}, #{scheduleJob.taskType}, #{scheduleJob.maxRetryNum}, #{scheduleJob.nodeAddress}, #{scheduleJob.versionId}, #{scheduleJob.sourceType}, #{scheduleJob.computeType}, #{scheduleJob.execStartTime}, #{scheduleJob.execEndTime}, #{scheduleJob.logInfo}, #{scheduleJob.engineLog})" })
    void insert(@Param("scheduleJob") ScheduleJob scheduleJob);

    @Delete({"delete from schedule_job\n" +
            "    where id = #{id}"})
    void deleteById(@Param("id") Long id);
}
