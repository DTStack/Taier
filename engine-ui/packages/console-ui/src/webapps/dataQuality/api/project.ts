// import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {
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
    getProjectDetail (params: any) {
        return http.post(req.GET_PROJECT_DETAIL, params);
    },
    getAllJobStatus (params?: any) {
        return http.post(req.GET_ALL_JOB_STATUS, params);
    }
}
