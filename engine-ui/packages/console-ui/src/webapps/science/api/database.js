import http from './http'
import req from '../consts/reqUrls'

export default {

    loadCatalogue (params) {
        return http.post(req.GET_CATALOGUES, params);
    },

    createDB (params) {
        return http.post(req.CREATE_DB, params);
    },

    deleteDB (params) {
        return http.post(req.DROP_DB, params);
    },

    resetDBPassword (params) {
        return http.post(req.MODIFY_DB_PASSWORD, params);
    },

    getDatabases (params) {
        return http.post(req.GET_DB_LIST, params);
    },

    getDBDetail (params) {
        return http.post(req.GET_DB_DETAIL, params);
    },

    getDBUsers (params) {
        return http.post(req.GET_DB_USER_LIST, params);
    },

    getDBUserRoles (params) {
        return http.post(req.GET_DB_USER_ROLE_LIST, params);
    },

    searchUsersNotInDB (params) {
        return http.post(req.GET_USERS_NOT_IN_DB, params);
    },

    updateDBUserRole (params) {
        return http.post(req.UPDATE_DB_USER_ROLE, params);
    },

    addDBUser (params) {
        return http.post(req.ADD_DB_USER, params);
    },

    removeDBUser (params) {
        return http.post(req.DELETE_DB_USER, params);
    }

}
