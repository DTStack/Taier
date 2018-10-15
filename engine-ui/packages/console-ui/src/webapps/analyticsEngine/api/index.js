import http from './http'
import req from '../consts/reqUrls'

export default {

    getUserList() {
        return http.post(req.DQ_GET_USER_LIST)
    },
    getAllDict() {
        return http.post(req.DQ_GET_ALL_DICT)
    },

    loadCatalogue(params) {
        return http.post(req.GET_CATALOGUES, params);
    },

    createOrUpdateDB(params) {
        return http.post(req.CREATE_OR_UPDATE_DB, params);
    },
}