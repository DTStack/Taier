import UserAPI from 'main/api/user';

import req from './req';
import http from './http';

export default {

    // ========== User ========== //
    logout () { // 注销退出
        UserAPI.logout();
    },

    addRoleUser (user: any) {
        return http.post(req.ADD_ROLE_USRE, user)
    },
    getLoginedUser () {
        return http.post(req.GET_USER_BY_ID)
    },
    updateUserRole (user: any) {
        return http.post(req.UPDATE_USER_ROLE, user)
    },
    getNotProjectUsers (params: any) {
        return http.post(req.GET_NOT_PROJECT_USERS, params)
    },

    searchUICUsers (params: any) {
        return http.post(req.SEARCH_UIC_USERS, params)
    },

    // ========== Project ========== //
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
    getProjectListInfo (params: any) {
        return http.post(req.GET_PROJECT_LIST_INFO, params)
    },
    setSticky (params: any) {
        return http.post(req.SET_STICKY, params)
    },
    deleteProject (params: any) {
        return http.post(req.DELETE_PROJECT, params)
    },
    updateProjectSchedule (params: any) {
        return http.post(req.UPDATE_PROJECT_SCHEDULE, params)
    },
    bindProductionProject (params: any) {
        return http.post(req.BIND_PRODUCTION_PROJECT, params)
    },
    getBindingProjectList (params: any) {
        return http.post(req.GET_COULD_BINDING_PROJECT_LIST, params)
    },
    // ========== Role ========== //
    getRoleList (params: any) {
        return http.post(req.GET_ROLE_LIST, params)
    },
    updateRole (params: any) {
        return http.post(req.UPDATE_ROLE, params)
    },
    deleteRole (params: any) {
        return http.post(req.DELETE_ROLE, params)
    },
    getRoleTree (params?: any) {
        return http.post(req.GET_ROLE_TREE, params)
    },
    getRoleInfo (params: any) {
        return http.post(req.GET_ROLE_INFO, params)
    }
}
