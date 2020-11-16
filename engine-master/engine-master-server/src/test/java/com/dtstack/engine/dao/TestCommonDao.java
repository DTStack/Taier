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
            "delete from schedule_job_graph_trigger;"+
            "delete from lineage_data_set_info;"+
            "delete from lineage_data_source;" +
            "delete from lineage_real_data_source;"+
            "delete from lineage_table_table;"+
            "delete from lineage_table_table_unique_key_ref;"+
            "delete from lineage_column_column;"+
            "delete from lineage_column_column_unique_key_ref"
    )
    void truncate();
}
