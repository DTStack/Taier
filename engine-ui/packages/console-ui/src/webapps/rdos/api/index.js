import localDb from 'utils/localDb'
import utils from 'utils'

import req from './req'
import http from './http'
import offlineReq from './reqOffline';
import stremReq from './reqStrem';
import dataManageReq from './reqDataManage';
import { publishType, TASK_TYPE } from '../comm/const';

/* eslint-disable-next-line */
const UIC_URL_TARGET = APP_CONF.UIC_URL || '';
/* eslint-disable-next-line */
const UIC_DOMAIN_URL = APP_CONF.UIC_DOMAIN || '';

export default {

    sqlFormat (params) { // SQL格式化
        return http.post(offlineReq.SQL_FORMAT, params)
    },

    streamSqlFormat (params) {
        return http.post(req.SQL_FORMAT, params)
    },

    unlockFile (params) { // 解锁文件
        return http.post(offlineReq.UNLOCK_FILE, params)
    },

    // ========== User ========== //
    logout () { // 注销退出
        http.post(req.APP_LOGOUT).then(res => {
            if (res.code === 1) {
                this.openLogin();
            }
        })
    },

    openLogin () {
        localDb.clear()
        utils.deleteCookie('dt_user_id', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_token', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_tenant_id', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_tenant_name', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_username', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_is_tenant_admin', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_is_tenant_creator', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('project_id', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('project_id', null, '/')
        utils.deleteCookie('stream_project_id', null, '/')
        window.location.href = `${UIC_URL_TARGET}`
    },

    addRoleUser (user) {
        return http.post(req.ADD_ROLE_USRE, user)
    },
    getLoginedUser () {
        return http.post(req.GET_USER_BY_ID)
    },
    updateUserRole (user) {
        return http.post(req.UPDATE_USER_ROLE, user)
    },
    getNotProjectUsers (params) {
        return http.post(req.GET_NOT_PROJECT_USERS, params)
    },

    searchUICUsers (params) {
        return http.post(req.SEARCH_UIC_USERS, params)
    },

    // ========== Project ========== //
    queryProjects (params) {
        return http.post(req.QUERY_PROJECT_LIST, params)
    },
    getProjects (params) { // 获取项目
        return http.post(req.GET_PROJECT_LIST, params)
    },
    getAllProjects (params) { // 获取所有项目
        return http.post(req.GET_ALL_PROJECTS, params);
    },
    removeProjectUser (params) {
        return http.post(req.REMOVE_USER_FROM_PROJECT, params)
    },
    addProjectUser (params) {
        return http.post(req.ADD_PROJECT_USER, params)
    },
    getUserProjects (params) {
        return http.post(req.GET_USRE_PROJECTS, params)
    },
    getProjectUsers (params) {
        return http.post(req.GET_PROJECT_USERS, params)
    },
    getProjectByID (params) {
        return http.post(req.GET_PROJECT_BY_ID, params)
    },
    upateProjectInfo (params) {
        return http.post(req.UPDATE_PROJECT_INFO, params)
    },
    createProject (params) {
        return http.post(req.CREATE_PROJECT, params)
    },
    getProjectInfo (params) {
        return http.post(req.GET_PROJECT_INFO, params)
    },
    getProjectListInfo (params) {
        return http.post(req.GET_PROJECT_LIST_INFO, params)
    },
    setSticky (params) {
        return http.post(req.SET_STICKY, params)
    },
    deleteProject (params) {
        return http.post(req.DELETE_PROJECT, params)
    },
    updateProjectSchedule (params) {
        return http.post(req.UPDATE_PROJECT_SCHEDULE, params)
    },
    updateProjectAllowDownLoad (params) {
        return http.post(req.UPDATE_PROJECT_ALLOW_DOWNLOAD, params)
    },
    bindProductionProject (params) {
        return http.post(req.BIND_PRODUCTION_PROJECT, params)
    },
    getBindingProjectList (params) {
        return http.post(req.GET_COULD_BINDING_PROJECT_LIST, params)
    },
    getTableListFromDataBase (params) {
        return http.post(req.GET_TABLE_LIST_FROM_DATABASE, params)
    },
    getRetainDBList (params) {
        return http.post(req.GET_RETAINDB_LIST, params)
    },
    // ========== Role ========== //
    getRoleList (params) {
        return http.post(req.GET_ROLE_LIST, params)
    },
    updateRole (params) {
        return http.post(req.UPDATE_ROLE, params)
    },
    deleteRole (params) {
        return http.post(req.DELETE_ROLE, params)
    },
    getRoleTree (params) {
        return http.post(req.GET_ROLE_TREE, params)
    },
    getRoleInfo (params) {
        return http.post(req.GET_ROLE_INFO, params)
    },

    // ========== Task ========== //
    saveTask (task) {
        return http.post(req.SAVE_TASK, task)
    },
    forceUpdateTask (task) {
        return http.post(req.FORCE_UPDATE_TASK, task)
    },
    startTask (params) {
        return http.post(req.START_TASK, params)
    },
    stopTask (params) {
        return http.post(req.STOP_TASK, params)
    },
    updateTaskRes (params) {
        return http.post(req.UPDATE_TASK_RES, params)
    },
    getTasks (params) {
        return http.post(req.GET_TASK_LIST, params)
    },
    getTask (params) {
        return http.post(req.GET_TASK, params)
    },
    getTasksByStatus (params) {
        return http.post(req.GET_TASKS_BY_STATUS, params)
    },
    searchTask (params) {
        return http.post(req.SEARCH_TASKS_BY_NAME, params)
    },
    sortTask (params) {
        return http.post(req.SORT_TASKS, params)
    },
    deleteTask (params) {
        return http.post(req.DELETE_TASK, params)
    },
    cloneTask (params) {
        return http.post(req.CLONE_TASK, params)
    },
    cloneTaskToWorkflow (params) {
        return http.post(req.CLONE_TASK_TO_WORKFLOW, params)
    },
    getWorkflowList (params) {
        return http.post(req.GET_WORKFLOW_LISTS, params)
    },
    taskStatistics () {
        return http.post(req.TASK_STATISTICS)
    },
    getTaskLogs (params) {
        return http.post(req.GET_TASK_LOG, params)
    },
    searchRealtimeTask (params) {
        return http.post(req.GLOBAL_SEARCH_TASK, params)
    },
    getRealtimeTaskTypes (params) {
        return http.post(req.GET_TASK_TYPES, params)
    },
    getCheckPointRange (params) {
        return http.post(req.GET_CHECK_POINT_RANGE, params)
    },
    getCheckPoints (params) {
        return http.post(req.GET_CHECK_POINTS, params)
    },
    publishRealtimeTask (params) {
        return http.post(req.PUBLISH_REALTIME_TASK, params)
    },
    taskVersionScheduleConf (params) {
        return http.post(offlineReq.TASK_VERSION_SCHEDULE_CONF, params)
    },
    updateTaskOwner (params) {
        return http.post(offlineReq.UPDATE_TASK_OWNER, params)
    },
    getTypeOriginData (params) {
        return http.post(req.GET_TYPE_ORIGIN_DATA, params)
    },
    getTopicType (params) {
        return http.post(req.GET_TOPIC_TYPE, params)
    },
    getStremTableType (params) {
        return http.post(req.GET_STREM_TABLE_TYPE, params)
    },
    convertToHiveColumns (params) {
        return http.post(req.CONVERT_TO_HIVE_COLUMNS, params)
    },
    // ========== CATALOGUE ========== //
    getCatalogues (params) {
        return http.post(req.GET_CATALOGUE, params)
    },
    updateCatalogue (params) {
        return http.post(req.UPDATE_CATALOGUE, params)
    },
    addCatalogue (params) {
        return http.post(req.ADD_CATALOGUE, params)
    },
    deleteCatalogue (params) {
        return http.post(req.DELETE_CATALOGUE, params)
    },
    locateStreamCataPosition (params) {
        return http.post(req.GET_STREAM_CATALOGUE_BY_LOCATION, params)
    },

    getFunc (params) {
        return http.post(req.GET_FUNC, params)
    },
    createFunc (params) {
        return http.post(req.CREATE_FUNC, params)
    },
    delFunc (params) {
        return http.post(req.DELETE_FUNC, params)
    },
    moveFunc (params) {
        return http.post(req.MOVE_FUNC, params)
    },
    getSysFunc (params) {
        return http.post(req.GET_SYS_FUNC, params)
    },

    // ========== Resource ========== //
    uploadRes (res) {
        return http.postAsFormData(req.UPLOAD_RES, res)
    },
    getResList (params) {
        return http.post(req.GET_RES_LIST, params)
    },
    deleteRes (params) {
        return http.post(req.DELETE_RES, params)
    },
    getRes (params) {
        return http.post(req.GET_RES_BY_ID, params)
    },
    renameRes (params) {
        return http.post(req.RENAME_RES, params)
    },

    // ========== Alarm ========== //
    getAlarmList (params) {
        return http.post(req.GET_ALARM_LIST, params)
    },
    addAlarm (params) {
        return http.post(req.ADD_ALARM, params)
    },
    updateAlarm (params) {
        return http.post(req.UPDATE_ALARM, params)
    },
    closeAlarm (params) {
        return http.post(req.CLOSE_ALARM, params)
    },
    openAlarm (params) {
        return http.post(req.OEPN_ALARM, params)
    },
    deleteAlarm (params) {
        return http.post(req.DELETE_ALARM, params)
    },
    getAlarmRecords (params) {
        return http.post(req.GET_ALARM_RECORDS, params)
    },
    getAlarmStatistics (params) {
        return http.post(req.ALARM_STATISTICS, params)
    },

    // =========== 以下为离线模块 ==================//
    saveOfflineTask (task) {
        return http.post(offlineReq.SAVE_TASK, task)
    },

    convertDataSyncToScriptMode (params) {
        return http.post(offlineReq.CONVERT_SYNC_T0_SCRIPT_MODE, params)
    },

    renameTask (task) {
        return http.post(offlineReq.RENAME_TASK, task)
    },

    forceUpdateOfflineTask (task) {
        return http.post(offlineReq.FORCE_UPDATE_TASK, task)
    },

    getOfflineTaskByID (params) {
        return http.post(offlineReq.GET_TASK, params)
    },
    deleteOfflineTask (params) {
        return http.post(offlineReq.DELETE_TASK, params)
    },
    queryOfflineCataTask (params) {
        return http.post(offlineReq.QUERY_CATA_TASK, params)
    },
    getOfflineTasksByProject (params) {
        return http.post(offlineReq.GET_TASKS_BY_PROJECT_ID, params)
    },
    getOfflineTasksByName (params) {
        return http.post(offlineReq.GET_TASKS_BY_NAME, params)
    },
    queryOfflineTasks (params) {
        return http.post(offlineReq.QUERY_TASKS, params)
    },
    getOfflineTaskLog (params) { // 获取离线任务日志
        return http.post(offlineReq.GET_TASK_LOG, params)
    },
    getOfflineTaskPeriods (params) { // 转到前后周期
        return http.post(offlineReq.GET_TASK_PERIODS, params)
    },
    searchOfflineTask (params) {
        return http.post(offlineReq.GLOBAL_SEARCH_TASK, params)
    },
    getCustomParams (params) {
        return http.post(offlineReq.GET_CUSTOM_TASK_PARAMS, params)
    },
    getSyncTemplate (params) {
        return http.post(offlineReq.GET_SYNC_SCRIPT_TEMPLATE, params)
    },
    getCreateTargetTable (params) {
        return http.post(req.GET_CREATE_TARGET_TABLE, params)
    },
    getRelatedTasks (params) {
        return http.post(offlineReq.GET_WORKFLOW_RELATED_TASKS, params)
    },
    getRelatedJobs (params) {
        return http.post(offlineReq.GET_WORKFLOW_RELATED_JOBS, params)
    },
    getFillDataRelatedJobs (params) {
        return http.post(offlineReq.GET_WORKFLOW_FILLDATA_RELATED_JOBS, params)
    },
    // =========== 脚本模块 ==================//
    saveScript (params) {
        return http.post(offlineReq.SAVE_SCRIPT, params)
    },
    forceUpdateOfflineScript (task) {
        return http.post(offlineReq.FORCE_UPDATE_SCRIPT, task)
    },
    getScriptById (params) {
        return http.post(offlineReq.GET_SCRIPT_BY_ID, params)
    },
    execScript (params) {
        return http.post(offlineReq.EXEC_SCRIPT, params)
    },
    stopScript (params) { // 获取离线任务日志
        return http.post(offlineReq.STOP_SCRIPT, params)
    },
    deleteScript (params) {
        return http.post(offlineReq.DELETE_SCRIPT, params)
    },
    getScriptTypes (params) {
        return http.post(offlineReq.GET_SCRIPT_TYPES, params)
    },

    // =========== 离线Job模块 ==================//
    queryJobs (params) {
        return http.post(offlineReq.QUERY_JOBS, params)
    },
    publishOfflineTask (params) {
        return http.post(offlineReq.PUBLISH_TASK, params)
    },
    getTaskTypes (params) {
        return http.post(offlineReq.GET_TASK_TYPES, params)
    },
    getAnalyDataSourceLists (params) {
        return http.post(offlineReq.GET_ANALY_DTATSOURCE_LISTS, params)
    },
    getJobById (params) {
        return http.post(offlineReq.GET_JOB_BY_ID, params)
    },
    getJobGraph (params) {
        return http.post(offlineReq.GET_JOB_GRAPH, params)
    },
    getJobStatistics (params) {
        return http.post(offlineReq.GET_JOB_STATISTICS, params)
    },
    getJobTopTime (params) {
        return http.post(offlineReq.GET_JOB_TOP_TIME, params)
    },
    getJobTopError (params) {
        return http.post(offlineReq.GET_JOB_TOP_ERROR, params)
    },
    patchTaskData (params) { // 补数据
        return http.post(offlineReq.PATCH_TASK_DATA, params)
    },
    operaRecordData (params) { // 操作记录
        return http.post(offlineReq.OPERA_RECORD_DATA, params)
    },
    getTaskChildren (params) { // 获取任务子节点
        return http.post(offlineReq.GET_TASK_CHILDREN, params)
    },
    getTaskParents (params) { // 获取任务父节点
        return http.post(offlineReq.GET_TASK_PARENTS, params)
    },
    getFillData (params) { // 补数据搜索
        return http.post(offlineReq.GET_FILL_DATA, params)
    },
    getFillDate (params) { // 补数据日期列表
        return http.post(offlineReq.GET_FILL_DATE, params)
    },
    getFillDataDetail (params) { // 补数据详情
        return http.post(offlineReq.GET_FILL_DATA_DETAIL, params)
    },
    startJob (params) { // 启动任务
        return http.post(offlineReq.START_JOB, params)
    },
    stopJob (params) { // 停止任务
        return http.post(offlineReq.STOP_JOB, params)
    },
    restartAndResume (params) { // 重启并恢复任务
        return http.post(offlineReq.RESTART_AND_RESUME_JOB, params)
    },
    batchStopJob (params) { // 批量停止任务
        return http.post(offlineReq.BATCH_STOP_JOBS, params)
    },
    batchStopJobByDate (params) { // 按业务日期批量杀任务
        return http.post(offlineReq.BATCH_STOP_JOBS_BY_DATE, params)
    },
    batchRestartAndResume (params) { // 重启并恢复任务
        return http.post(offlineReq.BATCH_RESTART_AND_RESUME_JOB, params)
    },
    batchRestartJob (params) { // 批量重启Job
        return http.post(offlineReq.BATCH_RESTART_JOB, params)
    },
    getJobChildren (params) { // 获取任务子Job
        return http.post(offlineReq.GET_JOB_CHILDREN, params)
    },
    getJobParents (params) { // 获取任务父Job
        return http.post(offlineReq.GET_JOB_PARENT, params)
    },
    /**
     * 获取工作流任务的子节点
     */
    getTaskWorkflowNodes (params) {
        return http.post(offlineReq.GET_TASK_WORKFLOW_NODES, params)
    },
    /**
     * 获取工作流任务节点实例的子节点
     */
    getTaskJobWorkflowNodes (params) {
        return http.post(offlineReq.GET_TASK_JOB_WORKFLOW_NODES, params)
    },
    execSQLImmediately (params) { // 立即执行SQL
        return http.post(offlineReq.EXEC_SQL_IMMEDIATELY, params)
    },
    stopSQLImmediately (params) { // 停止执行数据同步
        return http.post(offlineReq.STOP_SQL_IMMEDIATELY, params)
    },
    execDataSyncImmediately (params) { // 立即执行数据同步
        return http.post(offlineReq.EXEC_DATA_SYNC_IMMEDIATELY, params)
    },
    stopDataSyncImmediately (params) { // 停止执行SQL
        return http.post(offlineReq.STOP_DATA_SYNC_IMMEDIATELY, params)
    },
    getIncrementColumns (params) { // 获取增量字段
        return http.post(offlineReq.GET_INCREMENT_COLUMNS, params)
    },
    checkSyncMode (params) { // 检测是否满足增量数据同步
        return http.post(offlineReq.CHECK_SYNC_MODE, params)
    },
    getHivePartitions (params) { // 获取Hive分区
        return http.post(offlineReq.CHECK_HIVE_PARTITIONS, params)
    },
    /**
     * - 查询数据同步任务，SQL 执行结果
     * - 需要补充增量同步
     * @param {Object} params 请求参数
     * @param {Number} taskType 任务类型
     */
    selectExecResultData (params, taskType) { //
        const url = taskType && taskType === TASK_TYPE.SYNC
            ? offlineReq.SELECT_DATA_SYNC_RESULT : offlineReq.SELECT_SQL_RESULT_DATA;
        return http.post(url, params)
    },
    checkIsLoop (prams) {
        return http.post(offlineReq.CHECK_IS_LOOP, prams)
    },
    getJobRuntimeInfo (prams) {
        return http.post(offlineReq.GET_JOB_RUNTIME_INFO, prams)
    },
    queryJobStatics (params) {
        return http.post(offlineReq.QUERY_JOB_STATISTICS, params)
    },
    queryJobSubNodes (params) {
        return http.post(offlineReq.QUERY_JOB_SUB_NODES, params)
    },
    forzenTask (params) {
        return http.post(offlineReq.FROZEN_TASK, params)
    },
    statisticsTaskRunTime (params) {
        return http.post(offlineReq.STATISTICS_TASK_RUNTIME, params)
    },
    stopFillDataJobs (params) {
        return http.post(offlineReq.STOP_FILL_DATA_JOBS, params)
    },
    getRestartJobs (params) {
        return http.post(offlineReq.GET_RESTART_JOBS, params);
    },

    // =========== 离线catalogue目录模块 ==================//
    getOfflineCatalogue (params) {
        return http.post(offlineReq.GET_OFFLINE_CATALOGUE, params)
    },
    addOfflineCatalogue (params) {
        return http.post(offlineReq.ADD_OFFLINE_CATALOGUE, params)
    },
    editOfflineCatalogue (params) {
        return http.post(offlineReq.EDIT_OFFLINE_CATALOGUE, params)
    },
    locateCataPosition (params) {
        return http.post(offlineReq.GET_OFFLINE_CATALOGUE_BY_LOCATION, params)
    },

    addOfflineResource (params) {
        return http.postAsFormData(offlineReq.ADD_OFFLINE_RESOURCE, params)
    },
    addOfflineTask (params) {
        return http.post(offlineReq.ADD_OFFLINE_TASK, params)
    },
    getOfflineTaskDetail (params) {
        return http.post(offlineReq.GET_OFFLINE_TASK, params)
    },
    getOfflineTaskByName (params) {
        return http.post(offlineReq.GET_OFFLINE_TASK_BY_NAME, params)
    },
    getOfflineDataSource (params) {
        return http.post(offlineReq.GET_OFFLINE_DATASOURCE, params)
    },
    getOfflineTableList (params) {
        return http.post(offlineReq.GET_OFFLINE_TABLELIST, params)
    },
    getOfflineTableColumn (params) {
        return http.post(offlineReq.GET_OFFLINE_TABLECOLUMN, params)
    },
    isNativeHive (params) {
        return http.post(offlineReq.IS_NATIVE_HIVE, params);
    },
    getOfflineColumnForSyncopate (params) {
        return http.post(offlineReq.GET_OFFLINE_COLUMNFORSYNCOPATE, params)
    },
    getOfflineJobData (params) {
        return http.post(offlineReq.GET_OFFLINE_JOBDATA, params)
    },
    saveOfflineJobData (params) {
        return http.post(offlineReq.SAVE_OFFLINE_JOBDATA, params)
    },
    addOfflineFunction (params) {
        return http.post(offlineReq.ADD_OFFLINE_FUNCTION, params)
    },
    getTableListByName (params) {
        return http.post(req.GET_TABLE_LIST_BY_NAME, params)
    },
    getRecommentTask (params) {
        return http.post(req.GET_RECOMMEND_TASK, params)
    },
    getColumnsOfTable (params) {
        return http.post(req.GET_COLUMNS_OF_TABLE, params)
    },
    getAllFunction (params) {
        return http.post(req.GET_ALL_FUNCTION_NAME, params)
    },

    // =========== 离线文件操作 ==================//
    delOfflineTask (params) {
        return http.post(offlineReq.DEL_OFFLINE_TASK, params)
    },
    delOfflineFolder (params) {
        return http.post(offlineReq.DEL_OFFLINE_FOLDER, params)
    },
    delOfflineRes (params) {
        return http.post(offlineReq.DEL_OFFLINE_RES, params)
    },
    delOfflineFn (params) {
        return http.post(offlineReq.DEL_OFFLINE_FN, params)
    },
    moveOfflineFn (params) {
        return http.post(offlineReq.MOVE_OFFLINE_FN, params)
    },
    getOfflineFn (params) {
        return http.post(offlineReq.GET_FN_DETAIL, params)
    },
    getOfflineRes (params) {
        return http.post(offlineReq.GET_RES_DETAIL, params)
    },
    getDataPreview (params) {
        return http.post(offlineReq.DATA_PREVIEW, params)
    },
    getHBaseColumnFamily (params) {
        return http.post(offlineReq.GET_HBASE_COLUMN_FAMILY, params)
    },

    // =========== 离线alarm告警模块 ==================//
    getOfflineAlarmList (params) {
        return http.post(offlineReq.GET_ALARM_LIST, params)
    },
    addOfflineAlarm (params) {
        return http.post(offlineReq.ADD_ALARM, params)
    },
    updateOfflineAlarm (params) {
        return http.post(offlineReq.UPDATE_ALARM, params)
    },
    closeOfflineAlarm (params) {
        return http.post(offlineReq.CLOSE_ALARM, params)
    },
    openOfflineAlarm (params) {
        return http.post(offlineReq.OEPN_ALARM, params)
    },
    deleteOfflineAlarm (params) {
        return http.post(offlineReq.DELETE_ALARM, params)
    },
    getOfflineAlarmRecords (params) {
        return http.post(offlineReq.GET_ALARM_RECORDS, params)
    },
    getOfflineAlarmStatistics (params) {
        return http.post(offlineReq.ALARM_STATISTICS, params)
    },

    // =========== datasource数据源模块 ==================//
    addOrUpdateSource (source) {
        return http.post(offlineReq.SAVE_DATA_SOURCE, source)
    },
    deleteDataSource (params) {
        return http.post(offlineReq.DELETE_DATA_SOURCE, params)
    },
    queryDataSource (params) {
        return http.post(offlineReq.QUERY_DATA_SOURCE, params)
    },
    getDataSourceById (params) {
        return http.post(offlineReq.GET_DATA_SOURCE_BY_ID, params)
    },
    testDSConnection (params) {
        return http.post(offlineReq.TEST_DATA_SOURCE_CONNECTION, params)
    },
    getDataSourceTypes (params) {
        return http.post(offlineReq.GET_DATA_SOURCE_TYPES, params)
    },
    checkIsPermission (params) {
        return http.post(offlineReq.CHECK_IS_PERMISSION, params)
    },

    // ============== dataSource 整库同步 ==================
    saveSyncConfig (params) {
        return http.post(offlineReq.SAVE_SYNC_CONFIG, params)
    },
    getSyncHistoryList (params) {
        return http.post(offlineReq.GET_SYNC_HISTORY, params)
    },
    getSyncDetail (params) {
        return http.post(offlineReq.GET_SYNC_DETAIL, params)
    },
    publishSyncTask (params) {
        return http.post(offlineReq.PUBLISH_SYNC_TASK, params)
    },
    checkSyncConfig (params) {
        return http.post(offlineReq.CHECK_SYNC_CONFIG, params)
    },
    getTaskOfOfflineSource (params) {
        return http.post(offlineReq.GET_TASK_LIST_OF_OFFLINE_SOURCE, params)
    },
    checkSyncPermission (params) {
        return http.post(offlineReq.CHECK_SYNC_PERMISSION, params)
    },

    // ============== dataManage 数据管理 ==================
    createTable (params) {
        return http.post(dataManageReq.CREATE_TABLE, params)
    },
    searchTable (params) {
        return http.post(dataManageReq.SEARCH_TABLE, params)
    },
    getTable (params) {
        return http.post(dataManageReq.GET_TABLE, params)
    },
    getTableByName (params) {
        return http.post(dataManageReq.GET_TABLE_BY_NAME, params)
    },
    getTablesByName (params) {
        return http.post(dataManageReq.GET_TABLES_BY_NAME, params)
    },
    previewTable (params) {
        return http.post(dataManageReq.PREVIEW_TABLE, params)
    },
    saveTable (params) {
        return http.post(dataManageReq.SAVE_TABLE, params)
    },
    searchLog (params) {
        return http.post(dataManageReq.SEARCH_LOG, params)
    },
    getProjectUsersData (params) {
        return http.post(req.GET_PROJECT_USERS, params)
    },
    getCreateTableCode (params) {
        return http.post(dataManageReq.GET_CREATE_CODE, params)
    },
    dropTable (params) {
        return http.post(dataManageReq.DROP_TABLE, params)
    },
    createDdlTable (params) {
        return http.post(dataManageReq.DDL_CREATE_TABLE, params)
    },
    uploadTableData (params) {
        return http.post(dataManageReq.UPLOAD_TABLE_DATA, params)
    },
    checkTableExist (params) {
        return http.post(dataManageReq.CHECK_TABLE_EXIST, params)
    },
    checkHdfsLocExist (params) {
        return http.post(dataManageReq.CHECK_HDFSLOC_EXIST, params)
    },
    getTablePartition (params) {
        return http.post(dataManageReq.GET_TABLE_PARTITION, params)
    },
    checkTablePartition (params) {
        return http.post(dataManageReq.CHECK_TABLE_PARTITION, params)
    },
    importLocalData (params) { // 导入本地数据
        return http.postAsFormData(dataManageReq.UPLOAD_TABLE_DATA, params)
    },
    getUploadStatus (params) {
        return http.post(dataManageReq.GET_UPLOAD_STATUS, params)
    },
    getTableRelTree (params) {
        return http.post(dataManageReq.GET_REL_TABLE_TREE, params)
    },
    getRelTableInfo (params) {
        return http.post(dataManageReq.GET_REL_TABLE_INFO, params)
    },
    getParentRelTable (params) {
        return http.post(dataManageReq.GET_PARENT_REL_TABLES, params)
    },
    getChildRelTables (params) {
        return http.post(dataManageReq.GET_CHILD_REL_TABLES, params)
    },
    getRelTableTasks (params) {
        return http.post(dataManageReq.GET_REL_TABLE_TASKS, params)
    },

    // =========== 脏数据 ==================//
    getDirtyDataTrend (params) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TREND, params)
    },
    top30DirtyData (params) {
        return http.post(dataManageReq.TOP30_DIRTY_DATA, params)
    },
    getDirtyDataTables (params) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TABLES, params)
    },
    getPubSyncTask (params) { // 导入本地数据
        return http.post(dataManageReq.GET_PUB_SYNC_TASK, params)
    },
    getDirtyDataTableInfo (params) {
        return http.post(dataManageReq.GET_DIRTY_DATA_TABLE_INFO, params)
    },
    getDirtyDataTableOverview (params) {
        return http.post(dataManageReq.GET_DIRTY_TABLE_OVERVIEW, params)
    },
    countDirtyData (params) {
        return http.post(dataManageReq.COUNT_DIRTY_DATA, params)
    },
    getDirtyDataAnalytics (params) {
        return http.post(dataManageReq.GET_DIRTY_DATA_ANALYTICS, params)
    },

    // =========== 项目统计 ==================//
    countProjectTable (params) {
        return http.post(offlineReq.PROJECT_TABLE_COUNT, params)
    },
    countProjectStore (params) {
        return http.post(offlineReq.PROJECT_STORE_COUNT, params)
    },
    getProjectStoreTop (params) {
        return http.post(offlineReq.PROJECT_STORE_TOP, params)
    },
    getProjectTableStoreTop (params) {
        return http.post(offlineReq.PROJECT_TABLE_STORE_TOP, params)
    },
    getProjectDataOverview (params) {
        return http.post(offlineReq.PROJECT_DATA_OVERVIEW, params)
    },
    //= =============== 实时离线合并接口 ===============/
    linkSource (params, type = 'offline') {
        if (type == 'offline') {
            return http.post(offlineReq.LINK_SOURCE, params)
        } else {
            return http.post(stremReq.LINK_SOURCE, params)
        }
    },
    getLinkSourceList (params, type = 'offline') {
        if (type == 'offline') {
            return http.post(req.GET_OFFLINE_LINK_SOURCE, params)
        } else {
            return http.post(req.GET_REALTIME_LINK_SOURCE, params)
        }
    },
    getRePublishList (params, type = 'offline', listType = publishType.TASK) {
        const urlMap = {
            offline: {
                [publishType.TASK]: req.GET_OFFLINE_TASKS,
                [publishType.FUNCTION]: req.GET_OFFLINE_FUNCTION,
                [publishType.RESOURCE]: req.GET_OFFLINE_RESOURCE,
                [publishType.TABLE]: req.GET_TABLES
            },
            realtime: {
                [publishType.TASK]: req.GET_REALTIME_TASKS,
                [publishType.FUNCTION]: req.GET_REALTIME_FUNCTION,
                [publishType.RESOURCE]: req.GET_REALTIME_RESOURCE
            }
        }
        return http.post(urlMap[type][listType], params)
    },
    getPackageName (params, type = 'offline') {
        if (type == 'offline') {
            return http.post(req.GET_OFFLINE_PACKAGE_NAME, params)
        } else {
            return http.post(req.GET_REALTIME_PACKAGE_NAME, params)
        }
    },
    createPackage (params, type = 'offline') {
        if (type == 'offline') {
            return http.post(req.OFFLINE_CREATE_PACKAGE, params)
        } else {
            return http.post(req.REALTIME_CREATE_PACKAGE, params)
        }
    },
    publishPackage (params, type = 'offline') {
        if (type == 'offline') {
            return http.post(req.PUBLISH_OFFLINE_PACKAGE, params)
        } else {
            return http.post(req.PUBLISH_REALTIME_PACKAGE, params)
        }
    },
    getPackageList (params, type = 'offline') {
        if (type == 'offline') {
            return http.post(req.GET_OFFLINE_PACKAGE_LIST, params)
        } else {
            return http.post(req.GET_REALTIME_PACKAGE_LIST, params)
        }
    },
    deletePackage (params, type = 'offline') {
        if (type == 'offline') {
            return http.post(req.OFFLINE_DELETE_PACKAGE, params)
        } else {
            return http.post(req.REALTIME_DELETE_PACKAGE, params)
        }
    },
    getTaskLinkItems (params, type = 'offline') {
        if (type == 'offline') {
            return http.post(req.GET_OFFLINE_TASK_LINK_ITEMS, params)
        } else {
            return http.post(req.GET_REALTIME_TASK_LINK_ITEMS, params)
        }
    }
}
