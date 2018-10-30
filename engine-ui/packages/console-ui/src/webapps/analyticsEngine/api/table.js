
import http from './http'
import req from '../consts/reqUrls'

export default {

    getCreateSQL(params) {
        return http.post(req.GET_CREATE_SQL, params);
    },

    createTable(params) {
        return http.post(req.CREATE_TABLE, params);
    },

    dropTable(params) {
        return http.post(req.DROP_TABLE, params);
    },

    getTablesByDB(params) {
        return http.post(req.GET_TABLE_LIST_BY_DB, params);
    },

    getTableById(params) { // 暂缺
        return http.post(req.GET_TABLE_BY_ID, params);
    },

    searchTable(params) { // 暂缺
        return http.post(req.SEARCH_TABLES_BY_NAME, params);
    },

    saveTableInfo(params){
        return http.post(req.SAVE_TABLE_INFO, params)
    },

    createTableByDDL(params){
        return http.post(req.CREATE_TABLE_BY_DDL,params)
    },

    getTablePartiton(params){
        return http.post(req.PARTITIONS_INFO, params)
    },
    getPreviewData(params){
        return http.post(req.PREVIEW_DATA,params)
    }
}