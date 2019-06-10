import http from './http'
import req from '../consts/reqUrls'

export default {

    getTable (params) {
        return http.post(req.GET_TABLE, params)
    },
    checkTablePartition (params) {
        return http.post(req.CHECK_TABLE_PARTITION, params)
    },
    createTableByDDL (params) {
        return http.post(req.TABLE_CREATE_BY_DDL, params)
    },
    importLocalData (params) { // 导入本地数据
        return http.postAsFormData(req.UPLOAD_TABLE_DATA, params)
    },
    getTablesByName (params) {
        return http.post(req.GET_TABLES_BY_NAME, params)
    }
}
