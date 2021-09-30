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

import req from './req';
import http from './http';
import offlineReq from './reqOffline';
import { TASK_TYPE } from '../comm/const';

const api = {
    // ========== Task ========== //
    cloneTask(params: any) {
        return http.post(req.CLONE_TASK, params);
    },
    cloneTaskToWorkflow(params: any) {
        return http.post(req.CLONE_TASK_TO_WORKFLOW, params);
    },
    getWorkflowList(params: any) {
        return http.post(req.GET_WORKFLOW_LISTS, params);
    },
    taskVersionScheduleConf(params: any) {
        return http.post(offlineReq.TASK_VERSION_SCHEDULE_CONF, params);
    },
    updateTaskOwner(params: any) {
        return http.post(offlineReq.UPDATE_TASK_OWNER, params);
    },
    convertToHiveColumns(params: any) {
        return http.post(req.CONVERT_TO_HIVE_COLUMNS, params);
    },

    // =========== 以下为离线模块 ==================//
    saveOfflineTask(task: any) {
        return http.post(offlineReq.SAVE_TASK, task);
    },

    convertDataSyncToScriptMode(params: any) {
        return http.post(offlineReq.CONVERT_SYNC_T0_SCRIPT_MODE, params);
    },

    renameTask(task: any) {
        return http.post(offlineReq.RENAME_TASK, task);
    },

    forceUpdateOfflineTask(task: any) {
        return http.post(offlineReq.FORCE_UPDATE_TASK, task);
    },

    getOfflineTaskByID(params: any) {
        return http.post(offlineReq.GET_TASK, params);
    },
    queryOfflineCataTask(params: any) {
        return http.post(offlineReq.QUERY_CATA_TASK, params);
    },
    getOfflineTasksByProject(params: any) {
        return http.post(offlineReq.GET_TASKS_BY_PROJECT_ID, params);
    },
    getOfflineTasksByName(params: any) {
        return http.post(offlineReq.GET_TASKS_BY_NAME, params);
    },
    queryOfflineTasks(params: any) {
        return http.post(offlineReq.QUERY_TASKS, params);
    },
    getOfflineTaskLog(params: any) {
        // 获取离线任务日志
        return http.post(offlineReq.GET_TASK_LOG, params);
    },
    getOfflineTaskPeriods(params: any) {
        // 转到前后周期
        return http.post(offlineReq.GET_TASK_PERIODS, params);
    },
    searchOfflineTask(params: any) {
        return http.post(offlineReq.GLOBAL_SEARCH_TASK, params);
    },
    getCustomParams(params?: any) {
        return http.post(offlineReq.GET_CUSTOM_TASK_PARAMS, params);
    },
    getSyncTemplate(params: any) {
        return http.post(offlineReq.GET_SYNC_SCRIPT_TEMPLATE, params);
    },
    getCreateTargetTable(params: any) {
        return http.post(req.GET_CREATE_TARGET_TABLE, params);
    },
    getRelatedTasks(params: any) {
        return http.post(offlineReq.GET_WORKFLOW_RELATED_TASKS, params);
    },
    getRelatedJobs(params: any) {
        return http.post(offlineReq.GET_WORKFLOW_RELATED_JOBS, params);
    },
    getFillDataRelatedJobs(params: any) {
        return http.post(offlineReq.GET_WORKFLOW_FILLDATA_RELATED_JOBS, params);
    },
    // =========== 脚本模块 ==================//
    saveScript(params: any) {
        return http.post(offlineReq.SAVE_SCRIPT, params);
    },
    forceUpdateOfflineScript(task: any) {
        return http.post(offlineReq.FORCE_UPDATE_SCRIPT, task);
    },
    getScriptById(params: any) {
        return http.post(offlineReq.GET_SCRIPT_BY_ID, params);
    },
    execScript(params: any) {
        return http.post(offlineReq.EXEC_SCRIPT, params);
    },
    stopScript(params: any) {
        // 获取离线任务日志
        return http.post(offlineReq.STOP_SCRIPT, params);
    },
    deleteScript(params: any) {
        return http.post(offlineReq.DELETE_SCRIPT, params);
    },
    getScriptTypes(params?: any) {
        return http.post(offlineReq.GET_SCRIPT_TYPES, params);
    },

    // =========== 离线Job模块 ==================//
    queryJobs(params: any) {
        return http.post(offlineReq.QUERY_JOBS, params);
    },
    publishOfflineTask(params: any) {
        return http.post(offlineReq.PUBLISH_TASK, params);
    },
    getTaskTypes(params?: any) {
        return http.post(offlineReq.GET_TASK_TYPES, params);
    },
    getAnalyDataSourceLists(params?: any) {
        return http.post(offlineReq.GET_ANALY_DTATSOURCE_LISTS, params);
    },
    getJobById(params: any) {
        return http.post(offlineReq.GET_JOB_BY_ID, params);
    },
    getJobGraph(params?: any) {
        return http.post(offlineReq.GET_JOB_GRAPH, params);
    },
    getJobStatistics(params?: any) {
        return http.post(offlineReq.GET_JOB_STATISTICS, params);
    },
    getJobTopTime(params: any) {
        return http.post(offlineReq.GET_JOB_TOP_TIME, params);
    },
    getTableInfoByDataSource(params: any) {
        return http.post(offlineReq.GET_TABLE_INFO_BY_DATASOURCE, params)
    },
    getJobTopError(params?: any) {
        return http.post(offlineReq.GET_JOB_TOP_ERROR, params);
    },
    patchTaskData(params: any) {
        // 补数据
        return http.post(offlineReq.PATCH_TASK_DATA, params);
    },
    operaRecordData(params: any) {
        // 操作记录
        return http.post(offlineReq.OPERA_RECORD_DATA, params);
    },
    getTaskChildren(params: any) {
        // 获取任务子节点
        return http.post(offlineReq.GET_TASK_CHILDREN, params);
    },
    getTaskParents(params: any) {
        // 获取任务父节点
        return http.post(offlineReq.GET_TASK_PARENTS, params);
    },
    getFillData(params: any) {
        // 补数据搜索
        return http.post(offlineReq.GET_FILL_DATA, params);
    },
    getFillDate(params: any) {
        // 补数据日期列表
        return http.post(offlineReq.GET_FILL_DATE, params);
    },
    getFillDataDetail(params: any) {
        // 补数据详情
        return http.post(offlineReq.GET_FILL_DATA_DETAIL, params);
    },
    startJob(params: any) {
        // 启动任务
        return http.post(offlineReq.START_JOB, params);
    },
    stopJob(params: any) {
        // 停止任务
        return http.post(offlineReq.STOP_JOB, params);
    },
    restartAndResume(params: any) {
        // 重启并恢复任务
        return http.post(offlineReq.RESTART_AND_RESUME_JOB, params);
    },
    batchStopJob(params: any) {
        // 批量停止任务
        return http.post(offlineReq.BATCH_STOP_JOBS, params);
    },
    batchStopJobByDate(params: any) {
        // 按业务日期批量杀任务
        return http.post(offlineReq.BATCH_STOP_JOBS_BY_DATE, params);
    },
    batchRestartAndResume(params: any) {
        // 重启并恢复任务
        return http.post(offlineReq.BATCH_RESTART_AND_RESUME_JOB, params);
    },
    getJobChildren(params: any) {
        // 获取任务子Job
        return http.post(offlineReq.GET_JOB_CHILDREN, params);
    },
    getJobParents(params: any) {
        // 获取任务父Job
        return http.post(offlineReq.GET_JOB_PARENT, params);
    },
    /**
     * 获取工作流任务的子节点
     */
    getTaskWorkflowNodes(params: any) {
        return http.post(offlineReq.GET_TASK_WORKFLOW_NODES, params);
    },
    /**
     * 获取工作流任务节点实例的子节点
     */
    getTaskJobWorkflowNodes(params: any) {
        return http.post(offlineReq.GET_TASK_JOB_WORKFLOW_NODES, params);
    },
    execSQLImmediately(params: any) {
        // 立即执行SQL
        return http.post(offlineReq.EXEC_SQL_IMMEDIATELY, params);
    },
    /**
     * sparkSql高级运行模式
     * @param params 执行参数
     */
    execSparkSQLAdvancedMode(params: {
        projectId: string;
        isCheckDDL: number;
        taskVariables: any[];
        sqlList: string[];
        taskId: number;
    }) {
        // 执行 SparkSQL 高级模式
        return http.post(offlineReq.EXEC_SPARK_SQL_ADVANCED_MODE, params);
    },
    stopSQLImmediately(params: any) {
        // 停止执行数据同步
        return http.post(offlineReq.STOP_SQL_IMMEDIATELY, params);
    },
    execDataSyncImmediately(params: any) {
        // 立即执行数据同步
        return http.post(offlineReq.EXEC_DATA_SYNC_IMMEDIATELY, params);
    },
    stopDataSyncImmediately(params: any) {
        // 停止执行SQL
        return http.post(offlineReq.STOP_DATA_SYNC_IMMEDIATELY, params);
    },
    getIncrementColumns(params: any) {
        // 获取增量字段
        return http.post(offlineReq.GET_INCREMENT_COLUMNS, params);
    },
    checkSyncMode(params: any) {
        // 检测是否满足增量数据同步
        return http.post(offlineReq.CHECK_SYNC_MODE, params);
    },
    getHivePartitions(params: any) {
        // 获取Hive分区
        return http.post(offlineReq.CHECK_HIVE_PARTITIONS, params);
    },
    getPartitionType(params: any) {
        return http.post(offlineReq.GET_PARTITION_TYPE, params);
    },
    /**
     * - 查询数据同步任务，SQL 执行结果
     * - 需要补充增量同步
     * @param {Object} params 请求参数
     * @param {Number} taskType 任务类型
     */
    selectExecResultData(params: any, taskType: any) {
        //
        const url =
            taskType && taskType === TASK_TYPE.SYNC
                ? offlineReq.SELECT_DATA_SYNC_RESULT
                : offlineReq.SELECT_SQL_RESULT_DATA;
        return http.post(url, params);
    },
    checkIsLoop(prams: any) {
        return http.post(offlineReq.CHECK_IS_LOOP, prams);
    },
    getJobRuntimeInfo(prams: any) {
        return http.post(offlineReq.GET_JOB_RUNTIME_INFO, prams);
    },
    queryJobStatics(params: any) {
        return http.post(offlineReq.QUERY_JOB_STATISTICS, params);
    },
    queryJobSubNodes(params: any) {
        return http.post(offlineReq.QUERY_JOB_SUB_NODES, params);
    },
    forzenTask(params: any) {
        return http.post(offlineReq.FROZEN_TASK, params);
    },
    statisticsTaskRunTime(params: any) {
        return http.post(offlineReq.STATISTICS_TASK_RUNTIME, params);
    },
    stopFillDataJobs(params: any) {
        return http.post(offlineReq.STOP_FILL_DATA_JOBS, params);
    },
    getRestartJobs(params: any) {
        return http.post(offlineReq.GET_RESTART_JOBS, params);
    },

    // =========== 离线catalogue目录模块 ==================//
    getOfflineCatalogue(params: any) {
        return http.post(offlineReq.GET_OFFLINE_CATALOGUE, params);
    },
    addOfflineCatalogue(params: any) {
        return http.post(offlineReq.ADD_OFFLINE_CATALOGUE, params);
    },
    editOfflineCatalogue(params: any) {
        return http.post(offlineReq.EDIT_OFFLINE_CATALOGUE, params);
    },
    locateCataPosition(params: any) {
        return http.post(offlineReq.GET_OFFLINE_CATALOGUE_BY_LOCATION, params);
    },

    addOfflineResource(params: any) {
        return http.postAsFormData(offlineReq.ADD_OFFLINE_RESOURCE, params);
    },
    replaceOfflineResource(params: any) {
        return http.postAsFormData(offlineReq.REPLACE_OFFLINE_RESOURCE, params);
    },
    addOfflineTask(params: any) {
        return http.post(offlineReq.ADD_OFFLINE_TASK, params);
    },
    getOfflineTaskDetail(params: any) {
        return http.post(offlineReq.GET_OFFLINE_TASK, params);
    },
    getOfflineTaskByName(params: any) {
        return http.post(offlineReq.GET_OFFLINE_TASK_BY_NAME, params);
    },
    getOfflineDataSource(params?: any) {
        return http.post(offlineReq.GET_OFFLINE_DATASOURCE, params);
    },
    getOfflineTableList(params: any) {
        return http.post(offlineReq.GET_OFFLINE_TABLELIST, params);
    },
    getOfflineTableListPage(params: any) {
        return http.post(offlineReq.GET_OFFLINE_TABLELISTPAGE, params);
    },
    getAllSchemas(params: any) {
        return http.post(offlineReq.GET_OFFLINE_ALLSCHEMAS, params);
    },
    getOfflineCubeKylinInfo(params: any) {
        return http.post(offlineReq.GET_OFFLINE_CUBEKYLININFO, params);
    },
    getOfflineTableColumn(params: {
        sourceId: string;
        schema?: any;
        tableName: string;
        isIncludePart?: boolean;
    }) {
        return http.post(offlineReq.GET_OFFLINE_TABLECOLUMN, params);
    },
    isNativeHive(params: any) {
        return http.post(offlineReq.IS_NATIVE_HIVE, params);
    },
    getOfflineColumnForSyncopate(params: any) {
        return http.post(offlineReq.GET_OFFLINE_COLUMNFORSYNCOPATE, params);
    },
    getOfflineJobData(params: any) {
        return http.post(offlineReq.GET_OFFLINE_JOBDATA, params);
    },
    saveOfflineJobData(params: any) {
        return http.post(offlineReq.SAVE_OFFLINE_JOBDATA, params);
    },
    addOfflineFunction(params: any) {
        return http.post(offlineReq.ADD_OFFLINE_FUNCTION, params);
    },
    addOfflineProcedure(params: any) {
        return http.post(offlineReq.ADD_OFFLINE_PROCEDURE, params);
    },
    getschemaName(params: any) {
        return http.post(offlineReq.GET_SCHEMA_NAME, params);
    },
    getTableListByName(params: any) {
        return http.post(req.GET_TABLE_LIST_BY_NAME, params);
    },
    getRecommentTask(params: any) {
        return http.post(req.GET_RECOMMEND_TASK, params);
    },
    getColumnsOfTable(params: any) {
        return http.post(req.GET_COLUMNS_OF_TABLE, params);
    },
    getAllFunction(params: any) {
        return http.post(req.GET_ALL_FUNCTION_NAME, params);
    },

    // =========== 离线文件操作 ==================//
    delOfflineTask(params: any) {
        return http.post(offlineReq.DEL_OFFLINE_TASK, params);
    },
    delOfflineFolder(params: any) {
        return http.post(offlineReq.DEL_OFFLINE_FOLDER, params);
    },
    delOfflineRes(params: any) {
        return http.post(offlineReq.DEL_OFFLINE_RES, params);
    },
    delOfflineFn(params: any) {
        return http.post(offlineReq.DEL_OFFLINE_FN, params);
    },
    delOfflineProd(params: any) {
        return http.post(offlineReq.DEL_OFFLINE_PROD, params);
    },
    moveOfflineFn(params: any) {
        return http.post(offlineReq.MOVE_OFFLINE_FN, params);
    },
    getOfflineFn(params: any) {
        return http.post(offlineReq.GET_FN_DETAIL, params);
    },
    getOfflineRes(params: any) {
        return http.post(offlineReq.GET_RES_DETAIL, params);
    },
    getDataPreview(params: any) {
        return http.post(offlineReq.DATA_PREVIEW, params);
    },
    getHBaseColumnFamily(params: any) {
        return http.post(offlineReq.GET_HBASE_COLUMN_FAMILY, params);
    },
    //= =============== 离线合并接口 ===============/
    linkSource(params: any, type = 'offline') {
        if (type === 'offline') {
            return http.post(offlineReq.LINK_SOURCE, params);
        }
    },
    
    // =========== 组件模块 ==================//
    saveComponent(params: any) {
        return http.post(offlineReq.SAVE_COMPONENT, params);
    },
    updateComponentOwner(params: any) {
        return http.post(offlineReq.CHANGE_COMPONENT_OWNER_USER, params);
    },
    cloneComponent(params: any) {
        return http.post(offlineReq.CLONE_COMPONENT, params);
    },
    deleteComponent(params: any) {
        return http.post(offlineReq.DELETE_COMPONENT_BY_ID, params);
    },
    getComponentById(params: any) {
        return http.post(offlineReq.GET_COMPONENT_BY_ID, params);
    },
    getComponentByVersionId(params: any) {
        return http.post(offlineReq.GET_COMPONENT_BY_VERSIONID, params);
    },

    publishComponent(params: any) {
        return http.post(offlineReq.PUBLISH_COMPONENT, params);
    },
    execComponent(params: any) {
        return http.post(offlineReq.EXECUTE_COMPONENT, params);
    },
    getComponentTypes(params?: any) {
        return http.post(offlineReq.GET_SUPPORT_COMPONENT_TYPE, params);
    },
    pageQueryByProjectIdInComponent(params: any) {
        return http.post(offlineReq.PAGE_QUERY_BY_PROJECT, params);
    },
    selectStatus(params: any) {
        // 非数据同步接口轮训状态
        return http.post(offlineReq.SELECT_SQL_STATUS, params);
    },
    selectRunLog(params: any) {
        // 非数据同步接口获取日志
        return http.post(offlineReq.SELECT_SQL_LOG, params);
    },
    selectExecResultDataSync(params: any) {
        // 数据同步接口获取结果表
        return http.post(offlineReq.SELECT_DATA_SYNC_RESULT, params);
    },
    ftpRegexPre(params: any) {
        return http.post(offlineReq.GET_OFFLINE_FTP_REG, params);
    },
    /**
     * - 查询数据同步任务，SQL 执行结果
     * - 需要补充增量同步
     * @param {Object} params 请求参数
     * @param {Number} taskType 任务类型
     */
    getDataSourceVersion(params: any) {
        return http.post(offlineReq.GET_DATA_SOURCE_VERSION, params);
    },
    // =========== 离线文件操作 ==================//
    getChildTasks(params: any) {
        return http.post(offlineReq.GET_CHILD_TASKS, params);
    },
};
export default api;
