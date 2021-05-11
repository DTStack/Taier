package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleJob;
import org.apache.ibatis.annotations.*;

public interface TestScheduleJobDao {
    @Insert({ "INSERT INTO schedule_job\n" +
            "    (gmt_create, gmt_modified, tenant_id, project_id, dtuic_tenant_id, app_type, job_id, job_key, job_name, task_id,\n" +
            "    create_user_id, is_deleted, `type`, is_restart, business_date, cyc_time, dependency_type, flow_job_id,\n" +
            "    status, task_type, max_retry_num, node_address, version_id, compute_type, application_id, exec_start_time, exec_end_time, exec_time,log_info, engine_log,engine_job_id)\n" +
            "   VALUES\n" +
            "   (now(), now(), #{scheduleJob.tenantId}, #{scheduleJob.projectId}, #{scheduleJob.dtuicTenantId}, #{scheduleJob.appType}, #{scheduleJob.jobId}, #{scheduleJob.jobKey}, #{scheduleJob.jobName}, #{scheduleJob.taskId},\n" +
            "   #{scheduleJob.createUserId}, #{scheduleJob.isDeleted}, #{scheduleJob.type}, #{scheduleJob.isRestart}, #{scheduleJob.businessDate}, #{scheduleJob.cycTime}, #{scheduleJob.dependencyType}, #{scheduleJob.flowJobId},\n" +
            "   #{scheduleJob.status}, #{scheduleJob.taskType}, #{scheduleJob.maxRetryNum}, #{scheduleJob.nodeAddress}, #{scheduleJob.versionId}, #{scheduleJob.computeType}, #{scheduleJob.applicationId}, #{scheduleJob.execStartTime}, #{scheduleJob.execEndTime}, #{scheduleJob.execTime}, #{scheduleJob.logInfo}, #{scheduleJob.engineLog},#{scheduleJob.engineJobId})" })
    @Options(useGeneratedKeys=true, keyProperty = "scheduleJob.id", keyColumn = "id")
    void insert(@Param("scheduleJob") ScheduleJob scheduleJob);


    @Insert({ "INSERT INTO schedule_job\n" +
            "    (gmt_create, gmt_modified, tenant_id, project_id, dtuic_tenant_id, app_type, job_id, job_key, job_name, task_id,\n" +
            "    create_user_id, is_deleted, `type`, is_restart, business_date, cyc_time, dependency_type, flow_job_id,\n" +
            "    status, task_type, max_retry_num, node_address, version_id, compute_type, application_id, exec_start_time, exec_end_time, exec_time,log_info, engine_log)\n" +
            "   VALUES\n" +
            "   (#{scheduleJob.gmtCreate}, #{scheduleJob.gmtModified}, #{scheduleJob.tenantId}, #{scheduleJob.projectId}, #{scheduleJob.dtuicTenantId}, #{scheduleJob.appType}, #{scheduleJob.jobId}, #{scheduleJob.jobKey}, #{scheduleJob.jobName}, #{scheduleJob.taskId},\n" +
            "   #{scheduleJob.createUserId}, #{scheduleJob.isDeleted}, #{scheduleJob.type}, #{scheduleJob.isRestart}, #{scheduleJob.businessDate}, #{scheduleJob.cycTime}, #{scheduleJob.dependencyType}, #{scheduleJob.flowJobId},\n" +
            "   #{scheduleJob.status}, #{scheduleJob.taskType}, #{scheduleJob.maxRetryNum}, #{scheduleJob.nodeAddress}, #{scheduleJob.versionId}, #{scheduleJob.computeType}, #{scheduleJob.applicationId}, #{scheduleJob.execStartTime}, #{scheduleJob.execEndTime}, #{scheduleJob.execTime}, #{scheduleJob.logInfo}, #{scheduleJob.engineLog})" })
    @Options(useGeneratedKeys=true, keyProperty = "scheduleJob.id", keyColumn = "id")
    void insertWithCustomGmt(@Param("scheduleJob") ScheduleJob scheduleJob);

    @Select({"select * from schedule_job limit 1"})
    ScheduleJob getOne();


    @Update({"update schedule_job set job_extra_info = #{jobExtraInfo} where job_id = #{jobId}"})
    void updateJobExtraInfo(@Param("jobExtraInfo") String jobExtraInfo,@Param("jobId")String jobId);
}
