import req from '../consts/reqDataSource';
import http from './http';

export default {
    checkDataSourcePermission (params?: any) {
        return http.post(req.CHECK_IS_PERMISSION, params)
    },
    getDataSourceTypes (params?: any) {
        return http.post(req.GET_DATASOURCE_TYPES, params)
    },
    streamSaveDataSource (params: any) {
        return http.post(req.STREAM_SAVE_DATA_SOURCE, params)
    },
    streamSaveDataSourceWithKerberos (params: any) {
        return http.postAsFormData(req.STREAM_SAVE_DATA_SOURCE_KERBEROS, params)
    },
    streamTestDataSourceConnection (params: any) {
        return http.post(req.STREAM_TEST_DATA_SOURCE_CONNECTION, params)
    },
    streamTestDataSourceConnectionWithKerberos (params: any) {
        return http.postAsFormData(req.STREAM_TEST_DATA_SOURCE_CONNECTION_KERBEROS, params)
    },
    streamDeleteDataSource (params: any) {
        return http.post(req.STREAM_DELETE_DATA_SOURCE, params)
    },
    getStreamTablelist (params: any) {
        return http.post(req.GET_STREAM_TABLELIST, params)
    },
    checkSourceIsValid (params: any) {
        return http.post(req.CHECK_SOURCE_IS_VALID, params)
    },
    getStreamDataSourceList (params?: any) {
        return http.post(req.GET_STREAM_DATASOURCE_LIST, params)
    },
    streamQueryDataSource (params: any) {
        return http.post(req.STREAM_QUERY_DATA_SOURCE, params)
    }
}
