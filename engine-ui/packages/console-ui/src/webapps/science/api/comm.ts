import http from './http'
import req from '../consts/reqUrls'

export default {

    formatSQL (params: any) {
        return http.post(req.FORMAT_SQL, params);
    },
    getSQLResultData (params: any) {
        return http.post(req.GET_SQL_RESULT, params);
    },
    getSysParams (params?: any) {
        return http.post(req.GET_SYS_PARAMS, params);
    },
    getAllProject (params?: any) {
        return http.post(req.GET_ALL_PROJECTS, params);
    },
    getProjectList (params: any) {
        return http.post(req.GET_PROJECT_LIST, params);
    },
    createProject (params: any) {
        return http.post(req.CREATE_PROJECT, params);
    },
    updateProject (params: any) {
        return http.post(req.UPDATE_PROJECT, params);
    },
    getTopProject (params?: any) {
        return http.post(req.GET_TOP_PROJECT, params);
    },
    getAllJobStatus (params?: any) {
        return http.post(req.GET_ALL_JOB_STATUS, params);
    },
    getProjectDetail (params: any) {
        return http.post(req.GET_PROJECT_DETAIL, params);
    },
    getProjectJobStatus (params?: any) {
        return http.post(req.GET_JOB_STATUS, params);
    },
    getProjectJobGraph (params: any) {
        return http.post(req.GET_JOB_GRAPH, params);
    },
    queryTask (params: any) {
        return http.post(req.QUERY_TASK, params);
    },
    frozenTask (params: any) {
        return http.post(req.FROZEN_TASK, params);
    },
    listDataSource (params: any) {
        return http.post(req.LIST_DATA_SOURCE, params);
    },
    getDataSourceDetail (params: any) {
        return http.post(req.GET_DATA_SOURCE_DETAIL, params);
    },
    updateDataSource (params: any) {
        return http.post(req.UPDATE_DATA_SOURCE, params);
    },
    getSupportTaskTypes (params?: any) {
        return http.post(req.GET_SUPPORT_TASK_TYPES, params);
    }
}
