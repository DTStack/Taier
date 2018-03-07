import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getDataSources(params) {
        return http.post(req.GET_DATA_SOURCES, params);
    },
    getDataSourcesType(params) {
    	return http.post(req.GET_DATA_SOURCES_TYPE, params);
    },
    getDataSourcesTable(params) {
    	return http.post(req.GET_DATA_SOURCES_TABLE, params);
    },
    getDataSourcesColumn(params) {
    	return http.post(req.GET_DATA_SOURCES_COLUMN, params);
    },
    getDataSourcesPart(params) {
        return http.post(req.GET_DATA_SOURCES_PART, params);
    },
    getDataSourcesPreview(params) {
    	return http.post(req.GET_DATA_SOURCES_PREVIEW, params);
    },

}