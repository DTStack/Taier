import http from './http'
import req from '../consts/reqUrls'

export default {

    loadCatalogue(params) {
        return http.post(req.GET_CATALOGUES, params);
    },

    createOrUpdateDB(params) {
        return http.post(req.CREATE_OR_UPDATE_DB, params);
    },

    getCreateSQL(params) {
        return http.post(req.GET_CREATE_SQL, params);
    },

    deleteDB(params) {
        return http.post(req.CREATE_OR_UPDATE_DB, params);
    },

    searchDBUsers(params) {
        return http.post(req.CREATE_OR_UPDATE_DB, params);
    },
}