import http from './http'
import req from '../consts/reqUrls'

export default {

    getDataSources (params: any) {
        return http.post(req.GET_DATA_SOURCES, params);
    },
    testDSConnection (params: any) {
        return http.post(req.CHECK_CONNECTION, params);
    },
    addDataSource (params: any) {
        return http.post(req.ADD_DATA_SOURCES, params);
    },
    updateDataSource (params: any) {
        return http.post(req.UPDATE_DATA_SOURCES, params);
    },
    deleteDataSource (params: any) {
        return http.post(req.DELETE_DATA_SOURCES, params);
    },
    getDataSourcesType (params: any) {
        return http.post(req.GET_DATA_SOURCES_TYPE, params);
    },
    getDataSourcesCharType (params: any) {
        return http.post(req.GET_DATA_SOURCES_CHAR_TYPE, params);
    },
    getDataSourcesList (params: any) {
        return http.post(req.GET_DATA_SOURCES_LIST, params);
    },
    getDataSourcesTable (params: any) {
        return http.post(req.GET_DATA_SOURCES_TABLE, params);
    },
    getDataSourcesColumn (params: any) {
        return http.post(req.GET_DATA_SOURCES_COLUMN, params);
    },
    getDataSourcesPart (params: any) {
        return http.post(req.GET_DATA_SOURCES_PART, params);
    },
    getDataSourcesPreview (params: any) {
        return http.post(req.GET_DATA_SOURCES_PREVIEW, params);
    },
    checkDataSourcePermission (params?: any) {
        return http.post(req.CHECK_DATASOURCE_PERMISSION, params);
    }

}
