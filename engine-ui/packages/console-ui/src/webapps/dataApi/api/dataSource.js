import http from './http'
import req from '../consts/reqUrls'

export default {

    getDataSources (params) {
        return http.post(req.GET_DATA_SOURCES, params);
    },
    testDSConnection (params) {
        return http.post(req.CHECK_CONNECTION, params);
    },
    addDataSource (params) {
        return http.post(req.ADD_DATA_SOURCES, params);
    },
    updateDataSource (params) {
        return http.post(req.UPDATE_DATA_SOURCES, params);
    },
    deleteDataSource (params) {
        return http.post(req.DELETE_DATA_SOURCES, params);
    },
    getDataSourcesType (params) {
        return http.post(req.GET_DATA_SOURCES_TYPE, params);
    },
    getDataSourcesList (params) {
        return http.post(req.GET_DATA_SOURCES_LIST, params);
    },
    getDataSourcesTable (params) {
        return http.post(req.GET_DATA_SOURCES_TABLE, params);
    },
    getDataSourcesColumn (params) {
        return http.post(req.GET_DATA_SOURCES_COLUMN, params);
    },
    getDataSourcesPart (params) {
        return http.post(req.GET_DATA_SOURCES_PART, params);
    },
    getDataSourcesPreview (params) {
        return http.post(req.GET_DATA_SOURCES_PREVIEW, params);
    }

}
