import utils from 'utils'
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
        return http.post(req.ADD_DATA_SOURCE, params);
    },
    updateDataSource (params) {
        return http.post(req.UPDATE_DATA_SOURCE, params);
    },
    deleteDataSource (params) {
        return http.post(req.DELETE_DATA_SOURCE, params);
    },
    getDataSourcesType (params) {
    	return http.post(req.GET_DATA_SOURCES_TYPE, params);
    },
    getDataSourcesList (params) {
        return http.post(req.GET_DATA_SOURCES_LIST, params);
    },
    getTagDataSourcesList (params) {
        return http.post(req.GET_TAG_DATA_SOURCES_LIST, params);
    },
    getDataSourcesTable (params) {
    	return http.post(req.GET_DATA_SOURCES_TABLE, params);
    },
    getDataSourcesColumn (params) {
    	return http.post(req.GET_DATA_SOURCES_COLUMN, params);
    },
    getDataSourcesPreview (params) {
    	return http.post(req.GET_DATA_SOURCES_PREVIEW, params);
    }

}
