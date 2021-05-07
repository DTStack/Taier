package com.dtstack.engine.dao;

import org.apache.ibatis.annotations.Update;

public interface TestCommonDao {
    @Update("delete from schedule_plugin_info;" +
            "delete from schedule_engine_job_checkpoint;" +
            "delete from schedule_engine_job_cache;" +
            "delete from schedule_plugin_job_info;" +
            "delete from schedule_engine_unique_sign;" +
            "delete from schedule_engine_job_retry;" +
            "delete from schedule_engine_job_stop_record;" +
            "delete from schedule_node_machine;" +
            "delete from console_cluster;" +
            "delete from console_engine;" +
            "delete from console_component;" +
            "delete from console_dtuic_tenant;" +
            "delete from console_engine_tenant;" +
            "delete from console_queue;" +
            "delete from console_kerberos;" +
            "delete from console_user;" +
            "delete from console_account;" +
            "delete from console_account_tenant;" +
            "delete from schedule_task_shade;" +
            "delete from schedule_task_task_shade;" +
            "delete from schedule_job;" +
            "delete from schedule_job_job;" +
            "delete from schedule_fill_data_job;" +
            "delete from schedule_job_graph_trigger;" +
            "delete from console_component_config;" +
            "delete from console_tenant_resource;")
    void truncate();
}
