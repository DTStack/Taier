import http from './http'
import req from '../consts/reqUrls'

export default {

    getDataMapsByTable (params: any) {
        return http.post(req.GET_DATAMAP_BY_TABLE, params);
    },

    getDataMapDetail (params: any) {
        return http.post(req.GET_DATAMAP_DETAIL, params);
    },

    deleteDataMap (params: any) {
        return http.post(req.DROP_DATAMAP, params);
    },

    createDataMap (params: any) {
        return http.post(req.CREATE_DATA_MAP, params);
    },
    checkDataMapStatus (params: any) {
        return http.post(req.CHECK_DATAMAP_STATUS, params);
    },
    getTableColumns (params: any) {
        return http.post(req.GET_TABLE_COLUMNS, params);
    }
}
