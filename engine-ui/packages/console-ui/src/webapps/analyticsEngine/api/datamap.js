import http from './http'
import req from '../consts/reqUrls'

export default {

    getDataMapsByTable (params) {
        return http.post(req.GET_DATAMAP_BY_TABLE, params);
    },

    getDataMapDetail (params) {
        return http.post(req.GET_DATAMAP_DETAIL, params);
    },

    deleteDataMap (params) {
        return http.post(req.DROP_DATAMAP, params);
    },

    createDataMap (params) {
        return http.post(req.CREATE_DATA_MAP, params);
    }
}
