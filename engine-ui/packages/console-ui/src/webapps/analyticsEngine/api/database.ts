import http from './http'
import req from '../consts/reqUrls'

export default {

    loadCatalogue (params: any) {
        return http.post(req.GET_CATALOGUES, params);
    },

    createDB (params: any) {
        return http.post(req.CREATE_DB, params);
    },

    deleteDB (params: any) {
        return http.post(req.DROP_DB, params);
    },

    resetDBPassword (params: any) {
        return http.post(req.MODIFY_DB_PASSWORD, params);
    },

    getDatabases (params: any) {
        return http.post(req.GET_DB_LIST, params);
    },

    getDBDetail (params: any) {
        return http.post(req.GET_DB_DETAIL, params);
    },

    getDBUsers (params: any) {
        return http.post(req.GET_DB_USER_LIST, params);
    },

    getDBUserRoles (params: any) {
        return http.post(req.GET_DB_USER_ROLE_LIST, params);
    },

    searchUsersNotInDB (params: any) {
        return http.post(req.GET_USERS_NOT_IN_DB, params);
    },

    updateDBUserRole (params: any) {
        return http.post(req.UPDATE_DB_USER_ROLE, params);
    },

    addDBUser (params: any) {
        return http.post(req.ADD_DB_USER, params);
    },

    removeDBUser (params: any) {
        return http.post(req.DELETE_DB_USER, params);
    }

}
