
import http from './http'
import req from '../consts/reqUrls'

export default {
    queryProjects (params: any) {
        return http.post(req.QUERY_PROJECT_LIST, params)
    },
    getProjects (params: any) { // 获取项目
        return http.post(req.GET_PROJECT_LIST, params)
    },
    getAllProjects (params: any) { // 获取所有项目
        return http.post(req.GET_ALL_PROJECTS, params);
    },
    removeProjectUser (params: any) {
        return http.post(req.REMOVE_USER_FROM_PROJECT, params)
    },
    addProjectUser (params: any) {
        return http.post(req.ADD_PROJECT_USER, params)
    },
    getUserProjects (params: any) {
        return http.post(req.GET_USRE_PROJECTS, params)
    },
    getProjectUsers (params: any) {
        return http.post(req.GET_PROJECT_USERS, params)
    },
    getProjectByID (params: any) {
        return http.post(req.GET_PROJECT_BY_ID, params)
    },
    upateProjectInfo (params: any) {
        return http.post(req.UPDATE_PROJECT_INFO, params)
    },
    createProject (params: any) {
        return http.post(req.CREATE_PROJECT, params)
    },
    getProjectInfo (params: any) {
        return http.post(req.GET_PROJECT_INFO, params)
    },
    getProjectListInfo (params?: any) {
        return http.post(req.GET_PROJECT_LIST_INFO, params)
    }
}