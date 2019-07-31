
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
        return http.post(req.ADD_DATA_SOURCE, params);
    },
    updateDataSource (params: any) {
        return http.post(req.UPDATE_DATA_SOURCE, params);
    },
    deleteDataSource (params: any) {
        return http.post(req.DELETE_DATA_SOURCE, params);
    },
    getDataSourcesType (params: any) {
        return http.post(req.GET_DATA_SOURCES_TYPE, params);
    },
    getDataSourcesList (params: any) {
        return http.post(req.GET_DATA_SOURCES_LIST, params);
    },
    getTagDataSourcesList (params: any) {
        return http.post(req.GET_TAG_DATA_SOURCES_LIST, params);
    },
    getDataSourcesTable (params: any) {
        return http.post(req.GET_DATA_SOURCES_TABLE, params);
    },
    getDataSourcesColumn (params: any) {
        return http.post(req.GET_DATA_SOURCES_COLUMN, params);
    },
    getDataSourcesPreview (params: any) {
        return http.post(req.GET_DATA_SOURCES_PREVIEW, params);
    }

}
