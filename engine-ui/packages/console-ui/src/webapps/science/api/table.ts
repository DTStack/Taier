import http from './http'
import req from '../consts/reqUrls'

export default {

    getTable (params: any) {
        return http.post(req.GET_TABLE, params)
    },
    checkTablePartition (params: any) {
        return http.post(req.CHECK_TABLE_PARTITION, params)
    },
    createTableByDDL (params: any) {
        return http.post(req.TABLE_CREATE_BY_DDL, params)
    },
    importLocalData (params: any) { // 导入本地数据
        return http.postAsFormData(req.UPLOAD_TABLE_DATA, params)
    },
    getTablesByName (params: any) {
        return http.post(req.GET_TABLES_BY_NAME, params)
    }
}
