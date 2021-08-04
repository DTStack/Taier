import http from './http'
import offlineReq from './reqOffline';

export default {

    // ===== 运维中心模块 ===== //
    queryOfflineTasks (params: any) {
        return http.post(offlineReq.QUERY_TASKS, params)
    },
    getOfflineTaskLog (params: any) { // 获取离线任务日志
        return http.post(offlineReq.GET_TASK_LOG, params)
    },
    getOfflineTaskPeriods (params: any) { // 转到前后周期
        return http.post(offlineReq.GET_TASK_PERIODS, params)
    },
    getRelatedTasks (params: any) {
        return http.post(offlineReq.GET_WORKFLOW_RELATED_TASKS, params)
    },
    getRelatedJobs (params: any) {
        return http.post(offlineReq.GET_WORKFLOW_RELATED_JOBS, params)
    },
    getFillDataRelatedJobs (params: any) {
        return http.post(offlineReq.GET_WORKFLOW_FILLDATA_RELATED_JOBS, params)
    },
    queryJobs (params: any) {
        return http.post(offlineReq.QUERY_JOBS, params)
    },
    getTaskTypesX (params?: any) {
        return http.post(offlineReq.GET_TASK_TYPESX, params)
    },
    getJobGraph (params?: any) {
        return http.post(offlineReq.GET_JOB_GRAPH, params)
    },
    getJobTopTime (params: any) {
        return http.post(offlineReq.GET_JOB_TOP_TIME, params)
    },
    getJobTopError (params?: any) {
        return http.post(offlineReq.GET_JOB_TOP_ERROR, params)
    },
    patchTaskData (params: any) { // 补数据
        return http.post(offlineReq.PATCH_TASK_DATA, params)
    },
    getTaskChildren (params: any) { // 获取任务子节点
        return http.post(offlineReq.GET_TASK_CHILDREN, params)
    },
    findTaskRuleJob (params: any) { // 获取补数据周期实例hover信息
        return http.post(offlineReq.FIND_TASK_RULE_JOB, params)
    },
    findTaskRuleTask (params: any) { // 获取任务管理hover信息
        return http.post(offlineReq.FIND_TASK_RULE_TASK, params)
    },
    getFillData (params: any) { // 补数据搜索
        return http.post(offlineReq.GET_FILL_DATA, params)
    },
    getFillDataDetail (params: any) { // 补数据详情
        return http.post(offlineReq.GET_FILL_DATA_DETAIL, params)
    },
    stopJob (params: any) { // 停止任务
        return http.post(offlineReq.STOP_JOB, params)
    },
    restartAndResume (params: any) { // 重启并恢复任务
        return http.post(offlineReq.RESTART_AND_RESUME_JOB, params)
    },
    batchStopJob (params: any) { // 批量停止任务
        return http.post(offlineReq.BATCH_STOP_JOBS, params)
    },
    batchStopJobByDate (params: any) { // 按业务日期批量杀任务
        return http.post(offlineReq.BATCH_STOP_JOBS_BY_DATE, params)
    },
    batchRestartAndResume (params: any) { // 重启并恢复任务
        return http.post(offlineReq.BATCH_RESTART_AND_RESUME_JOB, params)
    },
    getJobChildren (params: any) { // 获取任务子Job
        return http.post(offlineReq.GET_JOB_CHILDREN, params)
    },
    getJobParents (params: any) { // 获取任务父Job
        return http.post(offlineReq.GET_JOB_PARENT, params)
    },
    queryJobStatics (params: any) {
        return http.post(offlineReq.QUERY_JOB_STATISTICS, params)
    },
    forzenTask (params: any) {
        return http.post(offlineReq.FROZEN_TASK, params)
    },
    stopFillDataJobs (params: any) {
        return http.post(offlineReq.STOP_FILL_DATA_JOBS, params)
    },
    getRestartJobs (params: any) {
        return http.post(offlineReq.GET_RESTART_JOBS, params);
    },
    getYWAppType (params: any) {
        return http.post(offlineReq.GET_APPTYPE, params)
    },

    getProjectList (params: any) {
        return http.post(offlineReq.GET_PROJECT_LIST, params)
    },

    // 202105运维中心新增负责人接口
    getPersonInCharge (params: any) {
        return http.post(offlineReq.USER_QUERYUSER, params)
    },

    // 存在疑问的接口方法
    getOfflineTaskByID (params: any) {
        return http.post(offlineReq.GET_TASK, params)
    },
    /**
     * 获取工作流任务节点实例的子节点
     */
    getTaskJobWorkflowNodes (params: any) {
        return http.post(offlineReq.GET_TASK_JOB_WORKFLOW_NODES, params)
    },
    /**
     * 获取工作流任务的子节点
     */
    getTaskWorkflowNodes (params: any) {
        return http.post(offlineReq.GET_TASK_WORKFLOW_NODES, params)
    },
    statisticsTaskRunTime (params: any) {
        return http.post(offlineReq.STATISTICS_TASK_RUNTIME, params)
    },
    operaRecordData (params: any) { // 操作记录
        return http.post(offlineReq.OPERA_RECORD_DATA, params)
    }
}
