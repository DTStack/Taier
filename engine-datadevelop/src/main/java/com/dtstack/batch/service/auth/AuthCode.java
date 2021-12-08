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

package com.dtstack.batch.service.auth;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/12/5
 */
public interface AuthCode {

    public static String AUTH_EH_CACHE = "BatchAuthSpringCache";

    /**
     * 生产测试项目
     */
    String TEST_PRODUCE_BINDING_PROJECT = "test_produce_binding_project";

    String TEST_PRODUCE_EDIT_SCHEDULE_STATUS = "test_produce_edit_schedule_status";

    String TEST_PRODUCE_EDIT_PACKAGE = "test_produce_edit_package";

    String TEST_PRODUCE_QUERY_PACKAGE = "test_produce_query_package";

    String TEST_PRODUCE_PUBLISH_PACKAGE = "test_produce_publish_package";

    String DATAINTEGRATION_BATCH_QUERY = "batchintegration_batch_query";

    String DATAINTEGRATION_BATCH_EDIT = "batchintegration_batch_edit";

    String DATAINTEGRATION_BATCH_DBSYNC = "batchintegration_batch_dbsync";

    /**
     * 数据开发
     */
    String DATADEVELOP_STREAM_TASKMANAGER_QUERY = "datadevelop_stream_taskmanager_query";

    String DATADEVELOP_BATCH_TASKMANAGER_QUERY = "datadevelop_batch_taskmanager_query";

    String DATADEVELOP_BATCH_TASKMANAGER_EDIT = "datadevelop_batch_taskmanager_edit";

    /**
     * TODO,与离线任务修改重合
     */
    String DATADEVELOP_BATCH_TASKMANAGER_PUBLISH = "datadevelop_batch_taskmanager_publish";

    String DATADEVELOP_BATCH_FUNCTIONMANAGER = "datadevelop_batch_functionmanager";

    String DATADEVELOP_BATCH_RESOURCEMANAGER = "datadevelop_batch_resourcemanager";


    String DATADEVELOP_BATCH_SCRIPTMANAGER_QUERY = "datadevelop_batch_scriptmanager_query";

    String DATADEVELOP_BATCH_SCRIPTMANAGER_EDIT = "datadevelop_batch_scriptmanager_edit";

    /**
     * 数据管理
     */
    String DATAMANAGER_TABLMANAGER_QUERY = "datamanager_tablemanager_query";

    String DATAMANAGER_TABLMANAGER_EDIT = "datamanager_tablemanager_edit";

    String DATAMANAGER_TABLEMANAGER_EDITCHARGE = "datamanager_tablemanager_editcharge";

    String DATAMANAGER_HANDLERECORD = "datamanager_handlerecord";

    String DATAMANAGER_CATALOGUE_EDIT  ="datamanager_catalogue_edit";

    String DATAMANAGER_DIRTYDATA = "datamanager_dirtydata";

    String DATAMANAGER_PERMISSIONMANAGER_QUERY = "datamanager_permissionmanager_query";

    String DATAMANAGER_PERMISSIONMANAGER_APPLY = "datamanager_permissionmanager_apply";

    String DATAMANAGER_PERMISSIONMANAGER_EDIT = "datamanager_permissionmanager_edit";

    /**
     * 运维中心
     */
    String MAINTENANCE_PANDECT_BATCH = "maintenance_pandect_batch";

    String MAINTENANCE_BATCH_QUERY = "maintenance_batch_query";

    String MAINTENANCE_BATCH_TASKOP = "maintenance_batch_taskop";

    /**
     * TODO,与任务查看重合
     */
    String MAINTENANCE_BATCHTASKMANAGER_QUERY = "maintenance_batchtaskmanager_query";

    String MAINTENANCE_BATCHTASKMANAGER_FILLDATA = "maintenance_batchtaskmanager_filldata";

    String MAINTENANCE_ALARM_RECORD_BATCH = "maintenance_alarm_record_batch";

    String MAINTENANCE_ALARM_CUSTOM_BATCH_QUERY = "maintenance_alarm_custom_batch_query";

    String MAINTENANCE_ALARM_CUSTOM_BATCH_EDIT = "maintenance_alarm_custom_batch_edit";

    /**
     * 项目管理
     */
    String PROJECT_EDIT = "project_edit";

    String PROJECT_CONFIGURE_QUERY = "project_configure_query";

    String PROJECT_CONFIGURE_EDIT = "project_configure_edit";

    String PROJECT_MEMBER_QUERY = "project_member_query";

    String PROJECT_MEMBER_EDIT = "project_member_edit";

    String PROJECT_ROLE_QUERY = "project_role_query";

    String PROJECT_ROLE_EDIT = "project_role_edit";

    /**
     * 数据模型
     */
    String DATAMODEL_MANAGER_QUERY = "datamodel_manager_query";

    String DATAMODEL_MANAGER_EDIT = "datamodel_manager_edit";

    String DATAMASK_RULE_EDIT = "datamask_rule_edit";
    String DATAMASK_RULE_QUERY = "datamask_rule_query";

    String DATAMASK_CONFIG_EDIT = "datamask_config_edit";
    String DATAMASK_CONFIG_QUERY = "datamask_config_query";

}
