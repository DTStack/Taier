
import http from './http'
import req from '../consts/reqUrls'

export default {

    getCreateSQL(params) {
        return http.post(req.GET_CREATE_SQL, params);
    },

    createTable(params) {
        return http.post(req.CREATE_TABLE, params);
    },

    getTablesByDB(params) {
        return http.post(req.GET_TABLE_LIST_BY_DB, params);
    },
}