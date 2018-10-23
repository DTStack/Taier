import localDb from 'utils/localDb'
import utils from 'utils'

import req from './req'
import http from './http'
import stremReq from './reqStrem';
import {publishType} from "../comm/const";

/* eslint-disable */
const UIC_URL_TARGET = APP_CONF.UIC_URL || ''
const UIC_DOMAIN_URL = APP_CONF.UIC_DOMAIN || ''

export default {

    streamSqlFormat(params) {
        return http.post(req.SQL_FORMAT, params)
    },

    unlockFile(params) {// 解锁文件
        return http.post(req.UNLOCK_FILE, params)
    },

    // ========== User ========== //
    logout() { // 注销退出
        http.post(req.APP_LOGOUT).then(res => {
            if (res.code === 1) {
                this.openLogin();
            }
        })
    },

    openLogin() {
        localDb.clear()
        utils.deleteCookie('dt_user_id', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_token', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_tenant_id', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_tenant_name', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_username', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_is_tenant_admin', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_is_tenant_creator', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('stream_project_id', UIC_DOMAIN_URL, '/')
        window.location.href = `${UIC_URL_TARGET}/#/login`
    },

    addRoleUser(user) {
        return http.post(req.ADD_ROLE_USRE, user)
    },
    getLoginedUser() {
        return http.post(req.GET_USER_BY_ID)
    },
    updateUserRole(user) {
        return http.post(req.UPDATE_USER_ROLE, user)
    },
    getNotProjectUsers(params) {
        return http.post(req.GET_NOT_PROJECT_USERS, params)
    },

    searchUICUsers(params) {
        return http.post(req.SEARCH_UIC_USERS, params)
    },

    // ========== Project ========== //
    queryProjects(params) {
        return http.post(req.QUERY_PROJECT_LIST, params)
    },
    getProjects(params) { // 获取项目
        return http.post(req.GET_PROJECT_LIST, params)
    },
    getAllProjects(params) {// 获取所有项目
        return http.post(req.GET_ALL_PROJECTS, params);
    },
    removeProjectUser(params) {
        return http.post(req.REMOVE_USER_FROM_PROJECT, params)
    },
    addProjectUser(params) {
        return http.post(req.ADD_PROJECT_USER, params)
    },
    getUserProjects(params) {
        return http.post(req.GET_USRE_PROJECTS, params)
    },
    getProjectUsers(params) {
        return http.post(req.GET_PROJECT_USERS, params)
    },
    getProjectByID(params) {
        return http.post(req.GET_PROJECT_BY_ID, params)
    },
    upateProjectInfo(params) {
        return http.post(req.UPDATE_PROJECT_INFO, params)
    },
    createProject(params) {
        return http.post(req.CREATE_PROJECT, params)
    },
    getProjectInfo(params) {
        return http.post(req.GET_PROJECT_INFO, params)
    },
    getProjectListInfo(params) {
        return http.post(req.GET_PROJECT_LIST_INFO, params)
    },
    setSticky(params) {
        return http.post(req.SET_STICKY, params)
    },
    deleteProject(params) {
        return http.post(req.DELETE_PROJECT, params)
    },
    updateProjectSchedule(params) {
        return http.post(req.UPDATE_PROJECT_SCHEDULE, params)
    },
    bindProductionProject(params) {
        return http.post(req.BIND_PRODUCTION_PROJECT, params)
    },
    getBindingProjectList(params) {
        return http.post(req.GET_COULD_BINDING_PROJECT_LIST, params)
    },
    // ========== Role ========== //
    getRoleList(params) {
        return http.post(req.GET_ROLE_LIST, params)
    },
    updateRole(params) {
        return http.post(req.UPDATE_ROLE, params)
    },
    deleteRole(params) {
        return http.post(req.DELETE_ROLE, params)
    },
    getRoleTree(params) {
        return http.post(req.GET_ROLE_TREE, params)
    },
    getRoleInfo(params) {
        return http.post(req.GET_ROLE_INFO, params)
    },

    // ========== Task ========== //
    saveTask(task) {
        return http.post(req.SAVE_TASK, task)
    },
    forceUpdateTask(task) {
        return http.post(req.FORCE_UPDATE_TASK, task)
    },
    startTask(params) {
        return http.post(req.START_TASK, params)
    },
    stopTask(params) {
        return http.post(req.STOP_TASK, params)
    },
    updateTaskRes(params) {
        return http.post(req.UPDATE_TASK_RES, params)
    },
    getTasks(params) {
        return http.post(req.GET_TASK_LIST, params)
    },
    getTask(params) {
        return http.post(req.GET_TASK, params)
    },
    getTasksByStatus(params) {
        return http.post(req.GET_TASKS_BY_STATUS, params)
    },
    searchTask(params) {
        return http.post(req.SEARCH_TASKS_BY_NAME, params)
    },
    sortTask(params) {
        return http.post(req.SORT_TASKS, params)
    },
    deleteTask(params) {
        return http.post(req.DELETE_TASK, params)
    },
    taskStatistics() {
        return http.post(req.TASK_STATISTICS)
    },
    getTaskLogs(params) {
        return http.post(req.GET_TASK_LOG, params)
    },
    searchRealtimeTask(params) {
        return http.post(req.GLOBAL_SEARCH_TASK, params)
    },
    getRealtimeTaskTypes(params) {
        return http.post(req.GET_TASK_TYPES, params)
    },
    getCheckPointRange(params) {
        return http.post(req.GET_CHECK_POINT_RANGE, params)
    },
    getCheckPoints(params) {
        return http.post(req.GET_CHECK_POINTS, params)
    },
    publishRealtimeTask(params) {
        return http.post(req.PUBLISH_REALTIME_TASK, params)
    },
    getTypeOriginData(params) {
        return http.post(req.GET_TYPE_ORIGIN_DATA, params)
    },
    getTopicType(params) {
        return http.post(req.GET_TOPIC_TYPE, params)
    },
    getStremTableType(params) {
        return http.post(req.GET_STREM_TABLE_TYPE, params)
    },
    getRealtimeJobData(params) {
        return http.post(stremReq.GET_REALTIME_JOBDATA, params)
    },
    getRealtimeCollectionTemplate(params) {
        return http.post(stremReq.GET_SYNC_SCRIPT_TEMPLATE, params)
    },
    getTaskMetrics(params){
        return http.post(req.GET_TASK_METRICS,params)
    },
    // ========== CATALOGUE ========== //
    getCatalogues(params) {
        return http.post(req.GET_CATALOGUE, params)
    },
    updateCatalogue(params) {
        return http.post(req.UPDATE_CATALOGUE, params)
    },
    addCatalogue(params) {
        return http.post(req.ADD_CATALOGUE, params)
    },
    deleteCatalogue(params) {
        return http.post(req.DELETE_CATALOGUE, params)
    },
    locateStreamCataPosition(params) {
        return http.post(req.GET_STREAM_CATALOGUE_BY_LOCATION, params)
    },

    getFunc(params) {
        return http.post(req.GET_FUNC, params)
    },
    createFunc(params) {
        return http.post(req.CREATE_FUNC, params)
    },
    delFunc(params) {
        return http.post(req.DELETE_FUNC, params)
    },
    moveFunc(params) {
        return http.post(req.MOVE_FUNC, params)
    },
    getSysFunc(params) {
        return http.post(req.GET_SYS_FUNC, params)
    },

    // ========== Resource ========== //
    uploadRes(res) {
        return http.postAsFormData(req.UPLOAD_RES, res)
    },
    getResList(params) {
        return http.post(req.GET_RES_LIST, params)
    },
    deleteRes(params) {
        return http.post(req.DELETE_RES, params)
    },
    getRes(params) {
        return http.post(req.GET_RES_BY_ID, params)
    },
    renameRes(params) {
        return http.post(req.RENAME_RES, params)
    },

    // ========== Alarm ========== //
    getAlarmList(params) {
        return http.post(req.GET_ALARM_LIST, params)
    },
    addAlarm(params) {
        return http.post(req.ADD_ALARM, params)
    },
    updateAlarm(params) {
        return http.post(req.UPDATE_ALARM, params)
    },
    closeAlarm(params) {
        return http.post(req.CLOSE_ALARM, params)
    },
    openAlarm(params) {
        return http.post(req.OEPN_ALARM, params)
    },
    deleteAlarm(params) {
        return http.post(req.DELETE_ALARM, params)
    },
    getAlarmRecords(params) {
        return http.post(req.GET_ALARM_RECORDS, params)
    },
    getAlarmStatistics(params) {
        return http.post(req.ALARM_STATISTICS, params)
    },
    getCreateTargetTable(params) {
        return http.post(req.GET_CREATE_TARGET_TABLE, params)
    },
    getTableListByName(params) {
        return http.post(req.GET_TABLE_LIST_BY_NAME, params)
    },
    getRecommentTask(params) {
        return http.post(req.GET_RECOMMEND_TASK, params)
    },
    getColumnsOfTable(params) {
        return http.post(req.GET_COLUMNS_OF_TABLE, params)
    },
    getAllFunction(params) {
        return http.post(req.GET_ALL_FUNCTION_NAME, params)
    },

    getTaskOfStreamSource(params) {
        return http.post(stremReq.GET_TASK_LIST_OF_STREAM_SOURCE, params)
    },
    getProjectUsersData(params) {
        return http.post(req.GET_PROJECT_USERS, params)
    },
    getCheckPointList(params){
        return http.post(req.GET_CHECKPOINT_LIST, params)
    },
    getCheckPointOverview(params){
        return http.post(req.GET_CHECKPOINT_OVERVIEW, params)
    },
    getDelayList(params){
        return http.post(req.GET_DATA_DELAY, params)
    },
    getDelayDetail(params){
        return http.post(req.GET_DATA_DELAY_DETAIL, params)
    },
    //================实时数据源 ===============/
    streamSaveDataSource(params) {
        return http.post(stremReq.STREAM_SAVE_DATA_SOURCE, params)
    },
    streamTestDataSourceConnection(params) {
        return http.post(stremReq.STREAM_TEST_DATA_SOURCE_CONNECTION, params)
    },
    streamDeleteDataSource(params) {
        return http.post(stremReq.STREAM_DELETE_DATA_SOURCE, params)
    },
    getStreamTablelist(params) {
        return http.post(stremReq.GET_STREAM_TABLELIST, params)
    },
    getStreamDataSourceList(params){
        return http.post(stremReq.GET_STREAM_DATASOURCE_LIST,params)
    },
    streamQueryDataSource(params) {
        return http.post(stremReq.STREAM_QUERY_DATA_SOURCE, params)
    },
    getStreamDataSource(params) {
        return http.post(stremReq.GET_STREAM_DATA_SOURCE, params)
    },
    getStreamTableColumn(params) {
        return http.post(stremReq.GET_STREAM_TABLECOLUMN, params)
    },
    //================ 实时离线合并接口 ===============/
    linkSource(params,type="offline") {
        if(type=="offline"){
            return http.post(offlineReq.LINK_SOURCE, params)
        }else{
            return http.post(stremReq.LINK_SOURCE, params)
        }
    },
    getLinkSourceList(params,type="offline") {
        if(type=="offline"){
            return http.post(req.GET_OFFLINE_LINK_SOURCE, params)
        }else{
            return http.post(req.GET_REALTIME_LINK_SOURCE, params)
        }
    },
}
