import utils from 'utils'
import http from './http'
import localDb from 'utils/localDb'
import req from '../consts/reqUrls'

export default {

    // =========== datasource数据源模块 ==================//
    addOrUpdateSource(source) {
        return http.post(offlineReq.SAVE_DATA_SOURCE, source)
    },

    deleteDataSource(params) {
        return http.post(offlineReq.DELETE_DATA_SOURCE, params)
    },
    queryDataSource(params) {
        return http.post(offlineReq.QUERY_DATA_SOURCE, params)
    },

    getDataSourceById(params) {
        return http.post(offlineReq.GET_DATA_SOURCE_BY_ID, params)
    },
    testDSConnection(params) {
        return http.post(offlineReq.TEST_DATA_SOURCE_CONNECTION, params)
    },
    getDataSourceTypes(params) {
        return http.post(offlineReq.GET_DATA_SOURCE_TYPES, params)
    },


}