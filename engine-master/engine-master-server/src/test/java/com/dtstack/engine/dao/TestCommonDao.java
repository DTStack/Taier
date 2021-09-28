/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            "delete from alert_record;"+
            "delete from alert_content;"+
            "delete from alert_channel;"+
            "delete from console_tenant_resource;"+
            "delete from schedule_job_graph_trigger;"+
            "delete from lineage_data_set_info;"+
            "delete from lineage_table_table;"+
            "delete from lineage_table_table_unique_key_ref;"+
            "delete from lineage_column_column;"+
            "delete from lineage_column_column_unique_key_ref;"+
            "delete from console_component_config;" +
            "delete from schedule_engine_project;" +
            "delete from console_tenant_resource;")
    void truncate();
}
