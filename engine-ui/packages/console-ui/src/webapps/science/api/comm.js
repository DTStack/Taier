import http from './http'
import req from '../consts/reqUrls'

export default {

    execTask (params) {
        return http.post(req.EXEC_SQL, params);
    },
    pollTask (params) {
        return http.post(req.POLL_SQL, params);
    },
    stopExecSQL (params) {
        return http.post(req.STOP_EXEC_SQL, params);
    },
    formatSQL (params) {
        return http.post(req.FORMAT_SQL, params);
    },
    getSQLResultData (params) {
        return http.post(req.GET_SQL_RESULT, params);
    },
    getSysParams (params) {
        return http.post(req.GET_SYS_PARAMS, params);
    },
    getAllProject (params) {
        return http.post(req.GET_ALL_PROJECTS, params);
    },
    getProjectList (params) {
        return http.post(req.GET_PROJECT_LIST, params);
    },
    createProject (params) {
        return http.post(req.CREATE_PROJECT, params);
    },
    updateProject (params) {
        return http.post(req.UPDATE_PROJECT, params);
    },
    getTopProject (params) {
        return http.post(req.GET_TOP_PROJECT, params);
    },
    getAllJobStatus (params) {
        return http.post(req.GET_ALL_JOB_STATUS, params);
    },
    getProjectDetail (params) {
        return http.post(req.GET_PROJECT_DETAIL, params);
    },
    getProjectJobStatus (params) {
        return http.post(req.GET_JOB_STATUS, params);
    },
    getProjectJobGraph (params) {
        return http.post(req.GET_JOB_GRAPH, params);
    },
    queryTask (params) {
        return http.post(req.QUERY_TASK, params);
    },
    frozenTask (params) {
        return http.post(req.FROZEN_TASK, params);
    }
}
