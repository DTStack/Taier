import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {
    getCatalogue (params) {
        return http.post(req.GET_CATALOGUE, params);
    },
    listByCondition (params) {
        return http.post(req.GET_API_MARKET_LIST, params);
    },
    getApiDetail (params) {
        return http.post(req.GET_MARKET_API_DETAIL, params);
    },
    apiApply (params) {
        return http.post(req.APPLY_API, params);
    },
    getApiExtInfoForNormal (params) {
        return http.post(req.GET_API_EXT_INFO, params);
    },
    getApiExtInfoForManager (params) {
        return http.post(req.GET_API_EXT_INFO_ADMIN, params);
    }

}
