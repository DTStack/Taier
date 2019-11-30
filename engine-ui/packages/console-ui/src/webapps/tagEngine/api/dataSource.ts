import req from '../consts/reqDataSource';
import http from './http';

export default {
    tagCreateDataSource (params?: any) {
        return http.post(req.TAG_CREATE_DATA_SOURCE, params)
    },
    tagUpdateeDataSource (params: any) {
        return http.post(req.TAG_UPDATE_DATA_SOURCE, params)
    },
    tagTestDSConnect (params: any) {
        return http.post(req.TAG_TEST_DATA_SOURCE_CONNECTION, params)
    },
    tagDeleteDataSource (params: any) {
        return http.post(req.TAG_DELETE_DATA_SOURCE, params)
    },
    getTagDataSourceList (params?: any) {
        return http.post(req.GET_TAG_DATASOURCE_LIST, params)
    },
    getDataSourceTypes (params?: any) {
        return http.post(req.GET_DATASOURCE_TYPES, params)
    },
    selectDataSource (params?: any) { // 获取数据源下拉
        return http.post(req.SELECT_DATASOURCE, params)
    }
}
